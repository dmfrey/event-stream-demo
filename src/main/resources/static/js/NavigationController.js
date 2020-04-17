
import NavigationView from './views/Navigation.js';

export default function NavigationController( container ) {
    console.log( 'NavigationController - initialize : enter' );

    this._container = container;
    this._navigationView = new NavigationView( this._container.querySelector( 'nav' ) );

    console.log( 'NavigationController - initialize : exit' );
}