
import workorderLoader from './workorderLoader.js'

async function loadControl( controlName ) {

    const response = await fetch( `./templates/${controlName}.html` );
    const control = await response.text();

    let html = document.createElement( 'div' );
    html.innerHTML = control;

    return html;
}

export async function loadControls() {

    let controls = [];
    controls[ "new-workorder" ] =  await workorderLoader.loadWorkorder();
    controls[ "workorder" ] = await workorderLoader.loadWorkorder();

    return controls;
}
