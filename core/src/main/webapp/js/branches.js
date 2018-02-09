var dataSaved ; 
$(document).ready(function() {
    localStorage.removeItem('branchId');
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/branches",
        dataType: 'json',
        async: false,
        success: function(result) {
            dataSaved = result.response_data;
            $('#branch_table').DataTable({
                "data": result.response_data,
                "columns": [
                    { "data": "id" },
                    { "data": "name" },
                    { "data": "manager_name" },
                    { "data": "phone" },
                    { "data": "email" },
                    {
                        "data": function(data, type, full) {
                            if (data.head_quarter)
                                return '<span class="fa fa-check fa-1-5x text-success ml-3" aria-hidden="true"></span>';
                            else
                                return '<span class="fa fa-times fa-1-5x text-danger ml-3" aria-hidden="true"></span>';
                        }
                    },
                    {
                        "data": function(data, type, full) {
                            return '<i class="fa fa-eye fa-1-5x cursor text-success ml-3" data-toggle="tooltip" title="View Branch" onClick="viewBranch(' + data.id + ')"></i><i class="fa fa-edit fa-1-5x cursor text-info ml-3" data-toggle="tooltip" title="Edit Branch" onClick="editBranch(' + data.id + ')"></i> <i class="fa fa-list-ul fa-1-5x cursor ml-3" data-toggle="tooltip" title="Show Counters" onClick="getCounters(' + data.id + ')"> </i>';
                        }
                    }
                ]
            });
        }
    });
});

function viewBranch(id){
}

function editBranch(id){
    _.forEach(dataSaved, function(item){
        if(item.id == id){
            localStorage.setItem('branchId', JSON.stringify(item));
            window.location.href = "edit_branch.html";
        }
    })
}

window.onhashchange = function(e) {
    e.preventDefault();

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

function logout() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/logout",
        dataType: 'json',
        async: false,
        success: function(result) {
            window.history.go(-window.history.length);
            window.location.href = "login.html";
        }
    });
    localStorage.removeItem('myCat');
}
/*function viewBranch(branchId) {
  $.ajax
      ({
        type: "GET",
        url: "/new-tally/rest/branches/"+branchId,
        dataType: 'json',
        async: false,
        success: function(result){
          window.location.href = "branch.html";
        }
      });
}
function editBranch(branchId) {
  $.ajax
      ({
        type: "GET",
        url: "/new-tally/rest/branches/"+branchId,
        dataType: 'json',
        async: false,
        success: function(result){
          window.location.href = "branch.html";
        }
      });
}*/
function getCounters(branchId) {
    localStorage.setItem('branchId', branchId);
    window.location.href = "counters.html";

}