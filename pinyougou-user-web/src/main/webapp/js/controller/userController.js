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
        userService.reg($scope.entity).success(function (response) {
            alert(response.message);
        })
    }
    
})