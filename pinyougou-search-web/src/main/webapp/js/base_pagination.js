//创建angularjs模块
var app = angular.module('pinyougou',['pagination']);

//angular路由规则
app.config(['$locationProvider',function ($locationProvider) {
    $locationProvider.html5Mode(true);
}]);

/*$sce服务写成过滤器*/
app.filter('trustHtml',['$sce',function($sce){
    return function(data){
        return $sce.trustAsHtml(data);
    }
}]);