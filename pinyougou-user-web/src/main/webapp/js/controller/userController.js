/**
* @Description userController
* @Author  guanx
* @Date   2020/3/1 20:15
* @Param  
* @Return      
* @Exception   
* 
*/
app.controller('userController',function ($scope,userService) {

    //注册用户
    $scope.reg=function () {
        userService.reg($scope.entity,$scope.code).success(function (response) {
            alert(response.message);
        })
    }

    //发送验证码
    $scope.createCode=function () {
        userService.createCode($scope.entity.phone).success(function (response) {
            alert(response.message);
        })
    }
    
})