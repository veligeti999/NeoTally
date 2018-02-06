/**
 * Master Controller
 */

angular.module('tallyApp').controller('loginCtrl', ['$scope', '$cookieStore', '$rootScope', 'serviceAdmin', 'toastr', '$state', loginCtrl]);

function loginCtrl($scope, $cookieStore, $rootScope, serviceAdmin, toastr, $state) {
    $scope.login = {};
    var request = {};
    $scope.submitForm = function(isValid) {
        if (isValid) {
            var data = {
                email: $scope.user.email,
                password: $scope.user.password
            }
            login(data);
        }
    };

    function login(data) {
        request = {
            url: "/login",
            params: data
        };
        serviceAdmin.PostJson(request).then(function(response) {
            // response.code contains the corresponding HTTP Codes like 500, 401, 404 etc.
            // Success HTTP code i.e. default is 200
            if (angular.isDefined(response)) {
                    $cookieStore.put('username', response.userName);
                    $state.go('index');
            }
            else{
                toastr.error('Please enter valid credentials', 'Error');
            } 
        },function(error){
            toastr.error(error.statusMsg, 'Error');
        })
    };

    $scope.focusLoginButton = function() {
        setTimeout(function() {
            $("#loginButton").focus();
        }, 400);
    };
}