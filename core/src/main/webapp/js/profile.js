function init() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants",
        dataType: 'json',
        async: false,
        success: function(result) {
            console.log(result);
            document.getElementById("merchant-name").innerHTML = result.response_data.name;
            document.getElementById("owner-name").innerHTML = result.response_data.ownerName;
            document.getElementById("merchant-name-profile").innerHTML = "Business Name : " + result.response_data.name;
            document.getElementById("owner-name-profile").innerHTML = "Business Name Contact : " + result.response_data.ownerName;
            document.getElementById("merchant-pan").innerHTML = "Business PAN : " + result.response_data.pan;
            document.getElementById("phone").innerHTML = "Business Name Contact : " + result.response_data.phone;
            document.getElementById("email").innerHTML = "Business Email : " + result.response_data.email;

        },
         error: function(error) {
            timeoutSession(error);
          }        
    });
}
init();

var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};

function getWalletAddress(){
  $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/wallet/addresses",
        dataType: 'json',
        async: false,
        success: function(result) {
          if(result.response_data.length > 0){
              $.each(result.response_data, function(index, value) {
              $("#walletConfigForm").empty().append('<div class="form-group row">'+
                                          '<label for="example-text-input" class="col-2 col-form-label">Wallet Address Saved</label>'+
                                        '</div>'+
                                        '<div class="form-group row">'+
                                          '<label for="example-text-input" class="col-2 col-form-label">Bit Coin </label>'+
                                          '<div class="col-6">'+
                                              '<input class="form-control p-input" id="newAddress" name="newAddress" placeholder="Enter valid address" type="text" value='+value.wallet_address+'>'+
                                          '</div>'+
                                          '<button id="submitButton" class="btn btn-primary col-2" title="Save wallet address" data-toggle="tooltip" onClick="saveExistedWalletAddress('+value.id+')">Save</button>'+
                                          '<button id="editButton" class="btn btn-primary col-2" title="Edit wallet address" data-toggle="tooltip" onClick="editWalletAddress()">Edit</button>'+
                                      '</div>');
            });
            disableImputs();
          }
          else{
           $("#walletConfigForm").empty().append('<div class="form-group row">'+
                                        '<label for="example-text-input" class="col-2 col-form-label">No Address saved</label>'+
                                      '</div>'+
                                      '<div class="form-group row">'+
                                        '<label for="example-text-input" class="col-2 col-form-label">Bit Coin </label>'+
                                        '<div class="col-6">'+
                                            '<input class="form-control p-input" id="newAddress" name="newAddress" placeholder="Enter valid address" type="text">'+
                                        '</div>'+
                                        '<button id="submitButton" class="btn btn-primary col-2" title="Save wallet address" data-toggle="tooltip" onClick="saveWalletAddress()">Save</button>'+
                                    '</div>');
          }
        },
         error: function(error) {
          timeoutSession(error);
        }
    });
}
var signupD = document.getElementById('submitDisable');
var signupL = document.getElementById('showSubmitLoader');
signupL.style.display = 'none';

var activeTab = getUrlParameter('activeTab');
console.log("activeTab", activeTab);
if(activeTab!=undefined){
  $('a[href="#home"]').removeClass("active");;
  $('a[href="#'+ activeTab +'"]').addClass("active");
  $("#home").removeClass("active");
  $("#" + activeTab).removeClass("fade");
  $("#" + activeTab).addClass("active");
  getWalletAddress();
}

$(function() {
    // Initialize form validation on the registration form.
    // It has the name attribute "registration"
    $("form[name='changePasswordForm']").validate({
        // Specify validation rules
        rules: {
            // The key name on the left side is the name attribute
            // of an input field. Validation rules are defined
            // on the right side
            currentPassword: {
                required: true
            },
            newPassword: {
                required: true,
                minlength: 5
            },
            confirmPassword: {
                required: true,
                equalTo: "#newPassword"
            }
        },
        // Specify validation error messages
        /*messages: {
          name: "Please enter your Merchant Name",
          ownerName: "Please enter your Owner Name",
          password: {
            required: "Please provide a password",
            minlength: "Your password must be at least 5 characters long"
          },
          email: "Please enter a valid email address"
        },*/
        // Make sure the form is submitted to the destination defined
        // in the "action" attribute of the form when valid
        submitHandler: function(form) {

            signupD.setAttribute("disabled", false);
            signupL.style.display = 'block';
            var postJson = {};
            postJson.currentPassword = document.getElementById('currentPassword').value;
            postJson.newPassword = document.getElementById('newPassword').value;
            console.log(postJson);
            $.ajax({
                type: "POST",
                url: "/new-tally/rest/merchants/change/password",
                dataType: 'json',
                async: false,
                data: JSON.stringify(postJson),
                headers: {
                    "Content-Type": "application/json"
                },
                success: function(result) {
                    console.log(result);
                    if (result.response_code == 0) {
                        localStorage.setItem('myCat', 'Tom');
                        toastr.success(result.response_message, "SUCCESS");
                        setTimeout(function() {
                            // window.history.go(-window.history.length);
                            window.location.href = "profile.html";
                        }, 1000);
                    } else {
                        toastr.error(result.response_message, 'ERROR');
                    }

                },
                error: function(error) {
                    timeoutSession(error);
                  }
            });
        }
    });
});

function disableImputs() {
  var editButton = document.getElementById('editButton');
  var submitButton = document.getElementById('submitButton');
  var editBtcInput = document.getElementById('newAddress');
    submitButton.style.display = 'none';
    editButton.style.display = 'block';
    editBtcInput.setAttribute('disabled', true);
    // editLtcInput.setAttribute('disabled', true);
}


function editWalletAddress() {
    var editButton = document.getElementById('editButton');
    var submitButton = document.getElementById('submitButton');
    var editBtcInput = document.getElementById('newAddress');
    // editBtcInput.value = inputs.btc;
    // editLtcInput.value = inputs.ltc;
    submitButton.style.display = 'block';
    editButton.style.display = 'none';
    $('#newAddress').removeAttr('disabled');
    // $('#litecoinDiscount').removeAttr('disabled');
}
function saveExistedWalletAddress(data) {
  console.log("wallet", data);
    var postJson={};
        postJson.id=data;
        postJson.wallet_address=document.getElementById('newAddress').value;
        $.ajax({
              type: "POST",
              url: "/new-tally/rest/merchants/wallet/update",
              dataType: 'json',
              async: false,
              data: JSON.stringify(postJson),
              headers: {
                  "Content-Type": "application/json"
              },
              success: function(result) {
                  console.log(result);
                  if (result.response_code == 0) {
                      localStorage.setItem('myCat', 'Tom');

                      toastr.success(result.response_message, "SUCCESS");
                      getWalletAddress();
                  } else {
                     toastr.error(result.response_message, 'ERROR');
                  }

              },
               error: function(error) {
                timeoutSession(error);
              }
          });
}

function saveWalletAddress(){
        var postJson={};
        postJson.currency_id=1;
        postJson.wallet_address=document.getElementById('newAddress').value;
        $.ajax({
              type: "POST",
              url: "/new-tally/rest/merchants/wallet/save",
              dataType: 'json',
              async: false,
              data: JSON.stringify(postJson),
              headers: {
                  "Content-Type": "application/json"
              },
              success: function(result) {
                  console.log(result);
                  if (result.response_code == 0) {
                      localStorage.setItem('myCat', 'Tom');
                      toastr.success(result.response_message, "SUCCESS");
                      getWalletAddress();
                  } else {
                      toastr.error(result.response_message, 'ERROR');
                  }

              },
               error: function(error) {
                  timeoutSession(error);
                }
          });
}