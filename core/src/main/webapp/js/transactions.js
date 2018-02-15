$(document).ready(function() {
    var response = {};
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/transactions",
        dataType: 'json',
        async: false,
        success: function(result) {
            console.log(result.response_data);
            response.data = result.response_data;
            $('#branch_table').DataTable({
                "data": response.data,
                "columns": [
                    { "data": "id" },
                    { "data": "currencyCode" },
                    { "data": "currencyAmount" },
                    { "data": "paymentAmount" },
                    { "data": "discountAmount" },
                    { "data": function(data, type, full) { return moment(new Date(data.createdDate)).format("DD-MMM-YY HH:mm:SS"); } },
                    { "data": function(data, type, full) {
                      if(data.status=="Pending"){
                        return data.status + '<i class="fa fa-clock-o" data-toggle="tooltip" title="Pending" style="margin-left: 5px"></i>';//' <img src="images/icons/cancel.png">';
                      }else{
                        return data.status + '<i class="fa fa-check text-success" data-toggle="tooltip" title="Success" style="margin-left: 5px"></i>';;//' <img src="images/icons/right.png">';
                      }
                    } },
                    {
                        "data": function(data, type, full) {
                            return '<span style="margin-left: 10px;"><button type="button" class="btn btn-primary disabled mr-2" data-toggle="tooltip" title="View Details" style="cursor: not-allowed">Details</button></span>';
                        }
                    }
                ]
            });
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
        }
    });
}
init();