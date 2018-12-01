'use strict';
angular.module('ec-admin.auth', [])

.controller('LoginCtrl', [

    '$scope',
    'Session',
    'Patterns',
    'APIStatus',
    'toastr',
    'Util',
    '$state',

    function ($scope, Session, Patterns, APIStatus, toastr, Util, $state) {
        // Checking admin already login
        if (Session.getAccessToken() && Session.getUser()){
            $state.go('categories.list');
            return;
        }
        

        $scope.submitting = false;

        $scope.user = {
            username: '',
            password: ''
        };

        $scope.registerConsoleUser = function () {
            $scope.submitting = true;
            console.log('admin-site',$scope.user);
            Session.consoleLogin({
                username: $scope.user.username,
                password: $scope.user.password
            }, function (response) {
                console.log('response',response);
                var status = response.status;
                if (status === 200) {
                    
                    // redirect page
                    $state.go('categories.list');
                } else {
                    //console.log('login APIStatus',APIStatus);
                    //console.log('login status',status);
                    var err = _.find(APIStatus, {status: status});
                    //console.log('login error',err);
                    if (err) {
                        toastr.error(Util.translate(err.message));
                    }
                }
            }).finally(function () {
                $scope.submitting = false;
            });
        };

        return {
            Patterns: Patterns
        };

    }
]);