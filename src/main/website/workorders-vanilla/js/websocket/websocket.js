
export default class WorkordersWebSocket {

    constructor() {

        this._dispatch = document.getElementById( 'dispatch' );
        this._eventTypes = [];

        this._socketUrl = null;
        this._ws = null;

        this._openSocket();

    }

    _openSocket() {

        let workordersWebSocket = this;

        this._socketUrl = new URL('/socket', 'http://localhost:9090' );
        this._socketUrl.protocol = 'ws';

        this._ws = new WebSocket( this._socketUrl.href );

        this._ws.onopen = ( event ) => {
            console.log( `Opening connection to websocket [${workordersWebSocket._socketUrl.href}]` );
            console.dir( event );

        };

        this._ws.onclose = function( event ) {
            console.log( `Closing connection to websocket [${workordersWebSocket._socketUrl.href}]` );
            console.dir( event );

            setTimeout(function() {
                workordersWebSocket._openSocket();
            }, 5000 );

        };

        this._ws.onerror = function( error ) {
            console.log( `Error connection to websocket [${workordersWebSocket._socketUrl.href}]` );
            console.dir( error );

        };

        this._ws.onmessage = function( event ) {
            console.log( `Received message from websocket [${workordersWebSocket._socketUrl.href}]` );

            if( workordersWebSocket._eventTypes.length == 0 ) return;

            let message = JSON.parse( event.data );
            console.dir( message );
            console.dir( workordersWebSocket._eventTypes );

            workordersWebSocket._eventTypes
                .filter( (listener) => message.type === listener )
                .map( listener => workordersWebSocket._dispatch.dispatchEvent( new CustomEvent( listener, {detail: message} ) ) );

        };

    }

    // get socket() {
    //
    //     return this._ws;
    // }

    get isConnected() {

        if( null == this._ws ) return false;

        return ( this._ws.readyState === WebSocket.OPEN );
    }

    send( message ) {
        console.log( `Sending message to websocket [${this._socketUrl.href}]` );
        console.dir( message );

        this._ws.send( JSON.stringify( message ) );

    }

    registerEventType( listener ) {
        console.log( `Registered event type [${listener}]` );

        this._eventTypes.push( listener );

    }

}