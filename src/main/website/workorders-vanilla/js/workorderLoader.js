
import repository from './repository/index.js';
import workorderTemplate from './workorder/workorderView.js'

export default class WorkorderLoader {

    constructor() {

        this._dispatchReceiver = document.getElementById('dispatch' );
        this._registerEventListeners();

        let websocket = document.querySelector( 'wo-websocket' );
        this._ws = websocket.ws;

        let control = document.querySelector( 'wo-control' );
        this._control = control;

        let workorders = document.querySelector( 'wo-workorders' );
        this._workorders = workorders;

        let offlineQueue = document.querySelector( 'wo-offline-queue' );
        this._offlineQueue = offlineQueue;

    }

    _registerEventListeners() {

        let loader = this;

    }

    async _loadWorkorderView( workorder ) {

        let loader = this;

        let host = document.createElement('div');
        host.innerHTML = await workorderTemplate( workorder );

        host.querySelectorAll( 'form' )
            .forEach( form => {

                form.addEventListener( 'submit', (event) => {
                    event.preventDefault();

                    const data = Object.values( event.target ).reduce( (obj,field) => { obj[ field.name ] = field.value; return obj }, {} );
                    // console.dir( data );

                    repository.db.then( async function( db ) {
                        console.log( 'Adding to offline queue' );

                        await db.get( 'workorders-store', data.workorderId )
                            .then( (workorder) => {

                                workorder.state = data._targetState;

                                let tx = db.transaction( 'workorders-store', 'readwrite' );
                                let store = tx.objectStore( 'workorders-store' );

                                store.put( workorder );
                                tx.done;

                                loader._control.setAttribute( 'workorderId', data.workorderId );
                                loader._workorders.setAttribute( 'reload', 'true' );

                            });

                        let tx = db.transaction( 'offline-queue-store', 'readwrite' );
                        let store = tx.objectStore( 'offline-queue-store' );

                        store.put( data )
                            .then( (request) => {
                                console.log( `success result: ${request}` );

                                data.requestId = request;

                            })
                            .catch( (error) => {
                                console.log( `error result: ${error}` );
                            })
                            .finally( () => {

                                if( loader._ws.isConnected ) {

                                    loader._ws.send( data );

                                }

                            });
                        tx.done;

                        loader._offlineQueue.setAttribute( 'reload', 'true' );

                        console.log( 'Added to offline queue!' );
                    })

                })

            });

        return host;
    }

    async loadWorkorder( workorderId ) {
        console.log( 'loadWorkorder : enter' );

        let loader = this;

        return await repository.db.then( function( db ) {

            let store = db.transaction( 'workorders-store' )
                .objectStore( 'workorders-store' );

            return store.get( workorderId )
                .then( async function( workorder ) {

                    return await loader._loadWorkorderView( workorder) ;
                });

        })

    }

}