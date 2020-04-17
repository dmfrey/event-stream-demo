
export class Menu extends HTMLElement {

    constructor() {
        super();

        this._menuItems = null;

        this._control = null;

    }

    async connectedCallback() {

        const response = await fetch( './templates/menu.html' );
        const template = await response.text();
        this.innerHTML = "";
        const host = document.createElement( 'div' );
        host.innerHTML = template;
        this.appendChild( host );
        this._menuItems = {
            workorders: ( document.getElementById( "menu-item-workorders" ) ),
            newWorkorder: ( document.getElementById( "menu-item-new-workorder" ) ),
        };
        this._menuItems.newWorkorder.addEventListener( 'click', () => this._control.showNewWorkorder() );
    }

    static get observedAttributes() {

        return [ 'menu-item' ];
    }

    async attributeChangedCallback( attrName, oldVal, newVal ) {
        console.log( `Menu.attributeChangedCallback : attrName[${attrName}], oldVal[${oldVal}], newVal[${newVal}]`)

        if( attrName === 'menu-item' ) {

            if( oldVal !== newVal ) {

                this._control = /** @type {Navigator} */ ( document.getElementById( "control-container" ) );
                console.dir( this._control );
            }

        }

    }

}

export const registerMenu = () => customElements.define('wo-menu', Menu );