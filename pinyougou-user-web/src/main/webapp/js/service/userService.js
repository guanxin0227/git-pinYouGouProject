/**
* @Description 用户service
* @Author  guanx
* @Date   2020/3/1 20:13
* @Param
* @Return
* @Exception
*
*/
app.service('userService',function ($http) {

    //注册功能
    this.reg=function (entity) {
       return $http.post('/user/add.shtml',entity);
    }
})