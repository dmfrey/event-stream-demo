
export default function template() {

    return `<header>
    <h3>Create New Work Order</h3>
</header>

<form>

    <p>
        <label for="title">Title</label>
        <input type="text" id="title" name="title" />
    </p>

    <p>
        <label for="targetNode">Target Node</label>
        <select id="targetNode" name="targetNode">
            <option value="---">---</option>
        </select>
    </p>

    <p>
        <input type="submit" value="New" />
    </p>

</form>`;
}