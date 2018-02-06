if(localStorage.getItem('myCat')){
        window.location.href = "index.html";
      }

$( document ).ajaxSuccess(function( event, request, settings ) {
  $( "#msg" ).append( "<li>Successful Authorization!</li>" );
   });


$( document ).ajaxError(function( event, request, settings ) {
  $( "#msg" ).append( "<li>Error in login</li>" );
});

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
      var username = document.getElementById('username').value;
      var password = document.getElementById('password').value;
      var usertype = $('input[name=inlineRadioOptions]:checked').val();
      console.log(username+":::"+password+"::::"+usertype)
      $.ajax
          ({
            type: "GET",
            url: "/new-tally/rest/merchants",
            dataType: 'json',
            async: false,
            headers: {
              "Authorization": "Basic " + btoa(usertype+":"+username + ":" + password)
            },
            success: function(result){
              console.log(result);
              localStorage.setItem('myCat', 'Tom');
              window.history.go(-window.history.length);
              window.location.href = "index.html";
            }
          });
    }
  });
});