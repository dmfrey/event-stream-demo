import WorkorderItemTemplate from '../templates/WorkorderListItem.js';

export default function WorkordersList(container ) {

    let workorders = this;

    this._container = container;

}

WorkordersList.prototype.addWorkorders = function(workorders ) {

    let htmlString = workorders.map( function( workorder ) {
        return WorkorderItemTemplate( workorder );
    }).join( '' );

    this._container.innerHTML = htmlString;

};