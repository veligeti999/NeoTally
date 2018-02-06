function init() {
      $.ajax
          ({
            type: "GET",
            url: "/new-tally/rest/merchants",
            dataType: 'json',
            async: false,
            success: function(result){
              console.log(result);
              document.getElementById("merchant-name").innerHTML = result.response_data.name;
              document.getElementById("owner-name").innerHTML =  result.response_data.ownerName;
              document.getElementById("merchant-name-profile").innerHTML = "Business Name : "+result.response_data.name;
              document.getElementById("owner-name-profile").innerHTML =  "Business Name Contact : "+ result.response_data.ownerName;
              document.getElementById("merchant-pan").innerHTML = "Business PAN : "+result.response_data.pan;
              document.getElementById("phone").innerHTML = "Business Name Contact : "+result.response_data.phone;
              document.getElementById("email").innerHTML = "Business Email : "+result.response_data.email;

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