function init() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/branches",
        dataType: 'json',
        async: false,
        success: function(result) {
            console.log(result);
            document.getElementById("branch-name").innerHTML = result.response_data.name;
            document.getElementById("merchant-name-footer").innerHTML = result.response_data.name;
            document.getElementById("manager-name").innerHTML = result.response_data.managerName;
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
        success: function(result) {
            console.log(result);
            document.getElementById("coin_code").innerHTML = result.response_data.coin_code;
            document.getElementById("coin_value").innerHTML = result.response_data.coin_value;
            document.getElementById("coin_value_in_currency").innerHTML = result.response_data.coin_value_in_currency + ' INR';
        },
         error: function(error) {
          timeoutSession(error);
        }
    });
}
init();