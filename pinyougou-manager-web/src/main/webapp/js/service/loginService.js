//获取用户登录名
app.service('loginService',function ($http) {
    //查询用户登录名信息
    this.getLoginUserName = function () {
        return $http.get("/login/getUserName.shtml");
    }
});