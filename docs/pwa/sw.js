const CACHE_NAME = 'child-timer-v1';
const ASSETS_TO_CACHE = [
  './',
  './index.html',
  './manifest.json',
  './assets/apple-touch-icon.png',
  './assets/icon-192.png',
  './assets/icon-512.png',
  './assets/ic_app_fox.png',
  './assets/ours.png',
  './assets/hibou.png',
  './assets/lapin.png',
  './assets/renard.png',
  './assets/croco.png',
  './assets/zebre.png',
  './assets/singe.png',
  './assets/elephant.png',
  './assets/girafe.png',
  './assets/ecureuil.png',
  './assets/panda.png',
  './assets/lion.png'
];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      return cache.addAll(ASSETS_TO_CACHE);
    }).then(() => self.skipWaiting())
  );
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) => {
      return Promise.all(
        keys.filter((key) => key !== CACHE_NAME).map((key) => caches.delete(key))
      );
    }).then(() => self.clients.claim())
  );
});

self.addEventListener('fetch', (event) => {
  event.respondWith(
    caches.match(event.request).then((cachedResponse) => {
      return cachedResponse || fetch(event.request);
    })
  );
});
