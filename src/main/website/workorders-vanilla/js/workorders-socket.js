
export function WorkordersSocket() {

    let _socketUrl = new URL('/socket', 'http://localhost:9090' );
    _socketUrl.protocol = 'ws';

    let _ws = new WebSocket( _socketUrl.href );
    let _callbacks = {};

    _ws.onopen = function( event ) {
        console.log( 'WorkordersSocket.onopen : opened connection to websocket' );

        dispatch( 'open', {} );

    };

    _ws.onclose = function( event ) {
        console.log( 'WorkordersSocket.onclose : closed connection to websocket' );
        console.dir( event );

        dispatch( 'close', {} );

    };

    _ws.onerror = function( error ) {
        console.log( 'WorkordersSocket.onerror : websocket error occurred' );
        console.dir( error );

        dispatch( 'error', error );

    };

    _ws.onmessage = function( event ) {
        console.log( 'WorkordersSocket.onmessage : websocket message received' );
        console.dir( event );

        let json = JSON.parse( event.data );
        dispatch( json.type, json );

    };

    this.bind = function( event_name, callback ) {
        console.log( `WorkordersSocket.bind [${event_name}] : enter` );

        _callbacks[ event_name ] = _callbacks[ event_name ] || [];
        _callbacks[ event_name ].push( callback );

        console.log( `WorkordersSocket.bind [${event_name}] : exit` );
        return this;
    };

    this.send = function( request ) {
        console.log( `WorkordersSocket.send [${request.type}] : enter` );
        console.dir( request );

        _ws.send( JSON.stringify( request ) );

        console.log( 'WorkordersSocket.send : exit' );
        return this;
    };

    this.socket = function() {

        return this._ws;
    };


    let dispatch = function( event_name, message ) {

        let chain = _callbacks[ event_name ];
        if( typeof chain == 'undefined' ) return;
        for( let i = 0; i < chain.length; i++ ) {
            chain[ i ]( message );
        }

    };

}
