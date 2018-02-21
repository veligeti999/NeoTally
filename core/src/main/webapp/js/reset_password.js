if (localStorage.getItem('myCat')) {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/logout",
        dataType: 'json',
        async: false
    });
}

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

$(function() {
    // Initialize form validation on the registration form.
    // It has the name attribute "registration"
    $("form[name='resetPasswordForm']").validate({
        // Specify validation rules
        rules: {
            // The key name on the left side is the name attribute
            // of an input field. Validation rules are defined
            // on the right side
           password: {
                required: true,
                minlength: 5
            },
            confirmPassword: {
                required: true,
                minlength: 5,
                equalTo: "#password"
            }
        },
        // Specify validation error messages
        /*messages: {
          name: "Please enter your Merchant Name",
          ownerName: "Please enter your Owner Name",
          password: {
            required: "Please provide a password",
            minlength: "Your password must be at least 5 characters long"
          },
          email: "Please enter a valid email address"
        },*/
        // Make sure the form is submitted to the destination defined
        // in the "action" attribute of the form when valid
        submitHandler: function(form) {
            // logD.setAttribute('disabled', true);
            // logL.style.display = 'block';
            var postJson = {};
            postJson.password = document.getElementById('password').value;
            postJson.confirmPassword = document.getElementById('confirmPassword').value;
            var token = getUrlParameter('token');
            var userType = getUrlParameter('userType');
            console.log("token:::"+token+":::userType;;;;"+userType)
                $.ajax({
                    type: "POST",
                    url: "/new-tally/rest/reset/password/"+token+"/"+userType,
                    dataType: 'json',
                    async: false,
                    data: JSON.stringify(postJson),
                    headers: {
                        "Content-Type": "application/json"
                    },
                    success: function(result) {
                               if (result.response_code == 0) {
                                    localStorage.setItem('myCat', 'Tom');
                                    toastr.success(result.response_message, "SUCCESS");
                                    setTimeout(function() {
                                        // window.history.go(-window.history.length);
                                        window.location.href = "login.html";
                                    }, 1000);
                                } else {
                                    // $('#loginDisable').removeAttr('disabled');
                                    // logL.style.display = 'none';
                                    toastr.error(result.response_message, 'ERROR');
                                }
                    },
                    error: function(error) {
                        // $('#loginDisable').removeAttr('disabled');
                        // logD.disabled = false;
                        // logL.style.display = 'none';
                        toastr.error('Failed reset password ! Please Try Again...', "ERROR");
                    }
                });
        }
    });
});