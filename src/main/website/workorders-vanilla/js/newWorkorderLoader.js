
import newWorkorderTemplate from './workorder/newWorkorderView.js'

export default class NewWorkorderLoader {

    constructor() {
        console.log( 'NewWorkorderLoader - initialize : enter' );

        this._dispatchReceiver = document.getElementById('dispatch' );
        this._registerEventListeners();

        console.log( 'NewWorkorderLoader - initialize : exit' );
    }

    _registerEventListeners() {

        let loader = this;

    }

    _loadTemplateView() {

        let host = document.createElement('div');
        host.innerHTML = newWorkorderTemplate();

        return host;
    }

    async loadNewWorkorder() {
        console.log( 'loadNewWorkorder : enter' );

        return this._loadTemplateView();
    }

}