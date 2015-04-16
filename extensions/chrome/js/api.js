//var host = "https://edtag.io";
var host = "http://localhost:9000";
//var host = "https://dry-tundra-9556.herokuapp.com";


var notificationTime = 3000;

var notID = 0;

function add_url(tab, userId) {

    if (tab.status != "complete")
        return;

    var secondsStart = new Date().getTime() / 1000;

    var url = host + "/api/page?url=" + encodeURIComponent(tab.url) + "&userId=" + userId;

    var request = new XMLHttpRequest();
    request.open( "GET", url, false );
    request.send( null );

    var response = request.responseText;

    if (response.length == 0)
        return; //TODO

    var secondsEnd = new Date().getTime() / 1000;

    var time = (secondsEnd - secondsStart) + "";

    notification(tab.favIconUrl, tab.title,  time); //TODO
}

function notification(iconUrl, title, text) {

    var options = {
        type : "basic",
        title: title,
        message: text,
        expandedMessage: "Longer part of the message"
    };

    options.iconUrl = iconUrl;

    options.priority = 0;

    options.buttons = [];
//    options.buttons.push({ title: "ok" });

    var id = "id" + notID++;

    chrome.notifications.create(id, options, creationCallback);
}

function creationCallback(notID) {

    console.log("Succesfully created " + notID + " notification");

//    if (document.getElementById("clear").checked) {

        setTimeout(function() {

            chrome.notifications.clear(notID, function(wasCleared) {
                console.log("Notification " + notID + " cleared: " + wasCleared);
            });

        }, notificationTime);
//    }
}
