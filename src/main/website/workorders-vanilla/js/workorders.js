
import WorkorderListItemsLoader from './workorderListItemLoader.js';

export class Workorders extends HTMLElement {

    constructor() {
        super();

        this._loader = new WorkorderListItemsLoader();

    }

    async connectedCallback() {
        console.log( 'Workorders.connectedCallback : enter' );

        let workorderListItems = await this._loader.loadWorkorderListItems();

        this.updateWorkorders( workorderListItems );

        console.log( 'Workorders.connectedCallback : exit' );
    }

    static get observedAttributes() {

        return [ 'reload' ];
    }

    async attributeChangedCallback( attrName, oldVal, newVal ) {
        console.log( `Workorders.attributeChangedCallback : attrName[${attrName}], oldVal[${oldVal}], newVal[${newVal}]`)

        if( attrName === 'reload' ) {

            if( newVal === 'true' ) {

                let workorderListItems = await this._loader.loadWorkorderListItems();

                this.updateWorkorders( workorderListItems );

            }

        }

    }

    refreshWorkorders() {
        console.log( 'Workorders.refreshWorkorders : enter' );

        let request = {
            type: "WorkordersRequest"
        };
        // this._socket.send( request );

        console.log( 'Workorders.refreshWorkorders : exit' );
    }

    async updateWorkorders( workorderListItems ) {
        console.log( "updateWorkorders : enter" );

        const response = await fetch( './templates/workorders.html' );
        const template = await response.text();
        this.innerHTML = "";

        let host = document.createElement( 'div' );
        host.innerHTML = template;

        workorderListItems
            .forEach( function( workorderListItem ) {

                host.querySelector( 'section' ).appendChild( workorderListItem );

            });

        this.appendChild( host );

    }

}

export const registerWorkorders = () => customElements.define('wo-workorders', Workorders );