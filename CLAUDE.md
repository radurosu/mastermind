# Mastermind — project context

Owner: Radu T. Rosu — **author of the 1997 NCSU Undergraduate Honors Thesis**
"Analysis of the game of Mastermind — the m^n case" (chair: Dr. Donald L. Bitzer,
Dec 10, 1997), cited in the literature as `rosu99`. This repo holds both the
original 1997 work and its 2026 recreation/extension, built with Claude Code.

Live site: https://radurosu.github.io/mastermind/ (GitHub Pages, main branch root)
Repo: https://github.com/radurosu/mastermind (public)
Blog post about the build: https://blog.eloquentix.com/one-prompt-mastermind-and-a-1997-thesis/
(blog source: `~/dev/blog`, 11ty, push to main auto-deploys)

## Layout — provenance matters, keep it separated

**1997 material (do not modify):**
- `teza/` — author's original disk dump: thesis drafts (`REPORT.WPD` = final paper,
  WordPerfect v2.0; `REPORTOL.WPD` = older draft), `teza/DOCS/` = published HTML site
  with figures, `teza/fromnet/` = period code collected from the net — including
  **`mm.java`, the real engine behind the published m^n table** (see below).
- `mastermind/` — 1997 Java applet + GUI + testing rigs (`mmsTester*.java`, output logs).
- `README.md` — owner's, untouched since initial commit.

**2026 material:**
- `index.html` — playable game page, classic Invicta board. Two modes (you break /
  Rosu-'97-heuristic breaks yours), hint button. Faithful JS port of `mmSolver.java`.
  Installable offline PWA: `sw.js` (stale-while-revalidate, so index.html updates
  propagate on the next online load — no cache-version bump needed) +
  `manifest.webmanifest` + `fonts/` (self-hosted woff2; no Google Fonts at runtime)
  + `icons/`. Mobile: peg tray pins to the bottom of the viewport ≤820px.
  Local stats ledger in `localStorage` (key `mm-ledger-v1`), device-only.
- `1997/` — browsable replica of the original thesis site, assembled from `teza/DOCS/`
  with minimal link repairs (offline-save path mangling, `conclusions/conlusion`
  typo aliases). `1997/applet/` = original applet, defunct, note added.
- `fableRetreivedFiles/` — Wayback Machine recovery of the thesis site (independent
  of `teza/`; byte-identical where they overlap — see its README for snapshot log).
- `solver2026/` — modern engines (see below).
- `research/NOTES.md` — research summary + **1997 vs 2026 timing/accuracy tables**.

Git history: owner's original commits are below merge `5045a3f`; everything above
is the 2026 collaboration. `git log --first-parent` splits provenance.

## The algorithms

1. **Rosu 1997 heuristic** (`mmSolver.java`, ported in `index.html`): shuffle full
   code set, submit first code consistent with all previous grades. {6,4}: avg 4.64,
   worst 8 (confirmed over 1M GPU games: 4.6546 ± 0.0009, max 8).
2. **The m^n engine** (`teza/fromnet/mm.java` → `solver2026/mn.c` + `solver2026/gpu/`):
   backtracking odometer over per-position randomized color permutations with
   two-sided prefix pruning. O(p·c) memory, never materializes the code space.
   This produced the thesis table up to {12,8} = 4.3e8 on a Pentium 100.
3. **Most-parts (Kooi 2005)** (`solver2026/mostparts.js`): one-step-ahead, beats the
   heuristic — exact E = 5668/1296 = 4.3735, worst 6 (matches Ville arXiv:1305.1010
   Table 3). Optimal is 4.3403 (Koyama–Lai 1993). NOT wired into the webpage yet.

## solver2026 usage

- `cc -O2 -o solver2026/mn solver2026/mn.c -lm -lpthread`
  `./solver2026/mn <colors> <positions> <runs> [seed]` — threaded (all cores;
  `MN_THREADS=N` override, `MN_LOG=1` per-game logs). Same seed ⇒ identical results
  at any thread count. Heartbeat + histogram on stderr.
- GPU: `cd solver2026/gpu && swiftc -O mn-gpu.swift -o mn-gpu && ./mn-gpu <c> <p> <runs> [seed]`
  Metal, one game/thread, limits c≤24 p≤16. {6,4}: 1.7M games/s on M4.
- `node solver2026/mostparts.js` — exact most-parts evaluation.

## Measured results (M4, July 2026)

| Config | Space | Avg | Max | Notes |
|---|---|---|---|---|
| {6,4} | 1,296 | 4.6546 ± 0.0009 | 8 | 1M GPU games |
| {12,8} | 4.3e8 | 8.9912 ± 0.011 | 13 | 1997 flagship, 683× faster |
| {12,9} | 5.2e9 | 9.89 | 14 | beyond 1997 ceiling |
| {12,10} | 6.2e10 | 10.29 | 13 | |
| {14,10} | 2.9e11 | 11.20 | 14 | |
| {16,10} | 1.1e12 | 11.62 | 14 | 690s single-core |

Guess growth stays near-linear in c and p — thesis curve extrapolates clean.

## Open threads

- **{16,12} run**: ~50–80 min threaded on M4, was killed mid-run; not rerun yet.
- **Cloud GPU / CUDA port**: Metal kernel translates nearly line-for-line; wanted
  for {20,12}+ (owner is interested). {20,15} = 3.3e19 needs algorithmic help,
  not just hardware — straggler games dominate.
- **Most-parts solver not in the webpage** — natural "Fable '26 breaks yours" third
  mode / head-to-head vs the 1997 heuristic.
- Blog post may deserve a follow-up on the m^n engine discovery + GPU replication.

## Conventions

- Never modify `teza/`, `mastermind/`, or owner's `README.md`.
- All measured numbers in NOTES.md are actually measured — never publish estimates
  as results.
- Commits: plain descriptive messages, Co-Authored-By Claude line.
