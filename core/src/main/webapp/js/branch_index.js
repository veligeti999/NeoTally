function init() {
      $.ajax
          ({
            type: "GET",
            url: "/new-tally/rest/branches",
            dataType: 'json',
            async: false,
            success: function(result){
              console.log(result);
              document.getElementById("branch-name").innerHTML = result.response_data.name;
              document.getElementById("merchant-name-footer").innerHTML = result.response_data.name;
              document.getElementById("manager-name").innerHTML = result.response_data.managerName;
            }
          });
    }
    init();
    function logout() {
      $.ajax
          ({
            type: "GET",
            url: "/new-tally/rest/merchants/logout",
            dataType: 'json',
            async: false,
            success: function(result){
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