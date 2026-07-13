/* Offline cache for the Mastermind game (stale-while-revalidate). */
const CACHE = 'mastermind-v1';

const SHELL = [
  './',
  'index.html',
  'manifest.webmanifest',
  'fonts/alfa-slab-one-latin-400.woff2',
  'fonts/ibm-plex-mono-latin-400.woff2',
  'fonts/ibm-plex-mono-latin-600.woff2',
  'fonts/ibm-plex-mono-latin-400i.woff2',
  'icons/icon.svg',
  'icons/icon-192.png',
  'icons/icon-512.png',
  'icons/icon-maskable-512.png',
  'icons/apple-touch-icon.png',
];

self.addEventListener('install', e => {
  e.waitUntil(
    caches.open(CACHE).then(c => c.addAll(SHELL)).then(() => self.skipWaiting())
  );
});

self.addEventListener('activate', e => {
  e.waitUntil(
    caches.keys()
      .then(keys => Promise.all(keys.filter(k => k !== CACHE).map(k => caches.delete(k))))
      .then(() => self.clients.claim())
  );
});

/* Serve from cache instantly (works on a plane), refresh the cache in the
   background when the network is there. Same-origin GET only; anything else
   goes straight through. Pages beyond the shell (e.g. the 1997 thesis site)
   get cached as they are visited. */
self.addEventListener('fetch', e => {
  const req = e.request;
  if (req.method !== 'GET' || new URL(req.url).origin !== location.origin) return;
  e.respondWith(
    caches.open(CACHE).then(cache =>
      cache.match(req, { ignoreSearch: true }).then(cached => {
        const refresh = fetch(req)
          .then(res => {
            if (res && res.ok) cache.put(req, res.clone());
            return res;
          })
          .catch(() => cached);
        return cached || refresh;
      })
    )
  );
});
