// find * | grep \\. | grep -v gitkeep

var cacheName = 'nutria';
var appShellFiles = [
"/css/font-awesome.css",
"/css/bulma.css",
"/css/nutria.css",
"/fonts/fontawesome-webfont.woff",
"/fonts/fontawesome-webfont.svg",
"/fonts/fontawesome-webfont.eot",
"/fonts/fontawesome-webfont.woff2",
"/fonts/FontAwesome.otf",
"/fonts/fontawesome-webfont.ttf",
"/html/nutria.html",
"/img/rendering.svg",
"/img/icon.png",
"/js/sw.js",
"/js/nutria.js",
"/manifest.json",
"/api/me"
];

self.addEventListener('install', (e) => {
  console.log('[Service Worker] Install');
  e.waitUntil(
    caches.open(cacheName).then((cache) => {
          console.log('[Service Worker] Caching all: app shell');
      return cache.addAll(appShellFiles);
    })
  );
});

self.addEventListener('fetch', (e) => {
  e.respondWith(
    caches.match(e.request).then((r) => {
          console.log('[Service Worker] Fetching resource: '+e.request.url);
      return r || fetch(e.request).then((response) => {
                return caches.open(cacheName).then((cache) => {
          console.log('[Service Worker] Caching new resource: '+e.request.url);
          cache.put(e.request, response.clone());
          return response;
        });
      });
    })
  );
});