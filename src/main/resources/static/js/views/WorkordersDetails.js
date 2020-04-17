
import WorkorderDetailsTemplate from '../templates/WorkorderDetails.js';

export default function WorkorderDetails( container ) {

    let workorder = this;

    this._container = container;

}

WorkorderDetails.prototype.setWorkorder = function( workorder ) {

    this._container.innerHTML = '';
    this._container.innerHTML = WorkorderDetailsTemplate( workorder );

};

WorkorderDetails.prototype.getContainer = function() {

    return this._container;
};