import NodesDs from './respository/NodesDs.js';
import { getAllWorkorders, getWorkorder, createWorkorder, transferWorkorder, changeWorkorderState } from './clients/WorkordersApi.js';

function createWorkordersList( wrapperId ) {

    let woListWrapper = document.getElementById( wrapperId );

    getAllWorkorders()
        .then( (workorders) => {
            const woRow =
                `${workorders.map( wo => 
                    `<div class="row wo-list-item" data-workorderId="${wo.workorderId}">
                        <span class="col-sm-5" data-workorderId="${wo.workorderId}">${wo.workorderId.substr( 24 )}</span>
                        <span class="col-sm-5" data-workorderId="${wo.workorderId}">${wo.title}</span>
                    </div>`
                )}`;

            woListWrapper.innerHTML = woRow;

            let elements = document.getElementsByClassName( "wo-list-item" );
            Array.from( elements ).forEach( (element) => element.addEventListener( 'click', loadWorkorder, false ) );
        });

}

function createWorkorderForm( wrapperId ) {

    let formWrapper = document.getElementById( wrapperId );

    const form = `
        <header> Create New Work Order</header>

        <form id="create-workorder" action="/workorders" method="post">
            <label for="new_title">Title</label>
            <input type="text" id="new_title" name="title" placeholder="Title" />
            <label for="new_targetNode">Target Node</label>
            <select id="new_targetNode" name="targetNode">
                ${NodesDs.options.map( option => `<option value="${option.value}" ${option.selected ? `selected="selected"` : ''}>${option.text}</option>` )}
            </select>
            <input id="create-workorder-submit" type="submit" value="Create Work Order" />
        </form>
    `;

    formWrapper.innerHTML = form;

    document.getElementById( 'create-workorder' ).addEventListener( 'submit', submitCreateWorkorder, false );
}

function loadWorkorder( event ) {
    console.debug( "loadWorkorder : enter" );

    let workorderId = event.target.getAttribute( 'data-workorderId' );
    console.log( `workorderId = ${workorderId}` );

    let woDetailsWrapper = document.getElementById( "wo-details" );
    woDetailsWrapper.innerHTML = '';

    getWorkorder( workorderId )
        .then( (workorder) => {
            const woDetails = `
                <input type="hidden" id="current-workorder" value="${workorder.workorderId}" />
                <div class="row">
                    <span class="col-md-5">${workorder.title}</span>
                    <span class="col-md-5">State: ${workorder.state}</span>
                </div>
                <div class="row">
                    <span class="col-md-5">Assigned Node: ${workorder.assigned}</span>
                    <span class="col-md-5">Origination Node: ${workorder.origination}</span>
                </div>
                <div class="row">
                    <form id="transfer-workorder" action="/workorders/${workorder.workorderId}/transfer" method="put" class="col-md-12">
                        <input type="hidden" id="transfer_workorderId" value="${workorder.workorderId}" />
                        <label for="transfer_targetNode">Target Node:</label>
                        <select id="transfer_targetNode" name="targetNode">
                            ${NodesDs.options.map( option => `<option value="${option.value}" ${option.selected ? `selected="selected"` : ''}>${option.text}</option>` )}
                        </select>
                        <input id="transfer-workorder-submit" type="submit" value="Transfer Work Order"></input>
                    </form>
                </div>
                <div class="row">
                    <span class="col-md-2">
                        <button id="btn-open" class="btn btn-primary btn-wo-open" data-workorderId="${workorder.workorderId}" data-action="open" ${workorder.state == 'OPEN' || workorder.state == 'COMPLETE'  ? `hidden="true"` : ''}>Open</button>
                    </span>
                    <span class="col-md-2">
                        <button id="btn-start" class="btn btn-success btn-wo-start" data-workorderId="${workorder.workorderId}" data-action="start" ${workorder.state == 'IN_PROCESS' || workorder.state == 'COMPLETE' ? `hidden="true"` : ''}>Start</button>
                    </span>
                    <span class="col-md-2">
                        <button id="btn-stop" class="btn btn-danger btn-wo-stop" data-workorderId="${workorder.workorderId}" data-action="stop" ${workorder.state == 'IN_REVIEW' || workorder.state == 'COMPLETE' ? `hidden="true"` : ''}>Stop</button>
                    </span>
                    <span class="col-md-2">
                        <button id="btn-complete" class="btn btn-primary btn-wo-complete" data-workorderId="${workorder.workorderId}" data-action="complete" ${workorder.state == 'COMPLETE' ? `hidden="true"` : ''}>Complete</button>
                    </span>
                </div>
            `;

            woDetailsWrapper.innerHTML = woDetails;

            document.getElementById( 'transfer-workorder' ).addEventListener( 'submit', submitTransferWorkorder, false );
            document.getElementById( 'btn-open' ).addEventListener( 'click', submitWorkorderStateChange, false );
            document.getElementById( 'btn-start' ).addEventListener( 'click', submitWorkorderStateChange, false );
            document.getElementById( 'btn-stop' ).addEventListener( 'click', submitWorkorderStateChange, false );
            document.getElementById( 'btn-complete' ).addEventListener( 'click', submitWorkorderStateChange, false );

        });

    console.debug( "loadWorkorder : exit" );
}

function submitCreateWorkorder( event ) {
    console.debug( "submitCreateWorkorder : enter" );

    event.preventDefault();

    let titleInput = document.getElementById( "new_title" );
    let targetNodeSelect = document.getElementById( "new_targetNode" );

    createWorkorder( titleInput.value, targetNodeSelect.value );

    console.debug( "submitCreateWorkorder : exit" );
}

function submitTransferWorkorder( event ) {
    console.debug( "submitTransferWorkorder : enter" );

    event.preventDefault();

    let workorderIdInput = document.getElementById( "transfer_workorderId" );
    let targetNodeSelect = document.getElementById( "transfer_targetNode" );

    transferWorkorder( workorderIdInput.value, targetNodeSelect.value )
        .then( () => getWorkorder( workorderIdInput.value ) );

    console.debug( "submitTransferWorkorder : exit" );
}

function submitWorkorderStateChange( event ) {
    console.debug( "submitWorkorderStateChange : enter" );

    event.preventDefault();

    let button = document.getElementById( event.target.id );

    changeWorkorderState( button.getAttribute( 'data-workorderId' ), button.getAttribute( 'data-action' ) )
        .then( () => getAllWorkorders() )
        .then( () => getWorkorder( button.getAttribute( 'data-workorderId' ) ) );

    console.debug( "submitWorkorderStateChange : exit" );
}

if ('serviceWorker' in navigator) {

    navigator.serviceWorker.onmessage = function (evt) {
        let message = evt.data;
        console.dir( message );

        let isRefresh = message.type === 'refresh';
        // let isAsset = message.url.includes('asset');
        let lastETag = localStorage.getItem( message.url );

        // [ETag](https://en.wikipedia.org/wiki/HTTP_ETag) header usually contains
        // the hash of the resource so it is a very effective way of check for fresh
        // content.
        let isNew = lastETag !== message.eTag;

        if (isRefresh /*&& isAsset*/ && isNew) {
            // Escape the first time (when there is no ETag yet)
            if (lastETag) {
                // Inform the user about the update
                notice.hidden = false;
            }
            // For teaching purposes, although this information is in the offline
            // cache and it could be retrieved from the service worker, keeping track
            // of the header in the `localStorage` keeps the implementation simple.
            localStorage.setItem( message.url, message.eTag );
        }
    };

    let notice = document.querySelector('#update-notice');

    let update = document.querySelector('#update');
    update.onclick = function (evt) {
        // let img = document.querySelector('img');
        // Avoid navigation.
        evt.preventDefault();
        // Open the proper cache.
        // caches.open(CACHE)
        //     // Get the updated response.
        //     .then(function (cache) {
        //         return cache.match(img.src);
        //     })
        //     // Extract the body as a blob.
        //     .then(function (response) {
        //         return response.blob();
        //     })
        //     // Update the image content.
        //     .then(function (bodyBlob) {
        //         let url = URL.createObjectURL(bodyBlob);
        //         img.src = url;
        //         notice.hidden = true;
        //     });

        createWorkordersList( 'create-workorder-form-container' );
        loadWorkorder;

    };

}

export { createWorkordersList, createWorkorderForm };