
import { DataBinding } from "./dataBinding.js";

export class WorkorderListItem {

    constructor( text, workorder ) {

        this._text = text;

        this._context = {};

        this._dataBinding = new DataBinding();

        this._html = document.createElement( 'div' );
        this._html.innerHTML = text;

        this._title = workorder.title;
        this._workorderId = workorder.workorderId;
        this._state = workorder.state;

        // execute any scripts
        const script = this._html.querySelector( 'script' );
        if( script ) {

            this._dataBinding.executeInContext( script.innerText, this._context, true );
            this._dataBinding.bindAll( this._html, this._context );

        }

    }

    get title() {

        return this._title;
    }

    get workorderId() {

        return this._workorderId;
    }

    get state() {

        return this._state;
    }

    get html() {

        return this._html;
    }

}