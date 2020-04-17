
import repository from './repository';
import websocket from './websocket';

export default class WorkorderListItemsLoader {

    constructor() {

        this._refresh = true;

        this._dispatchReceiver = document.getElementById('dispatch' );
        this._registerEventListeners();

        websocket.registerEventType( 'Workorders' );

    }

    _registerEventListeners() {

        let loader = this;

        this._dispatchReceiver.addEventListener( 'Workorders', ( event) => {
            console.log( 'Received message "Workorders"' );
            console.dir( event );

            repository.db
                .then( function( db ) {

                    let tx = db.transaction( 'workorders-store', 'readwrite' );
                    let store = tx.objectStore( 'workorders-store' );

                    event.detail.workorders.map( (workorder) => store.put( workorder) );

                    tx.done;

                    loader._refresh = false;

                    loader.loadWorkorderListItems();

                });


        }, false );

    }

    _loadWorkorderListItem( workorder ) {

        let host = document.createElement('div');
        host.innerHTML = `<div class="wo-list-item ${workorder.state}">${workorder.title} <span>${workorder.workorderId.substr(24)}</span></div>`;

        host.addEventListener( 'click', ( event ) => {
            console.log( 'click received' );

            let container = document.getElementById( 'control-container' );
            container.setAttribute( 'workorderId', workorder.workorderId );

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

                        if( loader._refresh && websocket.isConnected ) {

                            let request = {
                                type: "WorkordersRequest"
                            };
                            websocket.send( request );

                        }

                    }, 5000 )

                });

        });

    }

}