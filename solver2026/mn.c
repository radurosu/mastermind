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
 * Env:   MN_THREADS=N  worker threads (default: hardware cores)
 *        MN_LOG=1      per-game log lines
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <time.h>
#include <stdint.h>
#include <pthread.h>
#include <stdatomic.h>
#include <unistd.h>

#define MAXP 64
#define MAXC 64
#define MAXATT 512

static uint64_t xrand(uint64_t *s) {     /* xorshift64* */
    uint64_t x = *s;
    x ^= x >> 12; x ^= x << 25; x ^= x >> 27;
    *s = x;
    return x * 0x2545F4914F6CDD1DULL;
}
static int rnd(uint64_t *s, int min, int max) { return min + (int)(xrand(s) % (uint64_t)(max - min)); }

typedef struct {
    int positions, colors, attempt;
    uint64_t rng;
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
        m->secret[i] = rnd(&m->rng, 0, col);
        m->counter[i] = 0;
        for (int j = 0; j < col; j++) m->value[i][j] = j;
        for (int j = col - 1; j > 0; j--) {         /* Fisher-Yates */
            int k = rnd(&m->rng, 0, j + 1);
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

static int play(int colors, int positions, uint64_t seed) {
    MM m;
    m.rng = seed;
    xrand(&m.rng); xrand(&m.rng);
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

/* ---- threaded runner: workers pull game indices from an atomic counter ---- */
typedef struct {
    int colors, positions, runs;
    uint64_t seed;
    int verbose;
    _Atomic int next;                    /* next game index to claim  */
    _Atomic int done;                    /* games completed           */
    int *result;                         /* guess count per game      */
    struct timespec t0;
} Job;

static void *worker(void *arg) {
    Job *j = arg;
    for (;;) {
        int i = atomic_fetch_add(&j->next, 1);
        if (i >= j->runs) return NULL;
        int g = play(j->colors, j->positions, j->seed + (uint64_t)i * 0x9E3779B97F4A7C15ULL);
        j->result[i] = g;
        int d = atomic_fetch_add(&j->done, 1) + 1;
        if (j->verbose) {
            struct timespec tn; clock_gettime(CLOCK_MONOTONIC, &tn);
            double el = (tn.tv_sec - j->t0.tv_sec) + (tn.tv_nsec - j->t0.tv_nsec) / 1e9;
            fprintf(stderr, "[mn] game %d/%d: %d guesses  %.1fs elapsed\n", d, j->runs, g, el);
        }
    }
}

static void *reporter(void *arg) {
    Job *j = arg;
    int last = 0;
    for (;;) {
        sleep(5);
        int d = atomic_load(&j->done);
        if (d >= j->runs) return NULL;
        if (d == last) continue;
        last = d;
        struct timespec tn; clock_gettime(CLOCK_MONOTONIC, &tn);
        double el = (tn.tv_sec - j->t0.tv_sec) + (tn.tv_nsec - j->t0.tv_nsec) / 1e9;
        double sum = 0; int max = 0;
        for (int i = 0; i < j->runs; i++)
            if (j->result[i]) { sum += j->result[i]; if (j->result[i] > max) max = j->result[i]; }
        fprintf(stderr, "[mn] {%d,%d} %d/%d done  avg=%.3f max=%d  %.1fs elapsed  eta %.0fs\n",
                j->colors, j->positions, d, j->runs, sum / d, max, el,
                el / d * (j->runs - d));
    }
}

static void run(int colors, int positions, int runs, uint64_t seed) {
    int nthreads = 1;
    const char *tenv = getenv("MN_THREADS");
    if (tenv) nthreads = atoi(tenv);
    else nthreads = (int)sysconf(_SC_NPROCESSORS_ONLN);
    if (nthreads < 1) nthreads = 1;
    if (nthreads > runs) nthreads = runs;

    Job j = { .colors = colors, .positions = positions, .runs = runs,
              .seed = seed, .verbose = getenv("MN_LOG") != NULL };
    atomic_init(&j.next, 0);
    atomic_init(&j.done, 0);
    j.result = calloc(runs, sizeof(int));
    clock_gettime(CLOCK_MONOTONIC, &j.t0);

    fprintf(stderr, "[mn] start {%d,%d} space=%.3g runs=%d threads=%d\n",
            colors, positions, pow(colors, positions), runs, nthreads);

    pthread_t rep, th[256];
    if (nthreads > 256) nthreads = 256;
    pthread_create(&rep, NULL, reporter, &j);
    for (int t = 0; t < nthreads; t++) pthread_create(&th[t], NULL, worker, &j);
    for (int t = 0; t < nthreads; t++) pthread_join(th[t], NULL);

    struct timespec t1; clock_gettime(CLOCK_MONOTONIC, &t1);
    double ms = (t1.tv_sec - j.t0.tv_sec) * 1e3 + (t1.tv_nsec - j.t0.tv_nsec) / 1e6;

    double sum = 0, sumsq = 0;
    int max = 0, hist[MAXATT] = {0};
    for (int i = 0; i < runs; i++) {
        int g = j.result[i];
        sum += g; sumsq += (double)g * g;
        if (g > max) max = g;
        hist[g < MAXATT ? g : MAXATT - 1]++;
    }
    fprintf(stderr, "[mn] histogram:");
    for (int g = 1; g < MAXATT; g++) if (hist[g]) fprintf(stderr, " %d:%d", g, hist[g]);
    fprintf(stderr, "\n");
    double avg = sum / runs;
    double sd = sqrt((sumsq / runs - avg * avg) / runs);
    printf("Colors: %-3d Positions: %-3d Runs: %-6d Average: %-7.3f Std.Dev: %-7.4f Maximum: %-3d Time: %.1f ms  (threads: %d)\n",
           colors, positions, runs, avg, sd, max, ms, nthreads);
    fflush(stdout);
    free(j.result);
    pthread_cancel(rep);
    pthread_join(rep, NULL);
}

int main(int argc, char **argv) {
    uint64_t seed = (argc > 4) ? strtoull(argv[4], 0, 10) : (uint64_t)time(0) * 0x9E3779B97F4A7C15ULL + 1;
    if (argc > 1 && !strcmp(argv[1], "sweep")) {
        for (int c = 2; c <= 12; c++)
            for (int p = 1; p <= 10; p++)
                run(c, p, 100, seed + c * 1000 + p);
        return 0;
    }
    if (argc < 4) { fprintf(stderr, "usage: %s <colors> <positions> <runs> [seed] | sweep\n", argv[0]); return 1; }
    run(atoi(argv[1]), atoi(argv[2]), atoi(argv[3]), seed);
    return 0;
}
