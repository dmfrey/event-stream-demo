
const staticCacheName = 'wo-static-v1';
const contentImgsCache = 'wo-content-imgs';
const allCaches = [
    staticCacheName,
    contentImgsCache
];

self.addEventListener('install', function( event) {
    console.log( 'install : The service worker is being installed.' );

    event.waitUntil(

        caches.open( staticCacheName ).then( function( cache ) {

            return cache.addAll([
                './',
                './js/templates/WorkorderDetails.js',
                './js/templates/WorkorderListItem.js',
                './js/utils/loadScripts.js',
                './js/views/WorkordersDetails.js',
                './js/views/WorkordersList.js',
                './js/Fetch.js',
                './js/index.js',
                './js/WorkordersApi.js',
                './js/WorkordersController.js',
                './css/style.css',
                './nodes',
                './webjars/idb/build/esm/index.js',
                './webjars/idb/build/esm/chunk.js',
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

            event.respondWith( caches.match('/' ) );

            return;
        }

        // Always serve /workorders from network
        if( requestUrl.pathname === '/workorders' ) {

            event.respondWith( fetch( event.request ) );

            return;
        }

        // TODO: Intercept and queue DELETE, POST, PUT

        // TODO: Serve TOs from cache

    }

    caches.open( staticCacheName ).then( function( cache ) {

        cache.keys().then( key => console.log( key ) );

        return cache;
    });

    event.respondWith(

        caches.match( event.request.url ).then( function( response ) {

            return response || fetch( event.request ).then( (response) => {
                console.log( event.request.url + ' served from network' );
                return response;
            });
        })

    );

});


//
// const CACHE = 'workorders-cache-v1';
// let workordersDb;
//
// self.addEventListener( 'install', function( event ) {
//     console.log( 'The service worker is being installed.' );
//
//     // Perform install steps
//     event.waitUntil( caches.open( CACHE ).then( function( cache ) {
//         cache.addAll([
//             './',
//             './index.html',
//             './webjars',
//             './css',
//             './js'
//         ]);
//     }));
//
// });
//
// self.addEventListener( 'activate', function( event ) {
//     console.log( 'The service worker is creating the indexedDb.' );
//
//     event.waitUntil(
//         createDb()
//     );
//
// });
//
// self.addEventListener( 'fetch', function( event ) {
//     console.log( 'The service worker is serving the asset.' );
//
//     if( event.request.method !== 'GET' ) {
//
//         event.respondWith( fromNetwork( event.request, 400 ).catch( function( e ) {
//             console.error( 'fetch : error', e );
//         })).then( refresh );
//
//     } else {
//
//         event.respondWith( fromCache( event.request ) );
//
//         event.waitUntil(
//             update( event.request )
//                 .then( refresh )
//         );
//
//     }
//
// });
//
// function createDb() {
//
//     workordersDb = indexedDB.open( 'workorders', 1 );
//
//     workordersDb.onerror = function() {
//         console.error( "Error", workordersDb.error );
//     };
//
//     workordersDb.onsuccess = function( event ) {
//         console.log( "WorkordersDb created successfully" );
//
//         let db = event.target.result;
//
//         const options = {
//             method: 'GET',
//             headers: {
//                 'Content-Type': 'application/json' // we will be sending JSON
//             }
//         };
//
//         fetch('/workorders', options).then( (response) => {
//
//             return response.json()
//
//         }).then( (data) => {
//
//             data.map( wo => {
//
//                 let tx = db.transaction( 'workorders', 'readwrite' );
//                 tx.onsuccess = function( event ) {
//                     console.log( 'tx complete!' );
//                 };
//                 tx.onerror = function( event ) {
//                     console.log( 'tx error!' );
//                 };
//                 tx.onabort = function( event ) {
//                     console.log( 'tx abort!' );
//                 };
//
//                 let store = tx.objectStore( 'workorders' );
//
//                 let request = store.put( wo );
//                 request.onsuccess = function () {
//                     console.log( 'workorder insert succeeded', request.result );
//                 };
//                 request.onerror = function () {
//                     console.error( 'workorder insert failed', request.error );
//                 };
//
//             });
//
//         });
//
//     };
//
//     workordersDb.onupgradeneeded = function( event ) {
//
//         let db = event.target.result;
//         db.createObjectStore('workorders', { keyPath: 'workorderId' } );
//
//     };
//
// }
//
// function fromNetwork( request, timeout ) {
//
//     return new Promise(function( fulfill, reject ) {
//
//         let timeoutId = setTimeout( reject, timeout );
//
//         fetch( request ).then( function( response) {
//
//             clearTimeout( timeoutId );
//             fulfill( response );
//
//         }, reject );
//
//     });
//
// }
//
// function fromCache( request ) {
//     console.log( 'service-worker : fromCache - enter' );
//
//     return caches.open( CACHE ).then( function( cache) {
//
//         return cache.match( request );
//     });
//
// }
//
// function update( request ) {
//     console.log( 'service-worker : update - enter' );
//
//     return caches.open( CACHE ).then( function( cache) {
//
//         return fetch( request ).then( function( response) {
//             console.log( 'service-worker : update - response ' + response );
//
//             return cache.put( request, response.clone() ).then( function() {
//
//                 return response;
//             });
//
//         });
//
//     });
//
// }
//
// function refresh( response ) {
//     console.log( 'service-worker : refresh - enter' );
//
//     return self.clients.matchAll().then( function( clients ) {
//
//         clients.forEach(function( client ) {
//             const message = {
//                 type: 'refresh',
//                 url: response.url,
//                 eTag: response.headers.get( 'ETag' )
//             };
//
//             client.postMessage( message );
//
//         });
//
//     });
//
// }