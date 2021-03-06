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
        },
         error: function(error) {
          timeoutSession(error);
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
        },
         error: function(error) {
          timeoutSession(error);
        }
      });
}
init();

function withdraw(currencyId){
  $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/withdraw/"+currencyId,
        dataType: 'json',
        async: false,
        success: function(result){
          if(result.response_code == 0) {
            toastr.success(result.response_message, "SUCCESS");
             setTimeout(function() {
                window.location.href = "index.html";
              }, 1000);
            } else {
              toastr.error(result.response_message, "ERROR");
            }
        },
        error: function(error) {
          timeoutSession(error);
        }
      });
}

function configureWallet(currencyId) {
  window.location.href = "profile.html?activeTab=menu1";
}