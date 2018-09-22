//searches through the products with the given input and if its certified only


function search () {
    var value = $("#search-input").val();
    var value2 = $("#searchCertified").prop("checked");

    // refresh page with the new parameters
    $(location).attr('href', context + 'products?search=' + value + '&onlyCertified=' + value2 );
}

function urlParam (name){
    // read a parameter from the current URL
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results==null){
        return null;
    }
    else{
        return decodeURI(results[1]) || '';
    }
}

$ (document).ready(function(){
    // get search filter from URL
    var searchValue = urlParam('search');
    if(searchValue !==  null) {
        $('#search-input').val(searchValue);
    }

    // get certified filter from URL
    var certValue = urlParam('onlyCertified');
    if(certValue !==  null) {
        $('#searchCertified').prop('checked', certValue === 'true');
    }

    // add event listeners
    $("#search-button").on('click', search);
    $("#search-input").keypress(function(event){
        if (event.which == 13) {
            event.preventDefault();
            search();
        }
    });

    $("#search-input").focus();
});