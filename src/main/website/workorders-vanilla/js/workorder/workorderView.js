
export default async function template( workorder ) {

    let nodes = await fetch( '/nodes' ).then( (response) => response.json() );

    return `<header>
    <h3>Work Order</h3>
</header>

<section class="details">

    <p>
        <span>Title:</span>
        ${workorder.title}
    </p>
    <p>
        <span>State:</span>
        ${workorder.state}
    </p>
    <p>
        <span>Assigned Node:</span>
        ${ (typeof workorder.assigned !== 'undefined') ? workorder.assigned : '' }
    </p>
    <p>
        <span>Origination Node:</span>
        ${ (typeof workorder.origination !== 'undefined') ? workorder.origination : '' }
    </p>

</section>

${(() => {
    
    if( workorder.state !== 'COMPLETE' ) {
        
        return `

<section class="transfer">

    <form id="transfer-form" method="PUT" action="/api/workorders/${workorder.workorderId}">

        <input type="hidden" name="type" value="TransferWorkorderRequest" />
        <input type="hidden" name="workorderId" value="${workorder.workorderId}" />

        <p>
        <label for="targetNode">Target Node</label>
        <select id="targetNode" name="targetNode">
            ${ nodes.availableNodes.filter( node => node !== 'cloud' ).map( node => `<option value="${node}" ${ (node === nodes.currentNode) ? `selected="true"` : `` }>${node}</option>` )}
        </select>
        <input type="submit" name="submit" value="Transfer" />
        </p>

    </form>

</section>

<section class="actions">

    <form id="open-form" method="PUT" action="/api/workorders/${workorder.workorderId}">

        <input type="hidden" name="_targetState" value="OPEN" />
        <input type="hidden" name="type" value="OpenWorkorderRequest" />
        <input type="hidden" name="workorderId" value="${workorder.workorderId}" />
        <input type="submit" name="submit" value="Open" />

    </form>

    <form id="start-form" method="PUT" action="/api/workorders/${workorder.workorderId}">

        <input type="hidden" name="_targetState" value="IN_PROCESS" />
        <input type="hidden" name="type" value="StartWorkorderRequest" />
        <input type="hidden" name="workorderId" value="${workorder.workorderId}" />
        <input type="submit" name="submit" value="Start" />

    </form>

    <form id="stop-form" method="PUT" action="/api/workorders/${workorder.workorderId}">

        <input type="hidden" name="_targetState" value="IN_REVIEW" />
        <input type="hidden" name="type" value="StopWorkorderRequest" />
        <input type="hidden" name="workorderId" value="${workorder.workorderId}" />
        <input type="submit" name="submit" value="Stop" />

    </form>

    <form id="complete-form" method="PUT" action="/api/workorders/${workorder.workorderId}">

         <input type="hidden" name="_targetState" value="COMPLETE" />
         <input type="hidden" name="type" value="CompleteWorkorderRequest" />
        <input type="hidden" name="workorderId" value="${workorder.workorderId}" />
        <input type="submit" name="submit" value="Complete" />

    </form>

</section>`
    } else {
        
        return ``
    }
    
})()}
`;
}