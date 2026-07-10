# Research notes — Radu T. Rosu, Mastermind (1997)

**Source:** "Analysis of the game of Mastermind — the m^n case," Undergraduate Honors
Thesis report, North Carolina State University, December 10, 1997. Chair: Dr. Donald
L. Bitzer. Recovered from the Wayback Machine (original URL
`www.csc.ncsu.edu/degrees/undergrad/Reports/rtrosu/`, first crawled January 23, 1998).
Raw HTML pages and original Java source are in `/fableRetreivedFiles/`.

Cited in later literature as `rosu99` (e.g. G. Ville, "An Optimal Mastermind (4,7)
Strategy and More Results in the Expected Case," arXiv:1305.1010, and the
JJ/mastermind-paper bibliography).

## The heuristic algorithm (mmSolver.java)

A random consistent-guess strategy:

1. Enumerate the full code set (`colors^positions` codes) and shuffle it once
   (Fisher–Yates, `randomizeCodeList()`).
2. First guess: next code in the shuffled order (effectively random).
3. After each grading, scan forward through the shuffled list and submit the first
   code consistent with every previous (guess, black/white) pair (`verifyGuess()`).
4. Stop when blacks == positions.

Key property: no set of remaining candidates is stored; consistency is re-checked
against guess history on the fly. Memory is O(history), the scan pointer never
moves backward.

### Measured results

- {6,4}: **average 4.64 guesses, worst case 8** — within ~7% of the Koyama–Lai
  optimum of 5625/1296 = 4.34, at a tiny fraction of the compute.
- Tested 2–12 colors × 1–10 positions (code spaces up to 429,981,696). Expected
  guesses grow ~linearly in colors and positions; runtime grows exponentially
  (solve times measured on a Pentium 100 laptop, 24 MB RAM, Java 1.1.4 JIT).

### Verification of the JS port (index.html)

2000 simulated {6,4} games with the ported solver: average 4.646, worst case 7.
Matches the thesis numbers.

## The deep search algorithm (depthSearch.java)

Exhaustive optimal-strategy search built as the baseline: try every code as a guess
at each node, partition remaining codes by grade, recurse, minimize total guesses
over all codes. Used to gauge how close the heuristic gets to optimal on small
{c,p} cases. Conclusion of the thesis: the heuristic is "the algorithm of choice" —
near-optimal results at any (colors, positions) combination.

## 1997 vs 2026 — same algorithm, modern hardware

The thesis published timings ("Table 2: Time in seconds needed to solve 100 codes,"
colors 1–12 × positions 1–8) measured on a Pentium 100 notebook, 24 MB RAM, Java
1.1.4 JIT. The engine behind those numbers is `teza/fromnet/mm.java` — a
backtracking odometer over per-position randomized color permutations with
two-sided prefix pruning (O(p·c) memory; the code space is never materialized).
`solver2026/mn.c` (C, single core) and `solver2026/gpu/` (Metal, one game per
thread) are faithful ports of it, run on an Apple M4 in July 2026.

### Time to solve 100 codes

| Config | Space | 1997 P100 | 2026 M4 CPU (1 core) | 2026 M4 GPU | Speedup |
|---|---|---|---|---|---|
| {6,4} | 1,296 | 0.28 s | ~1 ms | 0.06 ms | ~280× / ~4,700× |
| {8,6} | 262,144 | 6.87 s | 35 ms | 7.2 ms | 196× / 954× |
| {12,8} | 4.3×10⁸ | 1,192 s | 4.6 s | 1.76 s | 261× / 683× |

### Accuracy cross-check (same algorithm, 29 years apart)

| Config | 1997 avg | 2026 avg | 2026 sample |
|---|---|---|---|
| {6,4} | 4.64, max 8 | 4.6546 ± 0.0009, max 8 | 1,000,000 games (GPU) |
| {12,8} | 9.02 (100 games) | 8.9912 ± 0.011 | 10,000 games (GPU) |

In one million {6,4} games the 1997 worst case of 8 was never exceeded (219
games hit 8, zero hit 9).

### Beyond the 1997 ceiling (M4 CPU, single core)

The largest published cell was {12,8} = 429,981,696 codes. New territory:

| Config | Space | Avg guesses | Max | Runs | Time |
|---|---|---|---|---|---|
| {12,9} | 5.2×10⁹ | 9.89 | 14 | 100 | 30 s |
| {12,10} | 6.2×10¹⁰ | 10.29 | 13 | 100 | 127 s |
| {14,10} | 2.9×10¹¹ | 11.20 | 14 | 50 | 214 s |

Guess growth stays near-linear in colors and positions — the thesis curve
extrapolates cleanly two orders of magnitude past where it was measured.

## Files recovered (/fableRetreivedFiles/)

- `index.html`, `intro.htm`, `definitions.htm`, `heuristic.htm`, `deep.htm`,
  `conclusion.htm`, `code.htm`, `technical.htm` — thesis chapters
- `mmCode.java`, `mmResult.java`, `mmSolver.java`, `mmTester.java`,
  `depthSearch.java` — original 1997 Java source
