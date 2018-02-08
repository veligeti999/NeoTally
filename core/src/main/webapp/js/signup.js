$(function() {
    // Initialize form validation on the registration form.
    // It has the name attribute "registration"
    var signupD = document.getElementById('submitDisable');
    var signupL = document.getElementById('showSubmitLoader');
    signupD.setAttribute("disabled", false);
    signupL.style.display = 'none';
    $("form[name='registration']").validate({
        // Specify validation rules
        rules: {
            // The key name on the left side is the name attribute
            // of an input field. Validation rules are defined
            // on the right side
            name: "required",
            ownerName: "required",
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
            zip: "required"
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
            signupD.disabled = true;
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
                    toastr.success('Successfully Registered!', "SUCCESS");
                    $setTimeout(function() {
                        window.location.href = "login.html";
                    }, 1000);
                }, error: function(error) {
                    signupD.disabled = false;
                    signupL.style.display = 'none';
                    toastr.error('Something Went Wrong!' 'ERROR');
                }
            });
        }
    });
});