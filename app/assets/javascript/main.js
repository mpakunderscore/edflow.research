/**
 * Created by pavelkuzmin on 20/03/15.
 */

var pages_list = [];
var categories = [];

var selected_categories = [];

function query(text) {

    $.get("/query", {text: text}, function( data ) {});
}

function flow() {

    if (pages_list.length == 0)

        jQuery.ajax({
            url: "/api/pages",
            success: function(response) {
                pages_list = response['pages'].slice();
                categories = response['categories'].slice();
            },
            async: false
        });

    var main = $('main');

    if (pages_list.length == 0) {

        main.html('<ul id="pages"><li>Pages list is empty.</li></ul>');
        return;
    }

    main.html('');
    main.append('<ul id="pages"></ul>');

    var domains = {};

    for (var id in pages_list) {

        var url_categories = pages_list[id]['categories'];

        var remove = false;

        var url_categories_names = [];
        for (var nid in url_categories) {
            var name = url_categories[nid]['name'];
            if (!(name in url_categories_names))
                url_categories_names.push(name);
        }


        for (var cid in selected_categories) {

            //console.log(selected_categories[cid]);
            //console.log(url_categories_names);
            //console.log(!(selected_categories[cid] in url_categories_names));

            var name = selected_categories[cid];

            if (url_categories_names.indexOf(name) < 0) {
                remove = true;
            }
        }

        if (remove)
            continue;

        var domain = pages_list[id]['url'].replace("http://", "").replace("https://", "").replace("www.", "").split("/")[0];

        if (domains[domain] != null)
            domains[domain]++;

        else {

            //if (pages_list[id]['favIconFormat'])
            //    favIconsFormat[domain] = pages_list[id]['favIconFormat'];

            domains[domain] = 1;
        }

        //for (var ucid in url_categories) {
        //
        //    //console.log(categories)
        //
        //    var name = url_categories[ucid].name;
        //    var weight = url_categories[ucid].weight;
        //
        //    if (categories[name] != null)
        //        categories[name] += parseInt(weight);
        //
        //    else
        //        categories[name] = parseInt(weight);
        //}

        var title = pages_list[id]['title'].replace("\<", "\<\\");

        var protocol = pages_list[id]['url'].split("://")[0];
        var domain_ = pages_list[id]['url'].split("://")[1].split("/")[0];

        var favIconUrl = protocol + "://" + domain + "/favicon.ico"



        var favIcon = "<a href=''>" +
            //<object data="http://stackoverflow.com/does-not-exist.png" type="image/png">
            "<img src=" + favIconUrl + "></a>";

        var link = "<a href='" + pages_list[id]['url'] + "' target='_blank' title='" + title + "'>" + title + "</a>";

        var item = "<li class='page'>" + favIcon + link + "</li>";

        $('#pages').append(item);
    }

    var nav = $('nav');
    nav.html('');
    nav.append('<ul id="categories"></ul>');

    for (var cid in selected_categories) {

        var row = "<li><a href='javascript:void(0)' onclick='categories_sort(this)' title='remove' class='selected'>" + selected_categories[cid] + "</a></li>";
        $("#categories").append(row);
    }

    for (var id in categories) {

        var name = categories[id]['name'];

        if (selected_categories.indexOf(name) >= 0)
            continue;

        var row = "<li><a href='javascript:void(0)' onclick='categories_sort(this)' title=''>" + name + "</a></li>";

        $("#categories").append(row);

        if (id >= 13 - selected_categories.length) break;
    }

    flow_();

    window.history.pushState("object or string", "Title", "/");
}

function sort(map) {

    var sortable = [];
    for (var key in map)
        if (map[key] != null)
            sortable.push([key, map[key]]);

    sortable.sort(function(b, a) {return a[1] - b[1]});

    return sortable
}

function categories_sort(clicked) {

    if (selected_categories.indexOf(clicked.text) < 0)
        selected_categories.push(clicked.text);

    else selected_categories.splice(selected_categories.indexOf(clicked.text), 1);

    flow();
}

flow();

function flow_() {
    window.history.pushState("object or string", "Title", "/");
    document.title = "Flow";
}

function streams() {
    window.history.pushState("object or string", "Title", "/streams");
    document.title = "Streams";
}

function about() {
    window.history.pushState("object or string", "Title", "/about");
    document.title = "About";
}

function login() {
    window.history.pushState("object or string", "Title", "/login");
    document.title = "Login";
}

jQuery("header a").click(function(event) {
    $("header a").removeClass("selected");
    jQuery(this).addClass("selected");
});