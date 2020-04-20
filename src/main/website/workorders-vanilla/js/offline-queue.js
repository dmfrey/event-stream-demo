
import OfflineQueueListItemsLoader from './offlineQueueLoader.js';

export class OfflineQueue extends HTMLElement {

    constructor() {
        super();

        this._loader = new OfflineQueueListItemsLoader();

    }

    async connectedCallback() {
        console.log( 'OfflineQueue.connectedCallback : enter' );

        let offlineQueueListItems = await this._loader.loadOfflineQueueListItems();

        this.updateOfflineQueueItems( offlineQueueListItems );

        console.log( 'OfflineQueue.connectedCallback : exit' );
    }

    static get observedAttributes() {

        return [ 'reload' ];
    }

    async attributeChangedCallback( attrName, oldVal, newVal ) {
        console.log( `OfflineQueue.attributeChangedCallback : attrName[${attrName}], oldVal[${oldVal}], newVal[${newVal}]`)

        if( attrName === 'reload' ) {

            if( newVal === 'true' ) {

                let offlineQueueListItems = await this._loader.loadOfflineQueueListItems();

                this.updateOfflineQueueItems( offlineQueueListItems );

            }

        }

    }

    async updateOfflineQueueItems( offlineQueueListItems ) {
        console.log( "updateOfflineQueueItems : enter" );

        this.innerHTML = '';

        let template = `
<header>
    <h5>Offline Queue <span>[${offlineQueueListItems.length}]</span></h5>
</header>

<section class="offline-queue-container">
    <div class="offline-list-item">
        <span>Request Id</span>
        <span>Type</span>
        <span>Work Order Id</span>
    </div>
</section>`;

        let host = document.createElement( 'div' );
        host.innerHTML = template;

        offlineQueueListItems
            .forEach( function( offlineQueueListItem ) {
                console.dir( offlineQueueListItem );
                host.querySelector( 'section' ).appendChild( offlineQueueListItem );

            });

        this.appendChild( host );

    }

}

export const registerOfflineQueue = () => customElements.define('wo-offline-queue', OfflineQueue );