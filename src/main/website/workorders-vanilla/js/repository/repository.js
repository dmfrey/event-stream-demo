
export default class Repository {

    constructor( openDB ) {

        this._openDB = openDB;
        this._dbPromise = this._openDatabase();

        this._loadDb();

    }

    async _openDatabase() {
        console.log( 'repository : Opening Database' );

        // If the browser doesn't support service worker,
        // we don't care about having a database
        if( !navigator.serviceWorker ) {
            console.log( 'repository : Opening Database exited, serviceWorker not supported' );

            return Promise.resolve();
        }

        return await this._openDB( 'workorders-db', 1, {

            upgrade( db, oldVersion, newVersion, transaction ) {
                console.log( 'openDB - upgrade : enter [oldVersion: ' + oldVersion + ', newVersion: ' + newVersion + ']' );

                switch( newVersion ) {

                    case 1:

                        db.createObjectStore('workorders-store', {
                            keyPath: 'workorderId'
                        });

                        db.createObjectStore('offline-queue-store', {
                            keyPath: 'requestId',
                            autoIncrement: true
                        });

                }

                console.log( 'openDB - upgrade : exit' );
            },

        });

    }

    _loadDb() {

        // this._dbPromise.then( function( db ) {
        //
        //     db.put( 'workorders-store', {
        //             workorderId: '499968db-c6b1-4890-896a-cabb1ec7a12b',
        //             title: 'This is a test work order',
        //             state: 'OPEN'
        //         }
        //     );
        //
        // });
    }

    get db() {

        return this._dbPromise;
    }

}
