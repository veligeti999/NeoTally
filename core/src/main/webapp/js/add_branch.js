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
            document.getElementById("owner-name").innerHTML = result.response_data.ownerName;
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

function registerBranch() {
  var postJson={};
  postJson.name = document.getElementById('name').value;
  postJson.password = document.getElementById('password').value;
  postJson.manager_name = document.getElementById('manager-name').value;
  postJson.phone = document.getElementById('phone').value;
  postJson.email = document.getElementById('email').value;
  var address={};
  address.address=document.getElementById('address').value;
  address.city=document.getElementById('city').value;
  address.state=document.getElementById('state').value;
  address.country=document.getElementById('country').value;
  address.zip=document.getElementById('zip').value;
  postJson.address=address;
  console.log(postJson);
  $.ajax
      ({
        type: "POST",
        url: "/new-tally/rest/merchants/branch",
        dataType: 'json',
        async: false,
        data: JSON.stringify(postJson),
         headers: {
          "Content-Type": "application/json"
        },
        success: function(result){
          console.log(result);
          window.location.href = "branches.html";
        }
      });
}