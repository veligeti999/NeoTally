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

          $.ajax
          ({
            type: "GET",
            url: "/new-tally/rest/merchants/currency/discounts",
            dataType: 'json',
            async: false,
            success: function(result){
              console.log(result);
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
    window.onhashchange = function(e) {
      e.preventDefault();
    }

$(document).ready(function () {
    // EXTRACT JSON DATA.
    var data=new Object();
    function getConfigs(){
    $.ajax
          ({
            type: "GET",
            url: "/new-tally/rest/merchants/currency/discounts",
            dataType: 'json',
            async: false,
            success: function(result){
              data =result.response_data;
              console.log(result);
               $.each(result.response_data, function (index, value) {
                console.log("value.currency_name:::"+value.currency_name+" value.percentage:::"+ value.percentage);
            // APPEND OR INSERT DATA TO SELECT ELEMENT.
              $('#config-discounts').append('<div class="form-group row"><label for="example-text-input" class="col-2 col-form-label">'+value.currency_name+ '  <span style="color: red;">'+ value.percentage+'%</span></label><div class="col-4"><input  id="'+value.id+'" style="background: green; color: #fff;" class="form-control" type="text" value="Edit" id="example-text-input"></div></div>');
          });
             $('#config-discounts').append('<div class="form-group row"><label for="example-text-input" class="col-2 col-form-label"></label><div class="col-10"><div style="margin-top: 10px;" class="form-group"><button type="submit"  id="save" class="btn btn-primary">Save</button></div></div></div>');
          
          }
        }); 
        } 
      getConfigs();

      $('#save').click(function() {
       $.each(data, function (index, item) {
        var value=$('#'+item.id).val();
        console.log("typeof value", typeof value);
        if(!(typeof value === "undefined") && value !="Edit"){
        item.percentage=value;
        console.log("item:::", item);
        $.ajax
          ({
            type: "POST",
            url: "/new-tally/rest/merchants/currency/discounts",
            dataType: 'json',
            async: false,
            data: JSON.stringify(item),
            success: function(result){
              console.log(result);
              item=result.response_data;
            }
          });
        }
       });
       $('#config-discounts').empty();
      getConfigs();
    });
   
    });