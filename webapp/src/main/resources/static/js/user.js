function search() {
    var value = $("#search-input").val();
    console.log(value);
    $(location).attr('href', 'users?search=' + value);
}

function urlParam(name) {
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results == null) {
        return null;
    }
    else {
        return decodeURI(results[1]) || '';
    }
}

function admin(target) {
    console.log(target.checked);

    var role = target.checked ? 'ROLE_ADMIN' : 'ROLE_USER';

    var data = {
        email: target.getAttribute('user-email'),
        role: role
    };

    $.ajax({
        type: "POST",
        url: "users/admin",
        data: JSON.stringify(data),
        contentType: "application/json; charset=utf-8",
        dataType: "json"
    }).done(function (result) {
        console.log(result);
    })
}

$(document).ready(function () {
    var searchValue = urlParam('search');
    if (searchValue !== null) {
        $('#search-input').val(searchValue);
    }
    $("#search-button").on('click', search);
    $("#search-input").keypress(function (event) {
        if (event.which == 13) {
            event.preventDefault();
            search();
        }
    });
    $("#search-input").focus();
});