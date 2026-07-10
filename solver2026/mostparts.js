// Beat the 1997 heuristic (4.64 avg on {6,4}).
// One-step-ahead "most parts" strategy (Kooi): at each node pick the guess
// that splits the remaining consistent set into the most nonempty grade
// classes; tie-break to guesses that are themselves possible codes, then
// lexical. Exact evaluation over all 1296 secrets — no sampling.

const COLORS = 6, POS = 4, TOTAL = COLORS ** POS;

const codes = [];
for (let n = 0; n < TOTAL; n++) {
  const c = new Array(POS);
  let x = n;
  for (let j = POS - 1; j >= 0; j--) { c[j] = x % COLORS; x = Math.floor(x / COLORS); }
  codes.push(c);
}

function grade(a, b) {
  let blacks = 0;
  const ca = new Array(COLORS).fill(0), cb = new Array(COLORS).fill(0);
  for (let i = 0; i < POS; i++) {
    if (a[i] === b[i]) blacks++;
    ca[a[i]]++; cb[b[i]]++;
  }
  let match = 0;
  for (let c = 0; c < COLORS; c++) match += Math.min(ca[c], cb[c]);
  return blacks * 5 + (match - blacks); // packed key, blacks<=4, whites<=4
}

// precompute grade table (1296x1296, Int8)
const G = new Int8Array(TOTAL * TOTAL);
for (let i = 0; i < TOTAL; i++)
  for (let j = i; j < TOTAL; j++) {
    const g = grade(codes[i], codes[j]);
    G[i * TOTAL + j] = g; G[j * TOTAL + i] = g;
  }
const WIN = 4 * 5; // (4,0)

function bestGuess(set) {
  let best = -1, bestParts = -1, bestPossible = false;
  const count = new Int16Array(25);
  for (let g = 0; g < TOTAL; g++) {
    count.fill(0);
    const row = g * TOTAL;
    for (const s of set) count[G[row + s]]++;
    let parts = 0;
    for (let k = 0; k < 25; k++) if (count[k]) parts++;
    const possible = count[WIN] > 0;
    if (parts > bestParts || (parts === bestParts && possible && !bestPossible)) {
      bestParts = parts; best = g; bestPossible = possible;
    }
  }
  return best;
}

// L(set) = total guesses to find every code in set
const strategy = new Map(); // path -> guess (for export)
function L(set, path) {
  if (set.length === 1) { strategy.set(path, set[0]); return 1; }
  if (set.length === 2) { strategy.set(path, set[0]); return 3; } // guess one: 1 + 2
  const g = path === '' ? firstGuess : bestGuess(set);
  strategy.set(path, g);
  const parts = new Map();
  const row = g * TOTAL;
  for (const s of set) {
    const k = G[row + s];
    if (!parts.has(k)) parts.set(k, []);
    parts.get(k).push(s);
  }
  let total = set.length; // everyone spends this guess
  let worst = 0;
  for (const [k, p] of parts) {
    if (k === WIN) continue;
    total += L(p, path + '|' + k);
  }
  return total;
}

// depth (worst case) for reporting
function depth(set, path) {
  if (set.length === 1) return 1;
  if (set.length === 2) return 2;
  const g = strategy.get(path);
  const row = g * TOTAL;
  const parts = new Map();
  for (const s of set) {
    const k = G[row + s];
    if (!parts.has(k)) parts.set(k, []);
    parts.get(k).push(s);
  }
  let d = 0;
  for (const [k, p] of parts) {
    if (k === WIN) continue;
    d = Math.max(d, depth(p, path + '|' + k));
  }
  return 1 + d;
}

const firstGuess = codes.findIndex(c => c.join('') === '0012'); // 1123 in 1-based
const all = Array.from({ length: TOTAL }, (_, i) => i);
const t0 = Date.now();
const total = L(all, '');
const avg = total / TOTAL;
const worst = depth(all, '');
console.log(`first guess: ${codes[firstGuess].map(x => x + 1).join('')}`);
console.log(`L = ${total}   E = ${total}/${TOTAL} = ${avg.toFixed(4)}   worst = ${worst}`);
console.log(`Rosu 1997 heuristic: 4.64 avg / 8 worst.  Beat: ${avg < 4.64}`);
console.log(`optimal (Koyama-Lai): 5625/1296 = 4.3403`);
console.log(`computed in ${Date.now() - t0} ms`);
