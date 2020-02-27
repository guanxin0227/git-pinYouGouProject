/*****
 * 定义一个控制层 controller
 * 发送HTTP请求从后台获取数据
 ****/
app.controller("contentController",function($scope,contentService){

    $scope.contentList={};

    //调用service查询广告
    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(function (response) {
            //存储所有广告
            $scope.contentList[categoryId]=response;
        })
    }

    // $scope.findByCategoryId4=function (categoryId) {
    //     contentService.findByCategoryId(categoryId).success(function (response) {
    //         //存储所有广告
    //         $scope.contentList4=response;
    //     })
    // }

    //搜索方法
    $scope.search=function () {

        location.href="http://localhost:18087?keyword=" + $scope.keyword;
    }
});
