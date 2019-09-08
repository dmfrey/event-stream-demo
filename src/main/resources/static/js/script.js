$( document ).ready( function() {

    createNewWorkorderForm();
    getWorkorderStates();

    $( document ).on( 'submit', "#create-workorder", function( event ) {
       event.preventDefault();

       createNewWorkorder( this );

    });

    $( document ).on( 'submit', "#transfer-workorder", function( event ) {
        event.preventDefault();

        transferWorkorder( this );

    });

    $( document ).on( 'click', ".wo-list-item", function( event ) {

        var workorderId = $( this ).attr( "data-workorderId" );
        getWorkorderDetails( workorderId );

    });

    $( document ).on( 'click', ".btn-wo-open", function( event ) {

        var workorderId = $( this ).attr( "data-workorderId" );
        changeWorkorderState( workorderId, "open" );

    });

    $( document ).on( 'click', ".btn-wo-start", function( event ) {

        var workorderId = $( this ).attr( "data-workorderId" );
        changeWorkorderState( workorderId, "start" );

    });

    $( document ).on( 'click', ".btn-wo-stop", function( event ) {

        var workorderId = $( this ).attr( "data-workorderId" );
        changeWorkorderState( workorderId, "stop" );

    });

    $( document ).on( 'click', ".btn-wo-complete", function( event ) {

        var workorderId = $( this ).attr( "data-workorderId" );
        changeWorkorderState( workorderId, "complete" );

    });

});

function createNewWorkorder( form ) {

    var $form = $( form ),
        title = $form.find( "input[name='title']" ).val(),
        targetNode = $form.find( "select[name='targetNode']" ).val(),
        url = $form.attr( "action" );

    $.ajax({
        contentType: "application/json",
        method: "POST",
        url: url,
        data: JSON.stringify({ "title": title, "targetNode": targetNode } ),
        dataType: "json"
    })
        .always( function( data ) {
            console.dir( data );

            $form[0].reset();

            setTimeout( function() {
                console.log( 'waiting 2 seconds' );
                getWorkorderStates();

            }, 2000 );

        });

}

function transferWorkorder( form ) {

    var $form = $( form ),
        targetNode = $form.find( "select[name='targetNode']" ).val(),
        url = $form.attr( "action" );

    $.ajax({
        method: "PUT",
        url: url + '?targetNode=' + targetNode
    })
        .always( function( data ) {
            console.dir( data );

            setTimeout( function() {
                console.log( 'waiting 2 seconds' );
                $( "#wo-details" ).empty();
                getWorkorderStates();

            }, 2000 );

        })
        .fail( function( data, jqXHR, textStatus ) {
            console.log( textStatus );
            // console.dir( data );
            // console.dir( jqXHR );

            createNotification( 'ERROR!!', data.responseJSON.message );

        });

}

function getWorkorderStates() {

    $.get( "/workorders" )
        .done( function( data ) {
            console.dir( data );

            $( "#wo-list" ).empty();
            $.each( data, function( index, value ) {

                value.workorderKey = value.workorderId.substr( 24 );

                var html = Mustache.to_html( $( "#workorderStatusTemplate" ).html(), value );
                $( "#wo-list" ).append( html );

            });

        });

}

function getWorkorderDetails( workorderId ) {

    $.get( "/workorders/" + workorderId )
        .done( function( data ) {
            console.dir( data );

            var details = $( "#wo-details" );
            details.empty();

            data.options = options.options;
            $.each( data.options, function( index, option ) {
               if( option.value === data.assigned ) {
                   option.selected = true;
               } else {
                   option.selected = false;
               }
            });

            var html = Mustache.to_html( $( "#workorderTemplate" ).html(), data );
            details.html( html );

            if( "OPEN" === data.state ) {

                $( '.btn-wo-open' ).hide();

            }

            if( "IN_PROCESS" === data.state ) {

                $( '.btn-wo-open' ).hide();
                $( '.btn-wo-start' ).hide();

            }

            if( "IN_REVIEW" === data.state ) {

                $( '.btn-wo-open' ).hide();
                $( '.btn-wo-start' ).hide();
                $( '.btn-wo-stop' ).hide();

            }

            if( "COMPLETE" === data.state ) {

                $( '.btn-wo-open' ).hide();
                $( '.btn-wo-start' ).hide();
                $( '.btn-wo-stop' ).hide();
                $( '.btn-wo-complete' ).hide();

            }

        });

}

function changeWorkorderState( workorderId, action ) {

    $.ajax({
        method: "PUT",
        url: "/workorders/" + workorderId + "/" + action
    })
        .done( function ( data ) {

            setTimeout( function() {

                getWorkorderDetails( workorderId );

            }, 1000 );

        })
        .fail( function( data, jqXHR, textStatus ) {
            console.log( textStatus );
            // console.dir( data );
            // console.dir( jqXHR );

            createNotification( 'ERROR!!', data.responseJSON.message );

        });

}

function createNewWorkorderForm() {

    $.get( "/nodes" )
        .done( function( data ) {
            console.dir( data );

            currentNode = data.currentNode;

            $( "#create-workorder-form-container" ).empty();

            $.each( data.availableNodes, function( index, option ) {

                options.options[ index ] = { 'value': option, 'text': option };
                if( option === data.currentNode ) {
                    options.options[ index ].selected = true;
                }

            });

            var html = Mustache.to_html( $( "#createWorkorderFormTemplate" ).html(), options );
            $( "#create-workorder-form-container" ).append( html );

        });

}

var options = {
    'options': []
};

var currentNode = "";