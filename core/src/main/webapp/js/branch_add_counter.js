function init() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/branches",
        dataType: 'json',
        async: false,
        success: function(result) {
            console.log(result);
            document.getElementById("branch-name").innerHTML = result.response_data.name;
            // document.getElementById("merchant-name-footer").innerHTML = result.response_data.name;
            document.getElementById("manager-name").innerHTML = result.response_data.managerName;
        }
    });
}
init();

var addCounterLoader = document.getElementById('addCounterLoader');
var addCounterSubmit = document.getElementById('addCounterSubmit');
addCounterLoader.style.display = 'none';
var pageTitle = document.getElementById('titleView');
var dataSaved;
var showStatus = document.getElementById('status');

if (localStorage.getItem('EC')) {
    pageTitle.innerHTML = "Edit Counter";
    dataSaved = JSON.parse(localStorage.getItem('EC'));
    var phone = document.getElementById('phone');
    phone.value = dataSaved.phone;
    var email = document.getElementById('email');
    email.value = dataSaved.email;
    var name1 = document.getElementById('counterName');
    name1.value = dataSaved.name;
    var active = document.getElementById('status');
    active.value = dataSaved.active.toString();
} else {
    showStatus.style.display = "none";
    pageTitle.innerHTML = "Add Counter";
}

function cancel() {
    localStorage.removeItem('EC');
    window.location.href = "branch_counters.html";
}

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

$(function() {
    // Initialize form validation on the registration form.
    // It has the name attribute "registration"
    $("form[name='registerCounter']").validate({
        // Specify validation rules
        rules: {
            // The key name on the left side is the name attribute
            // of an input field. Validation rules are defined
            // on the right side
            name: {
                required: true
            },
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
            /*var postJson = {};
            postJson.phone = document.getElementById('phone').value;
            postJson.email = document.getElementById('email').value;
            $.ajax({
                type: "POST",
                url: "/new-tally/rest/branches/counter/register",
                dataType: 'json',
                async: false,
                data: JSON.stringify(postJson),
                headers: {
                    "Content-Type": "application/json"
                },
                success: function(result) {
                    console.log(result);
                    window.location.href = "counters.html";
                }
            });*/
            addCounterSubmit.setAttribute('disabled', true);
            addCounterLoader.style.display = 'block';
            var postJson = {};
            postJson.phone = document.getElementById('phone').value;
            postJson.email = document.getElementById('email').value;
            postJson.name = document.getElementById('counterName').value;
            if (localStorage.getItem('EC')) {
                postJson.branchId = dataSaved.branchId;
                postJson.id = dataSaved.id;
                postJson.active = document.getElementById('status').value;
                $.ajax({
                    type: "PUT",
                    url: "/new-tally/rest/branches/counter/update",
                    dataType: 'json',
                    async: false,
                    data: JSON.stringify(postJson),
                    headers: {
                        "Content-Type": "application/json"
                    },
                    success: function(result) {
                        if (result.response_code == 0) {
                            toastr.success(result.response_message, "SUCCESS");
                            setTimeout(function() {
                                localStorage.removeItem('EC');
                                window.location.href = "branch_counters.html";
                            }, 1000);
                        } else {
                            $('#addCounterSubmit').removeAttr('disabled');
                            addCounterLoader.style.display = 'none';
                            toastr.error(result.response_message, "ERROR");
                        }
                    },
                    error: function(error) {
                        $('#addCounterSubmit').removeAttr('disabled');
                        addCounterLoader.style.display = 'none';
                        toastr.error('Something went wrong!', "ERROR");
                    }
                });
            } else {
               postJson.branchId = localStorage.getItem("branchId");
                $.ajax({
                    type: "POST",
                    url: "/new-tally/rest/branches/counter/register",
                    dataType: 'json',
                    async: false,
                    data: JSON.stringify(postJson),
                    headers: {
                        "Content-Type": "application/json"
                    },
                    success: function(result) {
                        if (result.response_code == 0) {
                            toastr.success(result.response_message, "SUCCESS");
                            setTimeout(function() {
                                window.location.href = "branch_counters.html";
                            }, 1000);
                        } else {
                            $('#addCounterSubmit').removeAttr('disabled');
                            addCounterLoader.style.display = 'none';
                            toastr.error(result.response_message, "ERROR");
                        }
                    },
                    error: function(error) {
                        $('#addCounterSubmit').removeAttr('disabled');
                        addCounterLoader.style.display = 'none';
                        toastr.error('Something went wrong!', "ERROR");
                    }
                });
            }

        }
    });
});

function registerCounter() {

}