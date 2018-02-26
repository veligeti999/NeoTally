function init() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants",
        dataType: 'json',
        async: false,
        success: function(result) {
            console.log(result);
            document.getElementById("merchant-name").innerHTML = result.response_data.name;
            document.getElementById("merchant-name-footer").innerHTML = result.response_data.name;
            document.getElementById("owner-name").innerHTML = result.response_data.ownerName;
        }
    });
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/balance",
        dataType: 'json',
        async: false,
        success: function(result){
          console.log(result);
          document.getElementById("coin_code").innerHTML = result.response_data.coin_code;
          document.getElementById("coin_value").innerHTML = result.response_data.coin_value;
          document.getElementById("coin_value_in_currency").innerHTML = result.response_data.coin_value_in_currency+' INR';
        }
      });
}
init();

function withdraw(){
  $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/withdraw",
        dataType: 'json',
        async: false,
        success: function(result){
          if(result.response_code == 0) {
            toastr.success(result.response_message, "SUCCESS");
            } else {
              checkSession();
              toastr.error(result.response_message, "ERROR");
            }
        },
        error: function(error) {
          checkSession();
          toastr.error('Something went wrong!', "ERROR");
        }
      });
}