app.controller('itemController',function ($scope) {

    //定义变量，存储当前用户选择的规格
    $scope.specList={};

    //创建方法，用户点击规格时调用，并记录用户选择规格
    $scope.selectSpec=function (key,value) {
        $scope.specList[key] = value;
    }

    //判断用户是否选中了某规格
    $scope.isSelectedSpec=function (key,value) {

        if($scope.specList[key]==value){
            return 'selected';
        }

    }
})