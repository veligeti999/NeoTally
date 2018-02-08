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
              document.getElementById("manager-name").innerHTML =  result.response_data.managerName;
              document.getElementById("branch-name-profile").innerHTML = "Branch Name : "+result.response_data.name;
              document.getElementById("manager-name-profile").innerHTML =  "Branch Name Contact : "+ result.response_data.managerName;
              document.getElementById("phone").innerHTML = "Branch Name Contact : "+result.response_data.phone;
              document.getElementById("email").innerHTML = "Branch Email : "+result.response_data.email;

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
      /*var oldURL = e.oldURL.split('#')[1];
      var newURL = e.newURL.split('#')[1];

      if (oldURL == 'share') {
        $('.share').fadeOut();
        
        return false;
      }*/
      //console.log('old:'+oldURL+' new:'+newURL);
    }