
// import db repository
import WebDb from "./respository/WebDb.js";

// import web clients
import WebSocketClient from "./clients/WebSocketClient.js";

// import ToastsView from './views/Toasts.js';
import WorkordersListView from './views/WorkordersList.js';
import WorkorderDetailsView from './views/WorkordersDetails.js';

export default function WorkordersController( container ) {
    console.log( 'WorkordersController - initialize : enter' );

    this._container = container;
    this._workordersListView = new WorkordersListView( this._container.querySelector( '#workorders-list-container' ) );
    this._workorderDetailsView = new WorkorderDetailsView( this._container.querySelector( '#workorder-details-container' ) );
    this._lostConnectionToast = null;

    this._ws = null;

    let webDb = new WebDb();
    this._dbPromise = webDb.getDbPromise();

    this._registerServiceWorker();
    this._cleanImageCache();

    let workordersController = this;

    this._showCachedWorkorders()
        .finally( function() {

           workordersController._openSocket();

        });

    console.log( 'WorkordersController - initialize : exit' );
}

// Service Worker Lifecycle Events
WorkordersController.prototype._registerServiceWorker = function() {
    console.log( '_registerServiceWorker : enter' );

    if( !navigator.serviceWorker ) {
        console.log( '_registerServiceWorker : exit, serviceWorker not supported.' );

        return;
    }

    let workordersController = this;

    navigator.serviceWorker.register('/service-worker.js' ).then( function( reg ) {
        console.log( '_registerServiceWorker - register : Registering Service Worker.' );

        if( !navigator.serviceWorker.controller ) {
            console.log( 'register : Service Worker registered by other means.' );

            return;
        }

        if( reg.waiting ) {
            console.log( '_registerServiceWorker - register : current state - "WAITING"' );

            workordersController._updateReady( reg.waiting );

            return;
        }

        if( reg.installing ) {
            console.log( '_registerServiceWorker - register : current state - "INSTALLING"' );

            workordersController._trackInstalling( reg.installing );

            return;
        }

        reg.addEventListener('updatefound', function() {

            workordersController._trackInstalling( reg.installing );

        });

    });

    // Ensure refresh is only called once.
    // This works around a bug in "force update on reload".
    let refreshing;
    navigator.serviceWorker.addEventListener('controllerchange', function() {

        if( refreshing ) return;

        window.location.reload();
        refreshing = true;

    });

    console.debug( '_registerServiceWorker : exit' );
};

WorkordersController.prototype._updateReady = function( worker ) {
    console.debug( '_updateReady : enter' );

    // let toast = this._toastsView.show("New version available", {
    //     buttons: ['refresh', 'dismiss']
    // });
    //
    // toast.answer.then(function(answer) {
    //     if (answer != 'refresh') return;
    //     worker.postMessage({action: 'skipWaiting'});
    // });

    console.debug( '_updateReady : exit' );
};

WorkordersController.prototype._trackInstalling = function( worker ) {
    console.debug( '_trackInstalling : enter' );

    let workordersController = this;

    worker.addEventListener( 'statechange', function() {

        if( worker.state === 'installed' ) {

            workordersController._updateReady( worker );

        }

    });

    console.debug( '_trackInstalling : exit' );
};

// Cache Events
WorkordersController.prototype._cleanImageCache = function() {

    return this._dbPromise.then( function( db) {

        if( !db ) return;

        let imagesNeeded = [];

        // var tx = db.transaction('wittrs');
        // return tx.objectStore('wittrs').getAll().then(function(messages) {
        //     messages.forEach(function(message) {
        //         if (message.photo) {
        //             imagesNeeded.push(message.photo);
        //         }
        //         imagesNeeded.push(message.avatar);
        //     });
        //
        //     return caches.open('wittr-content-imgs');
        // }).then(function(cache) {
        //     return cache.keys().then(function(requests) {
        //         requests.forEach(function(request) {
        //             var url = new URL(request.url);
        //             if (!imagesNeeded.includes(url.pathname)) cache.delete(request);
        //         });
        //     });
        // });

    });

};

// WorkordersList
WorkordersController.prototype._showCachedWorkorders = function() {
    console.log( '_showCachedWorkorders : enter' );

    let workordersController = this;

    return this._dbPromise.then( function( db) {

        // if we're already showing posts, eg shift-refresh
        // or the very first load, there's no point fetching
        // posts from IDB
        if( !db /* || indexController._postsView.showingPosts() */ ) return;

        try {

            let store = db.transaction( 'workorders-store' )
                .objectStore( 'workorders-store' );

            return store.getAll()
                .then( function( workorders ) {

                    workordersController._updateWorkordersListView( workorders );

                    console.log( '_showCachedWorkorders : exit, displaying stored workorders' );

                });

        } catch( e ) {
            console.log( e );

            return;
        }

    });

};

// open a connection to the server for live updates
WorkordersController.prototype._openSocket = function() {
    console.log( '_openSocket : enter' );

    let workordersController = this;

    let webSocketClient = new WebSocketClient();
    webSocketClient.openConnection();
    workordersController._ws = webSocketClient.getClient();

    // add listeners
    workordersController._ws.onopen = function( event ) {
        console.log( '_openSocket : opened connection to websocket' );
        if( workordersController._lostConnectionToast ) {
            // workordersController._lostConnectionToast.hide();
        }

        let request = {
          type: "WorkordersRequest"
        };
        workordersController._ws.send( JSON.stringify( request ) );

    };

    workordersController._ws.onmessage = function( event ) {
        requestAnimationFrame(function() {
            workordersController._onSocketMessage( event.data );
        });
    };

    workordersController._ws.onclose = function( event ) {
        console.log( event );

        if( !workordersController._lostConnectionToast ) {
            // workordersController._lostConnectionToast = workordersController._toastsView.show("Unable to connect. Retrying…");
        }

        // try and reconnect in 5 seconds
        setTimeout(function() {
            workordersController._openSocket();
        }, 5000);

    };

    workordersController._ws.onerror = function( error ) {
        console.log( error );

    };

    console.log( '_openSocket : exit' );
};

// called when the web socket sends message data
WorkordersController.prototype._onSocketMessage = function( data ) {
    console.log( '_onSocketMessage : enter' );

    let workordersController = this;

    let response = JSON.parse( data );
    console.dir( response );

    this._dbPromise.then( function( db ) {

        if( !db ) return;

        const tx = db.transaction( 'workorders-store', 'readwrite' );

        if( response.type === 'Workorders' ) {

            response.workorders.forEach( function( workorder ) {

                tx.store.put( workorder );

            });
            tx.done;

            workordersController._updateWorkordersListView( response.workorders );

            console.log( '_onSocketMessage : loaded updated workorders' );
        }

        if( response.type === 'Workorder' ) {

            tx.store.put( response.workorder );
            tx.done;

            workordersController._updateWorkorderDetailsView( response.workorder );

            console.log( '_onSocketMessage : loaded updated workorder' );
        }

    });

    console.log( '_onSocketMessage : exit' );
};

WorkordersController.prototype._updateWorkordersListView = function( workorders ) {
    console.log( '_updateWorkordersListView : enter' );

    let workordersController = this;

    workordersController._workordersListView.addWorkorders( workorders );

    let elements = document.getElementsByClassName( "wo-list-item" );
    Array.from( elements ).forEach( (element) =>
        element.addEventListener( 'click', function( event ) {

            let workorderId = event.target.getAttribute( 'data-workorderId' );
            workordersController._dbPromise.then( function ( db ) {

                let store = db.transaction( 'workorders-store' )
                    .objectStore( 'workorders-store' );

                store.get( workorderId )
                    .then( function( workorder ) {

                        workordersController._updateWorkorderDetailsView( workorder );

                    })
                    .then( function() {

                        let request = {
                            type: 'WorkorderRequest',
                            workorderId: workorderId
                        };
                        workordersController._ws.send( JSON.stringify( request ) );

                    });

            });

        }, false ) );

    console.log( '_updateWorkordersListView : exit' );
};

WorkordersController.prototype._updateWorkorderDetailsView = function( workorder ) {
    console.log( '_updateWorkorderDetailsView : enter' );

    let workordersController = this;

    workordersController._workorderDetailsView.setWorkorder( workorder );

    let elements = workordersController._workorderDetailsView.getContainer().getElementsByClassName( "btn" );

    Array.from( elements ).forEach( (element) =>
        element.addEventListener( 'click', function( event ) {

            let workorderId = event.target.getAttribute( 'data-workorderId' );
            let action = event.target.getAttribute( 'data-action' );

            let request = {
                type: action,
                workorderId: workorderId
            };
            workordersController._ws.send( JSON.stringify( request ) );

        }, false ) );

    document.getElementById( 'transfer-workorder' ).addEventListener( 'submit', function( event ) {
        event.preventDefault();

        let workorderId = document.getElementById( 'transfer_workorderId' ).value;
        let targetNode = document.getElementById( 'transfer_targetNode' ).value;

        let request = {
            type: 'TransferWorkorderRequest',
            workorderId: workorderId,
            targetNode: targetNode
        };
        workordersController._ws.send( JSON.stringify( request ) );

    }, false );

    console.log( '_updateWorkorderDetailsView : exit' );
};

