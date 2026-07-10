/*
 * Modern replication of the 1997 m^n Mastermind heuristic engine.
 *
 * Faithful C port of mm.java (teza/fromnet/mm.java) — the backtracking
 * consistent-guess generator behind the published thesis table:
 *   - candidate built position-by-position through per-position random
 *     color permutations (value[i][]),
 *   - odometer counter[] enumerates candidates in randomized lex order,
 *   - test() checks all previous (guess, black, total) constraints over
 *     a growing prefix with two-sided pruning and returns the earliest
 *     failing position, where the odometer increments and zeroes the rest.
 *
 * Memory is O(positions * colors + attempts * positions) — the full code
 * space is never materialized, which is what let a Pentium 100 with 24 MB
 * reach {12,8} = 429,981,696 codes in 1997.
 *
 * Usage: ./mn <colors> <positions> <runs> [seed]
 *        ./mn sweep            (reproduce the 1997 table, 2-12 x 1-10)
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <time.h>
#include <stdint.h>

#define MAXP 64
#define MAXC 64
#define MAXATT 512

static uint64_t rng_state;
static uint64_t xrand(void) {            /* xorshift64* */
    uint64_t x = rng_state;
    x ^= x >> 12; x ^= x << 25; x ^= x >> 27;
    rng_state = x;
    return x * 0x2545F4914F6CDD1DULL;
}
static int rnd(int min, int max) { return min + (int)(xrand() % (uint64_t)(max - min)); }

typedef struct {
    int positions, colors, attempt;
    int value[MAXP][MAXC];               /* per-position color permutation */
    int counter[MAXP];                   /* odometer over permuted digits  */
    int guess[MAXATT][MAXP];
    int result_black[MAXATT], result_total[MAXATT];
    int secret[MAXP];
    int tmp[MAXP];
} MM;

static void mm_init(MM *m, int pos, int col) {
    m->positions = pos; m->colors = col; m->attempt = 0;
    for (int i = 0; i < pos; i++) {
        m->secret[i] = rnd(0, col);
        m->counter[i] = 0;
        for (int j = 0; j < col; j++) m->value[i][j] = j;
        for (int j = col - 1; j > 0; j--) {         /* Fisher-Yates */
            int k = rnd(0, j + 1);
            int t = m->value[i][j]; m->value[i][j] = m->value[i][k]; m->value[i][k] = t;
        }
    }
}

/* grade candidate a against b: blacks and total color matches */
static void grade(const MM *m, const int *a, const int *b, int *black, int *total) {
    int cb = 0, cnt_a[MAXC] = {0}, cnt_b[MAXC] = {0}, tot = 0;
    for (int i = 0; i < m->positions; i++) {
        if (a[i] == b[i]) cb++;
        cnt_a[a[i]]++; cnt_b[b[i]]++;
    }
    for (int c = 0; c < m->colors; c++) tot += cnt_a[c] < cnt_b[c] ? cnt_a[c] : cnt_b[c];
    *black = cb; *total = tot;
}

/* port of mm.java test(): earliest position where consistency fails */
static int mm_test(MM *m) {
    int p = m->positions;
    int *cand = m->guess[m->attempt];
    for (int i = 0; i < m->positions; i++) cand[i] = m->value[i][m->counter[i]];

    for (int g = 0; g < m->attempt; g++) {
        int black = 0;
        for (int i = 0; i < p; i++) {
            if (cand[i] == m->guess[g][i]) {
                black++;
                if (black > m->result_black[g]) p = i;
            } else {
                if (m->positions - i - 1 < m->result_black[g] - black) p = i;
            }
        }
        int total = 0;
        for (int i = 0; i < m->positions; i++) m->tmp[i] = m->guess[g][i];
        for (int i = 0; i < p; i++) {
            int j;
            for (j = 0; j < m->positions; j++)
                if (cand[i] == m->tmp[j]) break;
            if (j < m->positions) {
                total++;
                m->tmp[j] = -1;
                if (total > m->result_total[g]) p = i;
            } else {
                if (m->positions - i - 1 < m->result_total[g] - total) p = i;
            }
        }
    }
    return p;
}

/* port of get_guess(): odometer with backtracking to first consistent code */
static int mm_next(MM *m) {
    int p;
    while ((p = mm_test(m)) < m->positions) {
        m->counter[p]++;
        for (int i = p + 1; i < m->positions; i++) m->counter[i] = 0;
        while (m->counter[p] == m->colors) {
            if (p == 0) return -1;                   /* no solution possible */
            m->counter[p] = 0;
            p--;
            m->counter[p]++;
        }
    }
    return 0;
}

static int play(int colors, int positions) {
    MM m;
    mm_init(&m, positions, colors);
    for (;;) {
        if (mm_next(&m) < 0) { fprintf(stderr, "contradiction\n"); exit(1); }
        int black, total;
        grade(&m, m.guess[m.attempt], m.secret, &black, &total);
        m.result_black[m.attempt] = black;
        m.result_total[m.attempt] = total;
        m.attempt++;
        if (black == positions) return m.attempt;
        /* start next scan from the beginning of the randomized order */
        memset(m.counter, 0, sizeof(int) * positions);
    }
}

static void run(int colors, int positions, int runs) {
    double sum = 0, sumsq = 0;
    int max = 0;
    int hist[MAXATT] = {0};
    int verbose = getenv("MN_LOG") != NULL;
    int heartbeat = runs > 10 ? runs / 10 : 1;
    struct timespec t0, t1, tn;
    clock_gettime(CLOCK_MONOTONIC, &t0);
    fprintf(stderr, "[mn] start {%d,%d} space=%.3g runs=%d\n",
            colors, positions, pow(colors, positions), runs);
    for (int i = 0; i < runs; i++) {
        int g = play(colors, positions);
        sum += g; sumsq += (double)g * g;
        if (g > max) max = g;
        hist[g < MAXATT ? g : MAXATT - 1]++;
        clock_gettime(CLOCK_MONOTONIC, &tn);
        double el = (tn.tv_sec - t0.tv_sec) + (tn.tv_nsec - t0.tv_nsec) / 1e9;
        if (verbose)
            fprintf(stderr, "[mn] game %d/%d: %d guesses  avg=%.3f  %.1fs elapsed\n",
                    i + 1, runs, g, sum / (i + 1), el);
        else if ((i + 1) % heartbeat == 0)
            fprintf(stderr, "[mn] {%d,%d} %d/%d done  avg=%.3f max=%d  %.1fs elapsed  eta %.0fs\n",
                    colors, positions, i + 1, runs, sum / (i + 1), max, el,
                    el / (i + 1) * (runs - i - 1));
    }
    clock_gettime(CLOCK_MONOTONIC, &t1);
    fprintf(stderr, "[mn] histogram:");
    for (int g = 1; g < MAXATT; g++) if (hist[g]) fprintf(stderr, " %d:%d", g, hist[g]);
    fprintf(stderr, "\n");
    double ms = (t1.tv_sec - t0.tv_sec) * 1e3 + (t1.tv_nsec - t0.tv_nsec) / 1e6;
    double avg = sum / runs;
    double sd = sqrt((sumsq / runs - avg * avg) / runs);
    printf("Colors: %-3d Positions: %-3d Runs: %-6d Average: %-7.3f Std.Dev: %-7.4f Maximum: %-3d Time: %.1f ms\n",
           colors, positions, runs, avg, sd, max, ms);
    fflush(stdout);
}

int main(int argc, char **argv) {
    rng_state = (argc > 4) ? strtoull(argv[4], 0, 10) : (uint64_t)time(0) * 0x9E3779B97F4A7C15ULL + 1;
    if (argc > 1 && !strcmp(argv[1], "sweep")) {
        for (int c = 2; c <= 12; c++)
            for (int p = 1; p <= 10; p++)
                run(c, p, 100);
        return 0;
    }
    if (argc < 4) { fprintf(stderr, "usage: %s <colors> <positions> <runs> [seed] | sweep\n", argv[0]); return 1; }
    run(atoi(argv[1]), atoi(argv[2]), atoi(argv[3]));
    return 0;
}
