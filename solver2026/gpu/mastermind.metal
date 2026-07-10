// GPU port of the 1997 m^n Mastermind heuristic engine (mm.java).
// One thread = one full game: random secret, backtracking odometer over
// per-position randomized color permutations, prefix consistency test
// with two-sided pruning. Thread writes its guess count to results[].
#include <metal_stdlib>
using namespace metal;

#define MAXP 16
#define MAXC 24
#define MAXATT 48

struct Params {
    uint colors;
    uint positions;
    uint runs;      // total games overall
    uint base;      // first game index of this dispatch
    ulong seed;
};

static ulong xrand(thread ulong &s) {
    ulong x = s;
    x ^= x >> 12; x ^= x << 25; x ^= x >> 27;
    s = x;
    return x * 0x2545F4914F6CDD1DUL;
}
static uint rnd(thread ulong &s, uint bound) { return uint(xrand(s) % ulong(bound)); }

kernel void play(constant Params &P [[buffer(0)]],
                 device uint *results [[buffer(1)]],
                 uint tid [[thread_position_in_grid]])
{
    uint gid = P.base + tid;
    if (gid >= P.runs) return;
    const uint pos = P.positions, col = P.colors;
    ulong rng = P.seed ^ (ulong(gid) * 0x9E3779B97F4A7C15UL + 0x1234567UL);
    xrand(rng); xrand(rng);

    uchar value[MAXP][MAXC];
    uchar counter[MAXP];
    uchar guessHist[MAXATT][MAXP];
    uchar rBlack[MAXATT], rTotal[MAXATT];
    uchar secret[MAXP];
    char  tmp[MAXP];

    for (uint i = 0; i < pos; i++) {
        secret[i] = uchar(rnd(rng, col));
        counter[i] = 0;
        for (uint j = 0; j < col; j++) value[i][j] = uchar(j);
        for (uint j = col - 1; j > 0; j--) {          // Fisher-Yates
            uint k = rnd(rng, j + 1);
            uchar t = value[i][j]; value[i][j] = value[i][k]; value[i][k] = t;
        }
    }

    uint attempt = 0;
    for (;;) {
        // ---- get_guess: odometer + backtracking to first consistent code
        for (;;) {
            // build candidate and test against history (port of test())
            uint p = pos;
            uchar cand[MAXP];
            for (uint i = 0; i < pos; i++) cand[i] = value[i][counter[i]];

            for (uint g = 0; g < attempt; g++) {
                int black = 0;
                for (uint i = 0; i < p; i++) {
                    if (cand[i] == guessHist[g][i]) {
                        black++;
                        if (black > int(rBlack[g])) p = i;
                    } else {
                        if (int(pos - i - 1) < int(rBlack[g]) - black) p = i;
                    }
                }
                int total = 0;
                for (uint i = 0; i < pos; i++) tmp[i] = char(guessHist[g][i]);
                for (uint i = 0; i < p; i++) {
                    uint j;
                    for (j = 0; j < pos; j++)
                        if (char(cand[i]) == tmp[j]) break;
                    if (j < pos) {
                        total++;
                        tmp[j] = -1;
                        if (total > int(rTotal[g])) p = i;
                    } else {
                        if (int(pos - i - 1) < int(rTotal[g]) - total) p = i;
                    }
                }
            }
            if (p >= pos) break;                       // consistent
            counter[p]++;
            for (uint i = p + 1; i < pos; i++) counter[i] = 0;
            while (counter[p] == col) {
                if (p == 0) { results[gid] = 0xFFFF; return; }  // contradiction
                counter[p] = 0;
                p--;
                counter[p]++;
            }
        }

        // ---- grade candidate against secret
        uchar cur[MAXP];
        for (uint i = 0; i < pos; i++) cur[i] = value[i][counter[i]];

        int black = 0, total = 0;
        {
            uchar ca[MAXC] = {0}, cb[MAXC] = {0};
            for (uint i = 0; i < pos; i++) {
                if (cur[i] == secret[i]) black++;
                ca[cur[i]]++; cb[secret[i]]++;
            }
            for (uint c = 0; c < col; c++) total += min(ca[c], cb[c]);
        }

        for (uint i = 0; i < pos; i++) guessHist[attempt][i] = cur[i];
        rBlack[attempt] = uchar(black);
        rTotal[attempt] = uchar(total);
        attempt++;

        if (black == int(pos) || attempt >= MAXATT) {
            results[gid] = attempt;
            return;
        }
        for (uint i = 0; i < pos; i++) counter[i] = 0;  // rescan from start
    }
}
