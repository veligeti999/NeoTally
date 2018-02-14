if (localStorage.getItem('myCat')) {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/logout",
        dataType: 'json',
        async: false
    });
}
localStorage.clear();

$(document).ajaxSuccess(function(event, request, settings) {
    $("#msg").append("<li>Successful Authorization!</li>");
});


$(document).ajaxError(function(event, request, settings) {
    $("#msg").append("<li>Error in login</li>");
});

var logD = document.getElementById('loginDisable');
var logL = document.getElementById('showLoginLoader');
// logD.setAttribute("disabled", false);
logL.style.display = 'none';

$(function() {
    // Initialize form validation on the registration form.
    // It has the name attribute "registration"
    $("form[name='loginForm']").validate({
        // Specify validation rules
        rules: {
            // The key name on the left side is the name attribute
            // of an input field. Validation rules are defined
            // on the right side
            username: {
                required: true,
                // Specify that email should be validated
                // by the built-in "email" rule
                email: true
            },
            password: {
                required: true
            },
            inlineRadioOptions: {
                required: true
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
            logD.setAttribute('disabled', true);
            logL.style.display = 'block';
            var username = document.getElementById('username').value;
            var password = document.getElementById('password').value;
            var usertype = $('input[name=inlineRadioOptions]:checked').val();
            localStorage.setItem('usertype', usertype);
            if (usertype == "mrct") {
                $.ajax({
                    type: "GET",
                    url: "/new-tally/rest/merchants",
                    dataType: 'json',
                    async: false,
                    headers: {
                        "Authorization": "Basic " + btoa(usertype + ":" + username + ":" + password)
                    },
                    success: function(result) {
                               if (result.response_code == 0) {
                                    localStorage.setItem('myCat', 'Tom');
                                    toastr.success('Login Successful', "SUCCESS");
                                    setTimeout(function() {
                                        // window.history.go(-window.history.length);
                                        window.location.href = "index.html";
                                    }, 1000);
                                } else {
                                    $('#loginDisable').removeAttr('disabled');
                                    logL.style.display = 'none';
                                    toastr.error('Login Un-Successful! Please Try Again...', 'ERROR');
                                }
                    },
                    error: function(error) {
                        $('#loginDisable').removeAttr('disabled');
                        // logD.disabled = false;
                        logL.style.display = 'none';
                        toastr.error('Invalid credentails ! Please Try Again...', "ERROR");
                    }
                });
            } else {
                $.ajax({
                    type: "GET",
                    url: "/new-tally/rest/branches",
                    dataType: 'json',
                    async: false,
                    headers: {
                        "Authorization": "Basic " + btoa(usertype + ":" + username + ":" + password)
                    },
                    success: function(result) {
                                if (result.response_code == 0) {
                                    localStorage.setItem('myCat', 'Tom');

                                    toastr.success('Login Successful', "SUCCESS");
                                    setTimeout(function() {
                                        // window.history.go(-window.history.length);
                                        window.location.href = "branch_index.html";
                                    }, 1000);
                                } else {
                                    $('#loginDisable').removeAttr('disabled');
                                    logL.style.display = 'none';
                                    toastr.error('Login Un-Successful! Please Try Again...', 'ERROR');
                                }
                    },
                    error: function(error) {
                        $('#loginDisable').removeAttr('disabled');
                        logL.style.display = 'none';
                        toastr.error('Invalid credentails ! Please Try Again...', "ERROR");
                    }
                });
            }
        }
    });
});