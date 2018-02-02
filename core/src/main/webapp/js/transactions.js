 $(document).ready(function () {
                                                    var response={};
                                                    $.ajax
                                                          ({
                                                            type: "GET",
                                                            url: "/new-tally/rest/merchants/transactions",
                                                            dataType: 'json',
                                                            async: false,
                                                            success: function(result){
                                                              console.log(result.response_data);
                                                              response.data=result.response_data;
                                                              $('#branch_table').DataTable(
                                                                {
                                                                "data": response.data,
                                                                "columns": [
                                                                    { "data": "id" },
                                                                    { "data": "currencyCode" },
                                                                    { "data": "currencyAmount" },
                                                                    { "data": "discountAmount" },
                                                                    { "data": "status" },
                                                                     { "data": function(data, type, full) {
                                                                            return '<img src="images/icons/right.png"> <span style="margin-left: 10px;"><button type="button" class="btn btn-primary mr-2">Details</button></span>';
                                                                          }
                                                                      }
                                                                ]
                                                            });
                                                            }
                                                          });
                                                    

                                                });

                                                window.onhashchange = function(e) {
      e.preventDefault();
      /*var oldURL = e.oldURL.split('#')[1];
      var newURL = e.newURL.split('#')[1];

      if (oldURL == 'share') {
        $('.share').fadeOut();
        
        return false;
      }*/
      //console.log('old:'+oldURL+' new:'+newURL);
    }
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
   