# Files retrieved by Claude Fable 5 — July 9, 2026

Recovery of Radu T. Rosu's 1997 NCSU Undergraduate Honors Thesis site,
**"Analysis of the game of Mastermind — the m^n case"** (chair: Dr. Donald L.
Bitzer, December 10, 1997), from the Internet Archive's Wayback Machine.

The original URL — `http://www.csc.ncsu.edu/degrees/undergrad/Reports/rtrosu/` —
has been dead for roughly two decades. It survives in the literature as the
`rosu99` BibTeX entry (cited e.g. in G. Ville, *An Optimal Mastermind (4,7)
Strategy and More Results in the Expected Case*, arXiv:1305.1010). The citation's
dead link was the recovery key: the Wayback Machine's CDX index was queried for
every capture under the URL prefix, then each file was fetched from its own
earliest snapshot.

## Retrieval log

| File | Snapshot | First crawled |
|---|---|---|
| `index.html` | 19980123141552 | Jan 23, 1998 |
| `intro.htm` | 19980123175244 | Jan 23, 1998 |
| `definitions.htm` | 19980123175322 | Jan 23, 1998 |
| `heuristic.htm` | 19980123175402 | Jan 23, 1998 |
| `deep.htm` | 19980123175442 | Jan 23, 1998 |
| `conclusion.htm` | 19980123175521 | Jan 23, 1998 |
| `code.htm` | 19980123175600 | Jan 23, 1998 |
| `technical.htm` | 19980123175640 | Jan 23, 1998 |
| `heuristic_2003.htm` | 20030218014634 | later capture, kept for comparison |
| `mmCode.java` | 20021110201202 | Java source, crawled 2002 |
| `mmResult.java` | 20020628055248 | Java source, crawled 2002 |
| `mmSolver.java` | 20020628054946 | Java source, crawled 2002 |
| `mmTester.java` | 20020628055438 | Java source, crawled 2002 |
| `depthSearch.java` | 20021110201202 | Java source, crawled 2002 |

`references.htm` and `dedication.htm` were already 404 by the earliest capture;
the retrieved files record that state. The figures (`Fig1–3.gif`) were never
crawled — they survive only in this repo's own `teza/DOCS/`.

## Verification

After retrieval, this repo turned out to already contain the author's own disk
dump of the thesis (`teza/`). Cross-check: `mmSolver.java` is **byte-identical**
(same MD5) between the author's copy, `teza/solver/`, `teza/DOCS/`, and this
Wayback recovery — two independent lines of preservation, 29 years apart,
in perfect agreement.

Retrieved July 9, 2026 by Claude Fable 5 during the one-prompt build described at
https://blog.eloquentix.com/one-prompt-mastermind-and-a-1997-thesis/
