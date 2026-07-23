// 파밍 관제소 서비스 워커
// 핵심 파일을 캐싱해 오프라인에서도 앱이 열리도록 한다.
// 데이터(캐릭터, 체크리스트 등)는 여기서 다루지 않는다 — 그건 app.js가 localStorage로 관리한다.

var CACHE_NAME = 'farming-control-station-v2';
var CORE_ASSETS = [
  './index.html',
  './style.css',
  './app.js',
  './manifest.json',
  './icons/icon-192.png',
  './icons/icon-512.png',
  './icons/icon-maskable-512.png'
];

self.addEventListener('install', function(event){
  event.waitUntil(
    caches.open(CACHE_NAME).then(function(cache){
      return cache.addAll(CORE_ASSETS);
    }).then(function(){
      return self.skipWaiting();
    })
  );
});

self.addEventListener('activate', function(event){
  event.waitUntil(
    caches.keys().then(function(keys){
      return Promise.all(
        keys.filter(function(key){ return key !== CACHE_NAME; })
            .map(function(key){ return caches.delete(key); })
      );
    }).then(function(){
      return self.clients.claim();
    })
  );
});

// 캐시 우선 전략: 캐시에 있으면 바로 응답, 없으면 네트워크로 가서 받아온 뒤 캐시에 채워둔다.
// (구글 폰트처럼 외부 요청은 실패해도 무시 — 오프라인일 땐 시스템 기본 글꼴로 자연스럽게 대체된다)
self.addEventListener('fetch', function(event){
  if(event.request.method !== 'GET') return;

  event.respondWith(
    caches.match(event.request).then(function(cached){
      if(cached) return cached;

      return fetch(event.request).then(function(response){
        var isSameOrigin = event.request.url.indexOf(self.location.origin) === 0;
        if(isSameOrigin && response && response.status === 200){
          var clone = response.clone();
          caches.open(CACHE_NAME).then(function(cache){
            cache.put(event.request, clone);
          });
        }
        return response;
      }).catch(function(){
        // 오프라인이고 캐시에도 없는 요청(예: 외부 폰트) — 그냥 실패하도록 둔다
      });
    })
  );
});
