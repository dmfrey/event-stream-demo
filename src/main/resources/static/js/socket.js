var stompClient = null;

function connect() {
    var socket = new SockJS('/workorders-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/events', function (entry) {
            console.dir( entry );
            showEntry( JSON.parse( entry.body ) );
        });
    });
}

function showEntry(entry) {
    var workorderId = entry.workorderId.substr( 24 );
    var node = entry.node;

    var action;
    var message = "workorder[" + workorderId + "]";
    switch (entry.eventType) {
        case "NameUpdated" :
            action = "Created";
            message += " created";
            break;
        case "WorkorderOpened" :
            action = "Opened";
            message += " opened";
            break;
        case "WorkorderStarted" :
            action = "Started";
            message += " in progress";
            break;
        case "WorkorderStopped" :
            action = "Stopped";
            message += " in review";
            break;
        case "WorkorderCompleted" :
            action = "Completed";
            message += " completed";
            break;
        case "NodeAssigned" :
            action = "Assigned";
            message += " assigned";
            break;
    }
    message += " at node (" + node + ")";
    console.log( message );

    createNotification( action, message );

}

$(function () {

    if( navigator.onLine ) {

        connect();

    }

});

// function for creating the notification
function createNotification(action, message) {

    // Let's check if the browser supports notifications
    if (!"Notification" in window) {
        console.log("This browser does not support notifications.");
    }

    // Let's check if the user is okay to get some notification
    else if (Notification.permission === "granted") {
        // If it's okay let's create a notification

        var notification = new Notification('Work Order ' + action, {body: message});

    }

    // Otherwise, we need to ask the user for permission
    // Note, Chrome does not implement the permission static property
    // So we have to check for NOT 'denied' instead of 'default'
    else if (Notification.permission !== 'denied') {
        Notification.requestPermission(function (permission) {

            // Whatever the user answers, we make sure Chrome stores the information
            if (!('permission' in Notification)) {
                Notification.permission = permission;
            }

            // If the user is okay, let's create a notification
            if (permission === "granted") {
                var notification = new Notification('Work Order ' + action, {body: message});

            }
        });
    }
}