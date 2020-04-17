
import NavigationTemplate from '../templates/Navigation.js';

export default function Navigation( container ) {

    let navigation = this;

    this._container = container;
    this._container.innerHTML = NavigationTemplate();

}

Navigation.prototype.getContainer = function() {

    return this._container;
};