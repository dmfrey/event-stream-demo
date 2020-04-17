
import loadScripts from './utils/loadScripts.js';
import NavigationController from "./NavigationController.js";
import WorkordersController from './WorkordersController.js';
import { getAllNodes } from "./clients/NodesApi.js";

const polyfillsNeeded = [];

loadScripts( polyfillsNeeded, function() {

    let container = document.querySelector('.container' );

    getAllNodes();
    new WorkordersController( container );
    new NavigationController( container );

});