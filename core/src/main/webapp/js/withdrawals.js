$(document).ready(function() {
    var response = {};
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/withdrawals",
        dataType: 'json',
        async: false,
        success: function(result) {
            console.log(result.response_data);
            response.data = result.response_data;
            $('#withdrawals_table').DataTable({
                "data": response.data,
                "columns": [
                    { "data": "id" },
                    { "data": "currencyCode" },  
                    { "data": "walletAddress" },
                    { "data": "transactionAmount" },
                    { "data": "commissionAmount" },
                    { "data": function(data, type, full) { return moment(new Date(data.transactionDate)).format("DD-MMM-YY, h:mm:ss a"); } },
                     { "data": function(data, type, full) {
                      if(!data.status){
                        return 'Pending <i class="fa fa-clock-o" data-toggle="tooltip" title="Pending" style="margin-left: 5px"></i>';//' <img src="images/icons/cancel.png">';
                      }else{
                        return 'Success <i class="fa fa-check text-success" data-toggle="tooltip" title="Success" style="margin-left: 5px"></i>';//' <img src="images/icons/right.png">';
                      }
                    } }
                ]
            });
        },
         error: function(error) {
          timeoutSession(error);
        }
    });
});

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
        },
         error: function(error) {
          timeoutSession(error);
        }
    });
}
init();