import Fetch from '../utils/Fetch.js'
import NodesDs from '../respository/NodesDs.js'

const _apiContext = '/nodes';

// GET all nodes
async function getAllNodes() {

    const request = await Fetch.get( _apiContext )
        .then( (data) => {

            NodesDs.currentNode = data.currentNode;

            let options = [];

            data.availableNodes.forEach( (element, index) => {

                options[ index ] = { 'value': element, 'text': element };
                if( element === data.currentNode ) {
                    options[ index ].selected = true;
                }

            });

            NodesDs.options = options;

        });

    return request;
}

export { getAllNodes }