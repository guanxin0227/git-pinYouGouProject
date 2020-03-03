//登录
app.service('loginService',function ($http) {

    //获取用户名
    this.showName=function () {
        return $http.get('/user/getUserName.shtml');
    }
})