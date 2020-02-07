//获取当前登录用户名
app.controller('loginController',function ($scope,$controller,loginService) {

    //继承父控制器
    $controller("baseController",{$scope:$scope});

    //获取用户名
    $scope.getUserName = function () {
        loginService.getLoginUserName().success(function (response) {
            $scope.userLoginName = response.loginUserName;
        })
    }
});