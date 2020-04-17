
// From component folder
// import { users, issues } from '../components';

// From layout folder
// import { header, sidebar } from '../layouts';

import NavigationController from "../NavigationController.js";

class Dashboard {

    loadDashboard( container ) {

        NavigationController( container );

        console.log( 'Dashboard loaded' );
    }

}

export let dashboard = new Dashboard();