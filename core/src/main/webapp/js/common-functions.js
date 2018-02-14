function checkSession() {
    if (!localStorage.getItem('myCat')) {
        toastr.error('Invalid Session! Please login again..', 'ERROR');
        setTimeout(function() {
            window.location.href = 'login.html'
            return false;
        }, 1000);
    }
}
checkSession();

function logout() {
    $.ajax({
        type: "GET",
        url: "/new-tally/rest/merchants/logout",
        dataType: 'json',
        async: false,
        success: function(result) {
            localStorage.clear();
            window.location.href = "login.html";
        }
    });
    localStorage.removeItem('myCat');
}

window.onhashchange = function(e) {
    e.preventDefault();
}