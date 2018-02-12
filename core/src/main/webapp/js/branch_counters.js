var counterData;
localStorage.removeItem('EC');
$(document).ready(function() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/branches/counters",
        dataType: 'json',
        async: false,
        success: function(result) {
            counterData = result.response_data;
            console.log(result.response_data);
            $('#branch_table').DataTable({
                "data": result.response_data,
                "columns": [
                    { "data": "id" },
                    { "data": "name" },
                    { "data": "phone" },
                    { "data": "email" },
                    { "data": function(data, type, full) {
                            if(data.active) {
                               return '<i class="fa fa-check fa-1-5x cursor text-success ml-3" data-toggle="tooltip" title="Active"></i>';
                            } else {
                               return '<i class="fa fa-times fa-1-5x cursor text-danger ml-3" data-toggle="tooltip" title="In Active"></i>';
                            }
                        } 
                    },
                    {
                        "data": function(data, type, full) {
                            return '<i class="fa fa-eye fa-1-5x cursor text-success ml-3" data-toggle="tooltip" title="View Counter" onClick="viewCounter(' + data.id + ')"></i> <i class="fa fa-edit fa-1-5x cursor text-info ml-3" data-toggle="tooltip" title="Edit Counter" onClick="editCounter(' + data.id + ')"></i>';
                        }
                    }
                ]
            });
        }
    });
});

window.onhashchange = function(e) {
    e.preventDefault();
}

function editCounter(id){
    _.forEach(counterData, function(item) {
        if(id == item.id){
            localStorage.setItem('EC', JSON.stringify(item));
            window.location.href = 'branch_add_counter.html';
        }
    })
}

function viewCounter(argument) {
    // body...
}

function init() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/branches",
        dataType: 'json',
        async: false,
        success: function(result) {
            console.log(result);
            document.getElementById("branch-name").innerHTML = result.response_data.name;
            //document.getElementById("merchant-name-footer").innerHTML = result.response_data.name;
            document.getElementById("manager-name").innerHTML = result.response_data.managerName;
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