import { openDB } from '/webjars/idb/build/esm/index.js';

async function openDatabase() {
    console.log( 'openDatabase : enter' );

    // If the browser doesn't support service worker,
    // we don't care about having a database
    if( !navigator.serviceWorker ) {
        console.log( 'openDatabase : exit, serviceWorker not supported' );

        return Promise.resolve();
    }

    return await openDB( 'workorders-db', 1, {

        upgrade( db, oldVersion, newVersion, transaction ) {
            console.log( 'openDB - upgrade : enter [oldVersion: ' + oldVersion + ', newVersion: ' + newVersion + ']' );

            db.createObjectStore('workorders-store', {
                keyPath: 'workorderId'
            });

            console.log( 'openDB - upgrade : exit' );
        },

    });

}

export default function WebDb() {
    console.log( 'WebDb - initialize : enter' );

    this._dbPromise = openDatabase();

    console.log( 'WebDb - initialize : exit' );
}

WebDb.prototype.getDbPromise = function() {
    console.log( 'WebDb - getDbPromise : enter' );

    console.log( 'WebDb - getDbPromise : exit' );
    return this._dbPromise;
};
