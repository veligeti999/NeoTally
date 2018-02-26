var dataReceieved;
localStorage.removeItem('editCounter');
$(document).ready(function() {
    var branchId = localStorage.getItem("branchId");
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/branches/" + branchId,
        dataType: 'json',
        async: false,
        success: function(result) {
            dataReceieved = result.response_data;
            $('#branch_table').DataTable({
                "data": result.response_data,
                "columns": [
                    { "data": "name" },
                    { "data": "phone" },
                    { "data": "email" },
                    { "data": "password" },
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

function editCounter(id){
    checkSession();
    _.forEach(dataReceieved, function(item){
        if(item.id == id){
            localStorage.setItem('editCounter', JSON.stringify(item));
            window.location.href = "add_counter.html";
        }
    })
}



function init() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants",
        dataType: 'json',
        async: false,
        success: function(result) {
            document.getElementById("merchant-name").innerHTML = result.response_data.name;
            document.getElementById("owner-name").innerHTML = result.response_data.ownerName;
        }
    });
}
init();