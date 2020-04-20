
import { Router } from "./router.js"
import NewWorkorderLoader from "./newWorkorderLoader.js";
import WorkorderLoader from "./workorderLoader.js";

export class Navigator extends HTMLElement {

    constructor() {
        super();

        this._newWorkorderLoader = new NewWorkorderLoader();
        this._workorderLoader = new WorkorderLoader();

        this._router = new Router();

        this._route = this._router.getRoute();

        this._router.eventSource.addEventListener( 'routechanged', () => {
            console.log( `current route[${this._route}]` );

            if( this._route !== this._router.getRoute() ) {

                this._route = this._router.getRoute();
                if( this._route ) {
                    console.log( `new route is ${this._route}` );

                    // this.showControl( this._route );

                }

            }

        });

    }

    static get observedAttributes() {

        return [ 'start', 'workorderid' ];
    }

    async attributeChangedCallback( attrName, oldVal, newVal ) {
        console.log( `Navigator.attributeChangedCallback : attrName[${attrName}], oldVal[${oldVal}], newVal[${newVal}]`)

        if( attrName === "start" ) {

            await this.showNewWorkorder();

        }

        if( attrName === "workorderid" && newVal !== '' ) {

            await this.showWorkorder( newVal );

        }

    }

    showControl( control, route ) {

        // console.dir( control );
        // console.log( this );

        this.innerHTML = '';
        this.appendChild( control );
        this._router.setRoute( route );
        this._route = this._router.getRoute();

    }

    async showNewWorkorder() {

        let control = await this._newWorkorderLoader.loadNewWorkorder();
        this.showControl( control, "new-workorder" );

    }

    async showWorkorder( workorderId ) {

        let control = await this._workorderLoader.loadWorkorder( workorderId );
        this.showControl( control, workorderId );

    }

}

export const registerControl = () => customElements.define('wo-control', Navigator);
