function init() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/branches",
        dataType: 'json',
        async: false,
        success: function(result) {
            console.log(result);
            document.getElementById("branch-name").innerHTML = result.response_data.name;
            document.getElementById("manager-name").innerHTML = result.response_data.managerName;
            document.getElementById("branch-name-profile").innerHTML = "Branch Name : " + result.response_data.name;
            document.getElementById("manager-name-profile").innerHTML = "Branch Name Contact : " + result.response_data.managerName;
            document.getElementById("phone").innerHTML = "Branch Name Contact : " + result.response_data.phone;
            document.getElementById("email").innerHTML = "Branch Email : " + result.response_data.email;

        }
    });
}
init();
var signupD = document.getElementById('submitDisable');
var signupL = document.getElementById('showSubmitLoader');
signupL.style.display = 'none';

$(function() {
    // Initialize form validation on the registration form.
    // It has the name attribute "registration"
    $("form[name='changePasswordForm']").validate({
        // Specify validation rules
        rules: {
            // The key name on the left side is the name attribute
            // of an input field. Validation rules are defined
            // on the right side
            currentPassword: {
                required: true
            },
            newPassword: {
                required: true,
                minlength: 5
            },
            confirmPassword: {
                required: true,
                equalTo: "#newPassword"
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

            signupD.setAttribute("disabled", false);
            signupL.style.display = 'block';
            var postJson = {};
            postJson.currentPassword = document.getElementById('currentPassword').value;
            postJson.newPassword = document.getElementById('newPassword').value;
            console.log(postJson);
            $.ajax({
                type: "POST",
                url: "/new-tally/rest/branches/change/password",
                dataType: 'json',
                async: false,
                data: JSON.stringify(postJson),
                headers: {
                    "Content-Type": "application/json"
                },
                success: function(result) {
                    console.log(result);
                    if (result.response_code == 0) {
                        localStorage.setItem('myCat', 'Tom');

                        toastr.success(result.response_message, "SUCCESS");
                        setTimeout(function() {
                            // window.history.go(-window.history.length);
                            window.location.href = "profile.html";
                        }, 1000);
                    } else {
                        checkSession();
                        $('#loginDisable').removeAttr('disabled');
                        signupL.style.display = 'none';
                        toastr.error(result.response_message, 'ERROR');
                    }

                },
                error: function(error) {
                    checkSession();
                    $('#submitDisable').removeAttr("disabled");
                    signupL.style.display = 'none';
                    toastr.error("Failed to update password", 'ERROR');
                }
            });
        }
    });
});