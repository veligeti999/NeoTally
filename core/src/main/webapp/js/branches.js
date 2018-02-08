$(document).ready(function() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/branches",
        dataType: 'json',
        async: false,
        success: function(result) {
            console.log(result.response_data);
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
                                return '<span class="fa fa-check" aria-hidden="true"></span>';
                            else
                                return '<span class="fa fa-times" aria-hidden="true"></span>';
                        }
                    },
                    {
                        "data": function(data, type, full) {
                            return '<img src="images/icons/eye.png" onClick="viewBranch(' + data.id + ')"> <img src="images/icons/edit.png" onClick="editBranch(' + data.id + ')"> <img src="images/icons/008-list.png" onClick="getCounters(' + data.id + ')">';
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
/*function viewBranch(branchId) {
  $.ajax
      ({
        type: "GET",
        url: "/new-tally/rest/branches/"+branchId,
        dataType: 'json',
        async: false,
        success: function(result){
          console.log(result);
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
          console.log(result);
          window.location.href = "branch.html";
        }
      });
}*/
function getCounters(branchId) {
    localStorage.setItem('branchId', branchId);
    window.location.href = "counters.html";

}