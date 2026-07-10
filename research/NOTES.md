# Research notes — Radu T. Rosu, Mastermind (1997)

**Source:** "Analysis of the game of Mastermind — the m^n case," Undergraduate Honors
Thesis report, North Carolina State University, December 10, 1997. Chair: Dr. Donald
L. Bitzer. Recovered from the Wayback Machine (original URL
`www.csc.ncsu.edu/degrees/undergrad/Reports/rtrosu/`, first crawled January 23, 1998).
Raw HTML pages and original Java source are in `original/`.

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

## Files recovered (research/original/)

- `index.html`, `intro.htm`, `definitions.htm`, `heuristic.htm`, `deep.htm`,
  `conclusion.htm`, `code.htm`, `technical.htm` — thesis chapters
- `mmCode.java`, `mmResult.java`, `mmSolver.java`, `mmTester.java`,
  `depthSearch.java` — original 1997 Java source
