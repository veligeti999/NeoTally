function checkSession() {
    if (!localStorage.getItem('myCat')) {
        toastr.error('Invaild Session! Please login again..', 'ERROR');
        setTimeout(function() {
            window.location.href = 'login.html'
            return false;
        }, 1000);
    }
}
checkSession();
function timeoutSession(error){
    if(error.status == '401'){
        if(localStorage.getItem('myCat')){
            localStorage.clear();
            toastr.error('Session Timed Out! Please login again..', 'ERROR');
            setTimeout(function() {
                window.location.href = 'login.html'
                return false;
            }, 1000);
        } else {
            return false;
        }
    }else{
      toastr.error('Something went wrong!', "ERROR");
    }
}

function logout() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/logout",
        dataType: 'json',
        async: false,
        success: function(result) {
            window.location.href = "login.html";
        }
    });
}

window.onhashchange = function(e) {
    e.preventDefault();
}