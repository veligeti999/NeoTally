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

    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/currency/discounts",
        dataType: 'json',
        async: false,
        success: function(result) {
            console.log(result);
        }
    });
}
init();
var data;
var inputs={btc:'', ltc:''};
var submitButton = document.getElementById('discountSubmit');
var editButton = document.getElementById('discountEdit');
var editBtcInput = document.getElementById('bitcoinDiscount');
var editLtcInput = document.getElementById('litecoinDiscount');

function disableImputs() {
    submitButton.style.display = 'none';
    editButton.style.display = 'block';
    editBtcInput.setAttribute('disabled', true);
    editLtcInput.setAttribute('disabled', true);
}
disableImputs();


function editData() {
    editBtcInput.value = inputs.btc;
    editLtcInput.value = inputs.ltc;
    submitButton.style.display = 'block';
    editButton.style.display = 'none';
    $('#bitcoinDiscount').removeAttr('disabled');
    $('#litecoinDiscount').removeAttr('disabled');
}

function logout() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/logout",
        dataType: 'json',
        async: false,
        success: function(result) {
            console.log(result);
            window.location.href = "login.html";
        }
    });
    localStorage.removeItem('myCat');
}
window.onhashchange = function(e) {
    e.preventDefault();
}

// EXTRACT JSON DATA.
function getConfigs() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/currency/discounts",
        dataType: 'json',
        async: false,
        success: function(result) {
            data = result.response_data;
            console.log(result);
            $.each(result.response_data, function(index, value) {
                console.log("value.currency_name:::" + value.currency_name + " value.percentage:::" + value.percentage);
                if (value.currency_code == 'BTC') {
                    inputs.btc = value.percentage;
                    console.log(editBtcInput);
                    editBtcInput.value = value.percentage + ' %';
                }
                if (value.currency_code == 'LTC') {
                    inputs.ltc = value.percentage;
                    console.log(editLtcInput);

                    editLtcInput.value = value.percentage + ' %';
                }
                // APPEND OR INSERT DATA TO SELECT ELEMENT.
                // $('#config-discounts').append('<div class="form-group row"><label for="example-text-input" class="col-2 col-form-label">' + value.currency_name + '  <span style="color: red;">' + value.percentage + '%</span></label><div class="col-4"><input  id="' + value.id + '" style="background: green; color: #fff;" class="form-control" type="text" value="Edit" id="example-text-input"></div></div>');
            });
            // $('#config-discounts').append('<div class="form-group row"><label for="example-text-input" class="col-2 col-form-label"></label><div class="col-10"><div style="margin-top: 10px;" class="form-group"><button type="submit"  id="save" class="btn btn-primary">Save</button></div></div></div>');
        }
    });
}
getConfigs();

function save() {
    $.each(data, function(index, item) {
        if (item.currency_code == 'BTC') {
            var btcDiscount = document.getElementById('bitcoinDiscount').value;
            if (typeof btcDiscount != 'undefined' && btcDiscount != '') {
                item.percentage = btcDiscount;
            }
        } else if (item.currency_code == 'LTC') {
            var ltcDiscount = document.getElementById('litecoinDiscount').value;
            if (typeof ltcDiscount != 'undefined' && ltcDiscount != '') {
                item.percentage = ltcDiscount;
            }
        }
        $.ajax({
            type: "POST",
            url: "/new-tally/rest/merchants/currency/discounts",
            dataType: 'json',
            async: false,
            data: JSON.stringify(item),
            success: function(result) {
                console.log(result);
                item = result.response_data;
            }
        });
    });
    disableImputs();
    getConfigs();
};