
import repository from './repository';
import workorderTemplate from './workorder/workorderView.js'

export default class WorkorderLoader {

    constructor() {

        this._dispatchReceiver = document.getElementById('dispatch' );
        this._registerEventListeners();

    }

    _registerEventListeners() {

        let loader = this;

    }

    _loadWorkorderView( workorder ) {

        let host = document.createElement('div');
        host.innerHTML = workorderTemplate( workorder );

        return host;
    }

    async loadWorkorder( workorderId ) {
        console.log( 'loadWorkorder : enter' );

        let loader = this;

        if( null === workorderId ) {

            return this._loadNewWorkorderView();
        }

        return await repository.db.then( function( db ) {

            let store = db.transaction( 'workorders-store' )
                .objectStore('workorders-store');

            return store.get( workorderId )
                .then( function( workorder ) {

                    return loader._loadWorkorderView( workorder) ;
                });

        })

    }

}