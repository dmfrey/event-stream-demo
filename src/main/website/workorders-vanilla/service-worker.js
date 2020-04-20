
const staticCacheName = 'wo-static-v1';
const contentImgsCache = 'wo-content-imgs';
const allCaches = [
    staticCacheName,
    contentImgsCache
];

self.addEventListener( 'install', function( event ) {
    console.log( 'install : The service worker is being installed.' );

    event.waitUntil(

        caches.open( staticCacheName ).then( function( cache ) {

            return cache.addAll([
                './',
                './index.html',
                './css/style.css',
                './js/app.js',
                './js/repository/index.js',
                './js/repository/repository.js',
                './js/websocket/index.js',
                './js/websocket/websocket.js',
                './js/workorder/newWorkorderView.js',
                './js/workorder/workorderView.js',
                './js/favorites.js',
                './js/menu.js',
                './js/navigator.js',
                './js/newWorkorderLoader.js',
                './js/offline-queue.js',
                './js/offlineQueueLoader.js',
                './js/router.js',
                './js/websocket.js',
                './js/workorderListItem.js',
                './js/workorderListItemLoader.js',
                './js/workorderLoader.js',
                './js/workorders.js',
                './templates/menu.html',
                './templates/favorites.html',
                './templates/workorders.html',
                './nodes',
                './js/lib/idb/index.js',
                './js/lib/idb/wrap-idb-value.js',
            ]);

        })

    );

});

self.addEventListener( 'activate', function( event ) {
    console.log( 'activate : The service worker is cleaning the caches.' );

    event.waitUntil(

        caches.keys().then( function( cacheNames ) {

            return Promise.all(

                cacheNames.filter( function( cacheName) {

                    return cacheName.startsWith( 'wo-' ) && !allCaches.includes( cacheName );

                }).map( function( cacheName ) {

                    return caches.delete( cacheName );
                })

            );

        })

    );

});

self.addEventListener( 'fetch', function( event ) {
    console.log( 'fetch : The service worker is serving the asset.' );
    console.log( event.request.url );

    let requestUrl = new URL( event.request.url );

    if( requestUrl.origin === location.origin ) {

        if( requestUrl.pathname === '/' ) {

            event.respondWith( caches.match('/index.html' ) );

            return;
        }

    }

    // caches.open( staticCacheName ).then( function( cache ) {
    //
    //     cache.keys().then( key => console.log( key ) );
    //
    //     return cache;
    // });

    event.respondWith(

        caches.match( event.request.url ).then( function( response ) {

            return response || fetch( event.request ).then( (response) => {
                console.log( event.request.url + ' served from network' );
                return response;
            });
        })

    );

});
