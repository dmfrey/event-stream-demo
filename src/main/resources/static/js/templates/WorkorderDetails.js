import Nodes from '../respository/NodesDs.js'

export default function workorderDetailsTemplate( workorder ) {

    return `<input type="hidden" id="current-workorder" value="${workorder.workorderId}" />
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
                            ${ Nodes.options.map( option => `<option value="${option.value}" ${ (option.value === workorder.assigned) ? `selected="true"` : `` }>${option.text}</option>` )}
                        </select>
                        <input id="transfer-workorder-submit" type="submit" value="Transfer Work Order"></input>
                    </form>
                </div>
                <div class="row">
                    <span class="col-md-2">
                        <button id="btn-open" class="btn btn-primary btn-wo-open" data-workorderId="${workorder.workorderId}" data-action="OpenWorkorderRequest" ${workorder.state == 'OPEN' || workorder.state == 'COMPLETE'  ? `hidden="true"` : ''}>Open</button>
                    </span>
                    <span class="col-md-2">
                        <button id="btn-start" class="btn btn-success btn-wo-start" data-workorderId="${workorder.workorderId}" data-action="StartWorkorderRequest" ${workorder.state == 'IN_PROCESS' || workorder.state == 'COMPLETE' ? `hidden="true"` : ''}>Start</button>
                    </span>
                    <span class="col-md-2">
                        <button id="btn-stop" class="btn btn-danger btn-wo-stop" data-workorderId="${workorder.workorderId}" data-action="StopWorkorderRequest" ${workorder.state == 'IN_REVIEW' || workorder.state == 'COMPLETE' ? `hidden="true"` : ''}>Stop</button>
                    </span>
                    <span class="col-md-2">
                        <button id="btn-complete" class="btn btn-primary btn-wo-complete" data-workorderId="${workorder.workorderId}" data-action="CompleteWorkorderRequest" ${workorder.state == 'COMPLETE' ? `hidden="true"` : ''}>Complete</button>
                    </span>
                </div>`;
}