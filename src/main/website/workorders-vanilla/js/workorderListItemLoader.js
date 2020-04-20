
import repository from './repository/index.js';

export default class WorkorderListItemsLoader {

    constructor() {

        this._refresh = true;

        this._dispatchReceiver = document.getElementById( 'dispatch' );
        this._registerEventListeners();

        let websocket = document.querySelector(  'wo-websocket' );
        this._ws = websocket.ws;

        let offlineQueue = document.querySelector( 'wo-offline-queue' );
        this._offlineQueue = offlineQueue;

        this._ws.registerEventType( 'Workorders' );
        this._ws.registerEventType( 'WorkorderCreated' );
        this._ws.registerEventType( 'Workorder' );

    }

    _registerEventListeners() {

        let loader = this;
        let host = document.querySelector( 'wo-workorders' );
        let control = document.querySelector( 'wo-control' );

        this._dispatchReceiver.addEventListener( 'Workorders', ( event) => {
            console.log( 'Received message "Workorders"' );
            // console.dir( event );

            repository.db
                .then( async function( db ) {

                    let tx = db.transaction( 'workorders-store', 'readwrite' );
                    let store = tx.objectStore( 'workorders-store' );

                    event.detail.workorders.map( (workorder) => store.put( workorder) );

                    tx.done;

                    loader._refresh = false;

                    host.setAttribute( 'reload', 'true' );
                    loader._offlineQueue.setAttribute( 'reload', 'true' );

                });


        }, false );

        this._dispatchReceiver.addEventListener( 'WorkorderCreated', ( event) => {
            console.log( 'Received message "WorkorderCreated"' );
            // console.dir( event );

            repository.db
                .then( async function( db ) {

                    const queue = await db.get( 'offline-queue-store', event.detail.requestId );

                    let tx = db.transaction( 'workorders-store', 'readwrite' );
                    let store = tx.objectStore( 'workorders-store' );

                    store.put( event.detail.workorder );

                    tx.done;

                    db.delete( 'offline-queue-store', queue.requestId );

                    loader._refresh = false;

                    host.setAttribute( 'reload', 'true' );
                    loader._offlineQueue.setAttribute( 'reload', 'true' );

                    let control = document.querySelector( 'wo-control' );
                    control.setAttribute( 'workorderId', event.detail.workorder.workorderId );

                })
                .catch( (error) => console.log( error ) );


        }, false );

        this._dispatchReceiver.addEventListener( 'Workorder', ( event) => {
            console.log( 'Received message "Workorder"' );
            console.dir( event.detail );

            repository.db
                .then( async function( db ) {

                    let tx = db.transaction( 'workorders-store', 'readwrite' );
                    let store = tx.objectStore( 'workorders-store' );

                    store.put( event.detail.workorder );

                    tx.done;

                    loader._refresh = false;

                    host.setAttribute( 'reload', 'true' );

                    control.setAttribute( 'workorderId', event.detail.workorderId );

                    if( typeof event.detail.requestId !== 'undefined' ) {

                        const queue = await db.get( 'offline-queue-store', event.detail.requestId );
                        db.delete( 'offline-queue-store', queue.requestId );

                    }
                    loader._offlineQueue.setAttribute( 'reload', 'true' );


                });


        }, false );

    }

    _loadWorkorderListItem( workorder ) {

        let loader = this;

        let host = document.createElement('div');
        host.innerHTML = `<div class="wo-list-item ${workorder.state}">${workorder.title} <span>${workorder.workorderId.substr(24)}</span></div>`;

        host.addEventListener( 'click', ( event ) => {
            console.log( 'click received' );

            let container = document.getElementById( 'control-container' );
            container.setAttribute( 'workorderId', workorder.workorderId );

            if( loader._ws.isConnected ) {

                let request = {
                    type: "WorkorderRequest",
                    workorderId: workorder.workorderId
                };
                loader._ws.send( request );

            }

        });

        return host;
    }

    async loadWorkorderListItems() {
        console.log( 'loadWorkorderListItems : enter' );

        let loader = this;

        return await repository.db.then( function( db ) {

            let store = db.transaction( 'workorders-store' )
                .objectStore('workorders-store');

            return store.getAll()
                .then( function( workorders ) {

                    const map = Array.prototype.map;

                    return map.call( workorders, workorder => {

                        return loader._loadWorkorderListItem( workorder) ;
                    });

                })
                .finally( () => {

                    setTimeout(() => {

                        if( loader._refresh && loader._ws.isConnected ) {

                            let request = {
                                type: "WorkordersRequest"
                            };
                            loader._ws.send( request );

                        }

                    }, 5000 )

                });

        });

    }

}