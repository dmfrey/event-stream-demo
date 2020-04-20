
import repository from './repository/index.js';
import newWorkorderTemplate from './workorder/newWorkorderView.js'


export default class NewWorkorderLoader {

    constructor() {
        console.log( 'NewWorkorderLoader - initialize : enter' );

        this._dispatchReceiver = document.getElementById('dispatch' );
        this._registerEventListeners();

        let websocket = document.querySelector( 'wo-websocket' );
        this._ws = websocket.ws;

        let offlineQueue = document.querySelector( 'wo-offline-queue' );
        this._offlineQueue = offlineQueue;

        this._ws.registerEventType( 'WorkorderCreated' );

        console.log( 'NewWorkorderLoader - initialize : exit' );
    }

    _registerEventListeners() {

        let loader = this;

        this._dispatchReceiver.addEventListener( 'WorkorderCreated', ( event) => {
            console.log( 'Received message "WorkorderCreated"' );
            // console.dir( event );

            let host = document.getElementById( 'control-container' );
            host.querySelector( 'form' ).reset();

        }, false );


    }

    async _loadTemplateView() {

        let host = document.createElement('div');
        host.innerHTML = await newWorkorderTemplate();

        let form  = host.querySelector( 'form' );
        form.addEventListener( 'submit', (event) => {
            event.preventDefault();
            console.log( 'Creating new workorder' );

            let loader = this;

            const data = Object.values( event.target ).reduce( (obj,field) => { obj[ field.name ] = field.value; return obj }, {} );

            repository.db.then( function( db ) {
                console.log( 'Adding to offline queue' );

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

        });
        // console.dir( form );

        return host;
    }

    async loadNewWorkorder() {
        console.log( 'loadNewWorkorder : enter' );

        return await this._loadTemplateView();
    }

}