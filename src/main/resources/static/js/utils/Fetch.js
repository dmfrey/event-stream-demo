
async function request( url, params, method = 'GET' ) {

    // options passed to the fetch request
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json' // we will be sending JSON
        }
    };

    // if params exists and method is GET, add query string to url
    // otherwise, just add params as a "body" property to the options object
    if( params ) {
        if( method === 'GET' ) {
            url += '?' + objectToQueryString( params );
        } else {
            options.body = JSON.stringify( params ); // body should match Content-Type in headers option
        }
    }

    // fetch returns a promise, so we add keyword await to wait until the promise settles
    const response = await fetch( url, options );
    // console.dir( response.clone() );

    // show an error if the status code is not 200
    if( response.status !== 200 ) {

        return generateErrorResponse( 'The server responded with an unexpected status.' );
    }

    const result = await response.json(); // convert response into JSON

    // returns a single Promise object
    return result;
}

// converts an object into a query string
// ex: {authorId : 'abc123'} -> &authorId=abc123
function objectToQueryString(obj) {

    return Object.keys( obj ).map( key => key + '=' + obj[ key ] ).join( '&' );
}

// A generic error handler that just returns an object with status=error and message
function generateErrorResponse( message ) {

    return {
        status : 'error',
        message
    };
}

function get( url, params ) {

    return request( url, params );
}

function create( url, params ) {

    return request( url, params, 'POST' );
}

function update( url, params ) {

    return request( url, params, 'PUT' );
}

function remove( url, params ) {

    return request( url, params, 'DELETE' );
}

export default {
    get,
    create,
    update,
    remove
};

