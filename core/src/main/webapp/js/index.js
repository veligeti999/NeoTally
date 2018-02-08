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

function logout() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/logout",
        dataType: 'json',
        async: false,
        success: function(result) {
            console.log(result);
            window.history.go(-window.history.length);
            window.location.href = "login.html";
        }
    });
    localStorage.removeItem('myCat');
}
window.onhashchange = function(e) {
    e.preventDefault();
}