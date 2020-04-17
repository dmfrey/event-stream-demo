
export default function workorderItemTemplate( workorder ) {

    return `<div class="row wo-list-item" data-workorderId="${workorder.workorderId}">
    <span class="col-sm-5" data-workorderId="${workorder.workorderId}">${workorder.workorderId.substr(24)}</span>
    <span class="col-sm-5" data-workorderId="${workorder.workorderId}">${workorder.title}</span>
</div>`;
}