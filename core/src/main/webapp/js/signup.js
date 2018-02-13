if (localStorage.getItem('myCat')) {
    localStorage.removeItem('myCat');
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/logout",
        dataType: 'json',
        async: false,
        success: function(result) {
            window.location.href = "login.html";
        }
    });

}
$(document).ready(function() {
  $.validator.addMethod("pan", function(value, element) 
  { 
  return this.optional(element) || /^([A-Z]{5})([0-9]){4}([A-Z]{1})$/i.test(value); 
  }, "Please enter a valid PAN.");
});
$(function() {
    // Initialize form validation on the registration form.
    // It has the name attribute "registration"
    var signupD = document.getElementById('submitDisable');
    var signupL = document.getElementById('showSubmitLoader');
    signupL.style.display = 'none';
    $("form[name='registration']").validate({
        // Specify validation rules
        rules: {
            // The key name on the left side is the name attribute
            // of an input field. Validation rules are defined
            // on the right side
            name: "required",
            ownerName: "required",
            pan: "required pan",
            email: {
                required: true,
                // Specify that email should be validated
                // by the built-in "email" rule
                email: true
            },
            password: {
                required: true,
                minlength: 5
            },
            confirmPassword: {
                required: true,
                minlength: 5,
                equalTo: "#password"
            },
            phone: {
                required: true,
                digits: true,
                minlength: 10,
                maxlength: 11
            },
            address: "required",
            city: "required",
            state: "required",
            country: "required",
            zip:  {
                required: true,
                digits: true,
                minlength: 6,
                maxlength: 6
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
            signupD.setAttribute("disabled", true);
            signupL.style.display = 'block';
            var postJson = {};
            postJson.name = document.getElementById('name').value;
            postJson.password = document.getElementById('password').value;
            postJson.ownerName = document.getElementById('owner-name').value;
            postJson.pan = document.getElementById('pan').value;
            postJson.phone = document.getElementById('phone').value;
            postJson.email = document.getElementById('email').value;
            var address = {};
            address.address = document.getElementById('address').value;
            address.city = document.getElementById('city').value;
            address.state = document.getElementById('state').value;
            address.country = document.getElementById('country').value;
            address.zip = document.getElementById('zip').value;
            postJson.address = address;
            console.log(postJson);
            $.ajax({
                type: "POST",
                url: "/new-tally/rest/merchants/register",
                dataType: 'json',
                async: false,
                data: JSON.stringify(postJson),
                headers: {
                    "Content-Type": "application/json"
                },
                success: function(result) {
                    console.log(result);
                    if (result.response_code == 0) {
                        toastr.success(result.response_message, "SUCCESS");
                        setTimeout(function() {
                            window.location.href = "login.html";
                        }, 1000);
                    } else {
                        $('#submitDisable').removeAttr('disabled');
                        signupL.style.display = 'none';
                        toastr.error(result.response_message, "ERROR");
                    }
                },
                error: function(error) {
                    $('#submitDisable').removeAttr('disabled');
                    signupL.style.display = 'none';
                    toastr.error('Something Went Wrong!', 'ERROR');
                }
            });
        }
    });
});