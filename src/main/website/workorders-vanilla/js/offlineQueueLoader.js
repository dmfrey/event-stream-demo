
import repository from './repository/index.js';

export default class OfflineQueueListItemsLoader {

    constructor() {

        this._dispatchReceiver = document.getElementById( 'dispatch' );
        this._registerEventListeners();

        let websocket = document.querySelector(  'wo-websocket' );
        this._ws = websocket.ws;

        this._ws.registerEventType( 'WebSocketOnline' );

    }

    _registerEventListeners() {

        let loader = this;

        this._dispatchReceiver.addEventListener( 'WebSocketOnline', ( event) => {
            console.log( 'Received message "WebSocketOnline"' );
            // console.dir( event );

            repository.db
                .then( async function( db ) {

                    let store = db.transaction( 'offline-queue-store', 'readonly' )
                        .objectStore( 'offline-queue-store' );

                    store.getAll()
                        .then( offlineQueue => {

                            offlineQueue.forEach( queued => {
                                console.log( 'Sending Offline Queued Item' );
                                console.dir( queued );

                                loader._ws.send( queued );

                            });

                        });

                })
                .catch( (error) => console.log( error ) );


        }, false );

    }

    _loadOfflineQueueListItem( queued ) {

        let host = document.createElement('div');
        host.innerHTML = `<div class="offline-list-item">
    <span>${queued.requestId}</span>
    <span>${queued.type}</span>
    <span>${ (typeof queued.workorderId !== 'undefined') ? `${queued.workorderId.substr(24)}` : `` }</span>
</div>`;

        return host;
    }

    async loadOfflineQueueListItems() {
        console.log( 'loadOfflineQueueListItems : enter' );

        let loader = this;

        return await repository.db.then( function( db ) {

            let store = db.transaction( 'offline-queue-store' )
                .objectStore( 'offline-queue-store' );

            return store.getAll()
                .then( function( offlineQueue ) {

                    const map = Array.prototype.map;

                    return map.call( offlineQueue, queued => {
                        console.log( queued );

                        return loader._loadOfflineQueueListItem( queued ) ;
                    });

                });

        });

    }

    async countOfflineQueuedItems() {
        console.log( 'countOfflineQueuedItems : enter' );

        return await repository.db.then( function( db ) {

            let store = db.transaction( 'offline-queue-store' )
                .objectStore( 'offline-queue-store' );

            return store.getAll()
                .then( function( offlineQueue ) {

                    return offlineQueue.length;

                });

        });

    }

}
