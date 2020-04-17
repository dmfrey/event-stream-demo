
export class Workorder extends HTMLElement {

    constructor() {
        super();

    }

    async connectedCallback() {

        const response = await fetch( './templates/workorder.html' );
        const template = await response.text();
        this.innerHTML = "";
        const host = document.createElement( 'div' );
        host.innerHTML = template;
        this.appendChild( host );

    }

}

export const registerWorkorder = () => customElements.define('wo-workorder', Workorder );