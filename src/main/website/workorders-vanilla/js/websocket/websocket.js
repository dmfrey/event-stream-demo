
export default class WorkordersWebSocket {

    constructor() {

        this._dispatch = document.getElementById( 'dispatch' );
        this._eventTypes = [];

        this._socketUrl = null;
        this._ws = null;

        this._registerEventListeners();

        this._offline = true;

        this._openSocket();

    }

    _registerEventListeners() {

        window.addEventListener( 'online', () => this.goOnline() );
        window.addEventListener( 'offline', () => this.goOffline() );

    }

    _openSocket() {

        let workordersWebSocket = this;

        this._socketUrl = new URL('/socket', 'http://localhost:9090' );
        this._socketUrl.protocol = 'ws';

        this._ws = new WebSocket( this._socketUrl.href );

        this._ws.onopen = ( event ) => {
            console.log( `websocket : Opening connection to websocket [${workordersWebSocket._socketUrl.href}]` );
            // console.dir( event );

            workordersWebSocket._offline = false;
            workordersWebSocket._dispatch.dispatchEvent( new CustomEvent( 'WebSocketOnline' ) );

        };

        this._ws.onclose = function( event ) {
            console.log( `websocket : Closing connection to websocket [${workordersWebSocket._socketUrl.href}]` );
            // console.dir( event );

            workordersWebSocket._offline = true;
            workordersWebSocket._dispatch.dispatchEvent( new CustomEvent( 'WebSocketOffline' ) );

            setTimeout(function() {
                workordersWebSocket._openSocket();
            }, 5000 );

        };

        this._ws.onerror = function( error ) {
            console.log( `websocket : Error connection to websocket [${workordersWebSocket._socketUrl.href}]` );
            // console.dir( error );

            workordersWebSocket._offline = true;
            workordersWebSocket._dispatch.dispatchEvent( new CustomEvent( 'WebSocketOffline' ) );

        };

        this._ws.onmessage = function( event ) {
            console.log( `websocket : Received message from websocket [${workordersWebSocket._socketUrl.href}]` );

            if( workordersWebSocket._eventTypes.length == 0 ) return;

            let message = JSON.parse( event.data );
            // console.dir( message );
            // console.dir( workordersWebSocket._eventTypes );

            workordersWebSocket._eventTypes
                .filter( (listener) => message.type === listener )
                .map( listener => workordersWebSocket._dispatch.dispatchEvent( new CustomEvent( listener, {detail: message} ) ) );

        };

    }

    get isConnected() {

        if( null == this._ws || this._offline ) return false;

        return ( this._ws.readyState === WebSocket.OPEN );
    }

    send( message ) {
        console.log( `websocket : Sending message to websocket [${this._socketUrl.href}]` );
        // console.dir( message );

        this._ws.send( JSON.stringify( message ) );

    }

    registerEventType( listener ) {
        console.log( `websocket : Registered event type [${listener}]` );

        this._eventTypes.push( listener );

    }

    goOnline() {

        this._offline = false;
        this._dispatch.dispatchEvent( new CustomEvent( 'WebSocketOnline' ) );

    }

    goOffline() {

        this._offline = true;
        this._dispatch.dispatchEvent( new CustomEvent( 'WebSocketOffline' ) );

    }

}