
$(document).ajaxSuccess(function(event, request, settings) {
    $("#msg").append("<li>Successful Authorization!</li>");
});


$(document).ajaxError(function(event, request, settings) {
    $("#msg").append("<li>Error in login</li>");
});

// var logD = document.getElementById('loginDisable');
// var logL = document.getElementById('showLoginLoader');
// logD.setAttribute("disabled", false);
// logL.style.display = 'none';
var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};
var token = getUrlParameter('token');
function init(){
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/confirm/email/"+token,
        dataType: 'json',
        async: false,
        success: function(result) {
            console.log(result);
            document.getElementById("message").innerHTML = result.response_message+' Click here to <a href="login.html">sign in</a>';
        }
    });
}
init();
