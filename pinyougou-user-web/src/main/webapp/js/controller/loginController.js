//登录获取当前登陆人
app.controller('loginController',function ($scope,loginService) {
    $scope.showName=function () {
        loginService.showName().success(function (response) {
            $scope.userName = response;
        })
    }
})