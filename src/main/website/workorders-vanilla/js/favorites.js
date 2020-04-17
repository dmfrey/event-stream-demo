
export class Favorites extends HTMLElement {

    constructor() {
        super();

    }

    async connectedCallback() {

        const response = await fetch( './templates/favorites.html' );
        const template = await response.text();
        this.innerHTML = "";
        const host = document.createElement( 'div' );
        host.innerHTML = template;
        this.appendChild( host );

    }

}

export const registerFavorites = () => customElements.define('wo-favorites', Favorites );