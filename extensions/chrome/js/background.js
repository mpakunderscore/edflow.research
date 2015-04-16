chrome.browserAction.onClicked.addListener(function(tab) {

    localStorage["userId"] = 0;

    var userId = localStorage["userId"];
    if (!userId) {

        var url = host + "/api/user";

        var request = new XMLHttpRequest();
        request.open("GET", url, false);
        request.send(null);

        var response = request.responseText;

        if (response.length == 0) {

            var loginURL = host + "/login";
            chrome.tabs.create({ url: loginURL });
            return;
        }

        var user = JSON.parse(response);

        userId = user.id;
        localStorage["userId"] = userId;
    }

    add_url(tab, userId);
});