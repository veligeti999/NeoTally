function register() {
      var postJson={};
      postJson.name = document.getElementById('name').value;
      postJson.password = document.getElementById('password').value;
      postJson.ownerName = document.getElementById('owner-name').value;
      postJson.pan = document.getElementById('pan').value;
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
            url: "/new-tally/rest/merchants/register",
            dataType: 'json',
            async: false,
            data: JSON.stringify(postJson),
             headers: {
              "Content-Type": "application/json"
            },
            success: function(result){
              console.log(result);
              window.location.href = "login.html";
            }
          });
    }