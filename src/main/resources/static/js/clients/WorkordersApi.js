import Fetch from '../utils/Fetch.js'

const _apiContext = '/workorders';

// GET all workorders
export async function getAllWorkorders() {
    const workorders = await Fetch.get( _apiContext );

    return workorders;
}

// POST create workorder
export async function createWorkorder( title, targetNode ) {
    const request = await Fetch.create( _apiContext, {
        title: title,
        targetNode: targetNode
    });

    return request;
}

// GET workorder by id
export async function getWorkorder( workorderId ) {
    const request = await Fetch.get( _apiContext + '/' + workorderId );

    return request;
}

// PUT transfer workorder
export async function transferWorkorder( workorderId, targetNode ) {
    const request = await Fetch.update( _apiContext + '/' + workorderId + '/transfer', {
        targetNode: targetNode
    });

    return request;
}

// PUT change workorder state
export async function changeWorkorderState( workorderId, action ) {
    const request = await Fetch.update( _apiContext + '/' + workorderId + '/' + action );

    return request;
}
