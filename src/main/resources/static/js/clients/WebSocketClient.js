
export default function WebSocketClient() {

    this._ws = null;

}

WebSocketClient.prototype.openConnection = function() {
    console.log( 'WebSocketClient - initialize : enter' );

    let client = this;

    let socketUrl = new URL('/socket', window.location );
    socketUrl.protocol = 'ws';

    client._ws = new WebSocket( socketUrl.href );

    console.log( 'WebSocketClient - initialize : exit' );
};

WebSocketClient.prototype.getClient = function () {
    console.log( 'WebSocketClient - getClient : enter' );

    let client = this;

    console.log( 'WebSocketClient - getClient : exit' );
    return client._ws;
};
