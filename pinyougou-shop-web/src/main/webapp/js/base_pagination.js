//创建angularjs模块
var app = angular.module('pinyougou',['pagination']);

//angular路由规则
app.config(['$locationProvider',function ($locationProvider) {
    $locationProvider.html5Mode(true);
}]);