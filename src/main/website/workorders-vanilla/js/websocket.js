
import websocket from "./websocket/index.js";

export class WorkorderWebSocket extends HTMLElement {

    constructor() {
        super();

    }

    async connectedCallback() {

    }

    static get observedAttributes() {

        return [ 'offline' ];
    }

    async attributeChangedCallback( attrName, oldVal, newVal ) {
        console.log( `WorkorderWebSocket.attributeChangedCallback : attrName[${attrName}], oldVal[${oldVal}], newVal[${newVal}]`)

        if( attrName === 'offline' ) {

            if( newVal === 'true' ) {

                websocket.goOffline();

            } else {

                websocket.goOnline();

            }

        }

    }

    get ws() {

        return websocket;
    }

}

export const registerWebsocket = () => customElements.define('wo-websocket', WorkorderWebSocket );