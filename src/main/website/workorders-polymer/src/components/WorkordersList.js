import {html, PolymerElement} from '@polymer/polymer';
import '@polymer/polymer/lib/elements/dom-repeat.js';
import '@polymer/iron-ajax/iron-ajax.js';

class WorkordersListElement extends PolymerElement {

    static get template() {
        return html`

            <style>
                .workorder {

                }

                .workorder {

                }

                .workorder h2 {

                }

                .workorder p span.workorderId {
                
                }
            
                .workorder p span.workorderState {
                
                }

            </style>
            
            <iron-ajax
                auto
                url="http://localhost:9090/workorders"
                handle-as="json"
                last-response="{{workorders}}">
            </iron-ajax>
            
            <h2>[[title]]</h2>
            <div id="workordersList">

                <template is="dom-repeat"
                    items="[[workorders]]" 
                    as="workorder">
                    
                    <div class="workorder">

                        <h4>[[workorder.title]]</h4>
                        <p>
                            <span class="workorderId">[[_formatWorkorderId( workorder.workorderId )]]</span>
                            <span class="workorderState">[[workorder.state]]</span>
                        </p>
                    </div>
                </template>

            </div>
        `;
    }

    static get properties() {

        return {
            title: {
                type: String,
                value: 'Work Orders'
            }
        }

    }

    _formatWorkorderId( workorderId ) {

        return workorderId.substr( 24 );
    }

}

customElements.define( 'workorders-list-element', WorkordersListElement );