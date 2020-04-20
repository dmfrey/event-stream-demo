
export default async function template() {

    let nodes = await fetch( '/nodes' ).then( (response) => response.json() );

    return `<header>
    <h3>Create New Work Order</h3>
</header>

<form>

    <input type="hidden" name="type" value="CreateWorkorderRequest" />
    
    <p>
        <label for="title">Title</label>
        <input type="text" id="title" name="title" />
    </p>

    <p>
        <label for="targetNode">Target Node</label>
        <select id="targetNode" name="targetNode">
            ${ nodes.availableNodes.map( node => `<option value="${node}" ${ (node === nodes.currentNode) ? `selected="true"` : `` }>${node}</option>` )}
    </select>
    </p>

    <p>
        <input type="submit" name="new" value="New" />
    </p>

</form>`;
}