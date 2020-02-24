app.controller('searchController',function ($scope,searchService) {

    //定义一个存储数据，用于存储选择的筛选条件
    $scope.searchMap={"keyword":"","category":"","brand":"",spec:{}};

    //搜索条件移除
    $scope.removeItemSearch=function(key){
        if(key == 'category' || key == 'brand'){
            $scope.searchMap[key] = '';
        }else{
            delete $scope.searchMap.spec[key];
        }
        //搜索
        $scope.search();
    }

    //点击分类时候，将选中分类记录  点击品牌时候，将选中分类记录  点击规格时候，将选中分类记录
    $scope.addIteamSearch=function(key,value){

        if(key == 'category' || key == 'brand'){
            $scope.searchMap[key] = value;
        }else{
            $scope.searchMap.spec[key] = value;
        }
        //搜索
        $scope.search();
    }

    //搜索方法
    $scope.search=function () {
        searchService.search($scope.searchMap).success(function (response) {
           $scope.resultMap=response;
        });
    }
})