
//searches through the products with the given input and if its certified only
function search () {
    var value = $("#search-input").val();
    var value2 = $("#searchCertified").prop("checked");

    $.ajax("product-list?search=" + value+ '&onlyCertified=' + value2, {}).done(function(fragment){
        $("#productList").replaceWith(fragment);
    });
}

$ (document).ready(function(){
    $("#search-button").on('click', search);
    $("#search-input").keypress(function(event){
        if (event.which == 13) {
            event.preventDefault();
            search();
        }
    });

    search();
});