if(localStorage.getItem('myCat')){
        window.location.href = "index.html";
      }

function login() {
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

$( document ).ajaxSuccess(function( event, request, settings ) {
  $( "#msg" ).append( "<li>Successful Authorization!</li>" );
   });


$( document ).ajaxError(function( event, request, settings ) {
  $( "#msg" ).append( "<li>Error in login</li>" );
});