
export default function template( workorder ) {

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

</section>

<section class="transfer">

    <form id="transfer-form" method="PUT" action="/api/workorders/${workorder.workorderId}">

        <p>
        <label for="targetNode">Target Node</label>
        <select id="targetNode" name="targetNode">
            <option value="---">---</option>
        </select>
        </p>

        <p>
        <input type="submit" value="Transfer" />
        </p>

    </form>

</section>

<section class="actions">

    <form id="open-form" method="PUT" action="/api/workorders/${workorder.workorderId}">

        <input type="submit" value="Open" />

    </form>

    <form id="start-form" method="PUT" action="/api/workorders/${workorder.workorderId}">

        <input type="submit" value="Start" />

    </form>

    <form id="stop-form" method="PUT" action="/api/workorders/${workorder.workorderId}">

        <input type="submit" value="Stop" />

    </form>

    <form id="complete-form" method="PUT" action="/api/workorders/${workorder.workorderId}">

        <input type="submit" value="Complete" />

    </form>

</section>`;
}