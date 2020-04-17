
export class OfflineQueue extends HTMLElement {

    constructor() {
        super();

    }

    async connectedCallback() {

        const response = await fetch( './templates/offline-queue.html' );
        const template = await response.text();
        this.innerHTML = "";
        const host = document.createElement( 'div' );
        host.innerHTML = template;
        this.appendChild( host );

    }

}

export const registerOfflineQueue = () => customElements.define('wo-offline-queue', OfflineQueue );