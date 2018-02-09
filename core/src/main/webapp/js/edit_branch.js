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
        }
    });
}
init();

var branchData = {};
var data = JSON.parse(localStorage.getItem('branchId'));
branchData.name = document.getElementById('name');
branchData.name.value = data.name;
branchData.managerName = document.getElementById('manager-name');
branchData.managerName.value= data.manager_name;
branchData.email = document.getElementById('email');
branchData.email.value = data.email;
branchData.phone = document.getElementById('phone');
branchData.phone.value = data.phone;
branchData.addr = document.getElementById('address');
branchData.addr.value = data.address.address;
branchData.city = document.getElementById('city');
branchData.city.value = data.address.city;
branchData.state = document.getElementById('state');
branchData.state.value = data.address.state;
branchData.country = document.getElementById('country');
branchData.country.value = data.address.country;
branchData.code = document.getElementById('zip');
branchData.code.value = data.address.zip;
// branchData.status = document.getElementById('status');
// branchData.status.value = data.active;//data.status;

var editBranchLoader = document.getElementById('editBranchLoader');
var editBranchSubmit = document.getElementById('editBranchSubmit');
editBranchLoader.style.display = 'none';

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
    localStorage.removeItem('branchId');
}
window.onhashchange = function(e) {
    e.preventDefault();
}

function cancelEdit(){
    localStorage.removeItem('branchId');
    window.location.href = "branches.html";
}
$(function() {
    // Initialize form validation on the registration form.
    // It has the name attribute "registration"
    $("form[name='registerBranch']").validate({
        // Specify validation rules
        rules: {
            // The key name on the left side is the name attribute
            // of an input field. Validation rules are defined
            // on the right side
            name: "required",
            managerName: "required",
            email: {
                required: true,
                // Specify that email should be validated
                // by the built-in "email" rule
                email: true
            },
            phone: {
                required: true,
                digits: true,
                minlength: 10,
                maxlength: 11
            },
            address: "required",
            city: "required",
            state: "required",
            country: "required",
            zip: "required",
            status: "required"
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
            editBranchSubmit.setAttribute('disabled', true);
            editBranchLoader.style.display = 'block';
            var postJson = {};
            postJson.id = data.id;
            postJson.merchant_id = data.merchant_id;
            postJson.name = document.getElementById('name').value;
            postJson.manager_name = document.getElementById('manager-name').value;
            postJson.phone = document.getElementById('phone').value;
            postJson.email = document.getElementById('email').value;
            postJson.branch_no = data.branch_no;
            var address = {};
            address.address = document.getElementById('address').value;
            address.city = document.getElementById('city').value;
            address.state = document.getElementById('state').value;
            address.country = document.getElementById('country').value;
            address.zip = document.getElementById('zip').value;
            // postJson.active = document.getElementById('status').value;
            postJson.address = address;
            console.log(postJson);
            $.ajax({
                type: "PUT",
                url: "/new-tally/rest/merchants/branch",
                dataType: 'json',
                async: false,
                data: JSON.stringify(postJson),
                headers: {
                    "Content-Type": "application/json"
                },
                success: function(result) {
                    console.log(result);
                    if(result.response_code == 0) {
                        toastr.success(result.response_message, "SUCCESS");
                        setTimeout(function() {
                            localStorage.removeItem('branchId');
                            window.location.href = "branches.html";
                        }, 1000);
                    } else {
                        $('#editBranchSubmit').removeAttr('disabled');
                        editBranchLoader.style.display = 'none';
                        toastr.error(result.response_message, "ERROR");
                    }
                },
                error: function(error) {
                    $('#editBranchSubmit').removeAttr('disabled');
                    editBranchLoader.style.display = 'none';
                    toastr.error('Something went wrong!', "ERROR");
                }
            });
        }
    });
});
