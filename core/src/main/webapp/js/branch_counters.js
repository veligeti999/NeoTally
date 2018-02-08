$(document).ready(function () {
    $.ajax
      ({
        type: "GET",
        url: "/new-tally/rest/branches/counters",
        dataType: 'json',
        async: false,
        success: function(result){
          console.log(result.response_data);
          $('#branch_table').DataTable(
            {
            "data": result.response_data,
            "columns": [
                { "data": "id" },
                { "data": "phone" },
                { "data": "email" },
                 { "data": function(data, type, full) {
                        return '<img src="images/icons/eye.png" onClick="viewBranch('+data.id+')"> <img src="images/icons/edit.png" onClick="editBranch('+data.id+')">';
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
      $.ajax
          ({
            type: "GET",
            url: "/new-tally/rest/branches",
            dataType: 'json',
            async: false,
            success: function(result){
              console.log(result);
              document.getElementById("branch-name").innerHTML = result.response_data.name;
              //document.getElementById("merchant-name-footer").innerHTML = result.response_data.name;
              document.getElementById("manager-name").innerHTML = result.response_data.managerName;
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
 
      
