
export class Menu extends HTMLElement {

    constructor() {
        super();

        this._dispatchReceiver = document.getElementById('dispatch' );

        let websocket = document.querySelector( 'wo-websocket' );
        this._ws = websocket.ws;

        this._menuItems = null;

        this._control = null;

        this._ws.registerEventType( 'WebSocketOnline' );
        this._ws.registerEventType( 'WebSocketOffline' );

    }

    _registerEventListeners() {

        this._dispatchReceiver.addEventListener( 'WebSocketOnline', ( event) => {
            console.log( 'Received message "WebSocketOnline"' );

            let online = document.getElementById( 'online-status' );

            online.innerHTML = 'Online';
            // online.value = 'on';
            // online.setAttribute( 'checked', 'true' );

        });

        this._dispatchReceiver.addEventListener( 'WebSocketOffline', ( event) => {
            console.log( 'Received message "WebSocketOffline"' );

            let online = document.getElementById( 'online-status' );

            online.innerHTML = 'Offline';
            // online.value = 'off';
            // online.removeAttribute( 'checked' );

        });

    }

    async connectedCallback() {

        const response = await fetch( './templates/menu.html' );
        const template = await response.text();
        this.innerHTML = "";
        const host = document.createElement( 'div' );
        host.innerHTML = template;
        this.appendChild( host );

        this._registerEventListeners();

        this._menuItems = {
            // workorders: ( document.getElementById( "menu-item-workorders" ) ),
            newWorkorder: ( document.getElementById( "menu-item-new-workorder" ) ),
        };
        this._menuItems.newWorkorder.addEventListener( 'click', () => this._control.showNewWorkorder() );

        // host.querySelector( 'input[type="checkbox"]' ).addEventListener( 'click', (event) => {
        //     console.log( 'offline clicked "click"' );
        //     event.preventDefault();
        //
        //
        //     // let websocket = document.querySelector( 'wo-websocket' );
        //     // websocket.setAttribute( 'offline', (event.target.value === 'on' ? 'true' : 'false' ) );
        //
        // });

    }

    static get observedAttributes() {

        return [ 'menu-item' ];
    }

    async attributeChangedCallback( attrName, oldVal, newVal ) {
        console.log( `Menu.attributeChangedCallback : attrName[${attrName}], oldVal[${oldVal}], newVal[${newVal}]`)

        if( attrName === 'menu-item' ) {

            if( oldVal !== newVal ) {

                this._control = /** @type {Navigator} */ ( document.getElementById( "control-container" ) );
                // console.dir( this._control );
            }

        }

        if( attrName === 'menu-item' ) {

        }

    }

}

export const registerMenu = () => customElements.define('wo-menu', Menu );