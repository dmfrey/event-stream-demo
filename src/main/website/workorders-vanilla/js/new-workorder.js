
export class NewWorkorder extends HTMLElement {

    constructor() {
        super();

    }

    async connectedCallback() {

        const response = await fetch( './templates/new-workorder.html' );
        const template = await response.text();
        this.innerHTML = "";
        const host = document.createElement( 'div' );
        host.innerHTML = template;
        this.appendChild( host );

    }

}

export const registerNewWorkorder = () => customElements.define('new-workorder', NewWorkorder );