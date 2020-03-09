app.controller('itemController',function ($scope,$http) {

    //定义变量，存储当前用户选择的规格
    $scope.specList={};

    //定义默认sku
    $scope.sku={};

    $scope.num=1;

    //加入购物车
    $scope.addCart = function(){

        //发送请求，执行购物车增加
        $http.post('http://localhost:18093/cart/add.shtml?itemId=' + $scope.sku.id + '&num=' + $scope.num,{'withCredentials':true}).success(function (response) {
            if(response.success){

                //跳转到购物车列表
                location.href = 'http://localhost:18093/cart.html';

            }else{
                alert(response.message);
            }
        });
    }

    //购买数量加减
    $scope.addNum = function(num){
        $scope.num = parseInt($scope.num) + parseInt(num);
        if($scope.num<=0){
            $scope.num=1;
        }
    }

    //加载默认规格
    $scope.loadDefaultSku=function(){
        $scope.specList =angular.fromJson(itemsList[0].spec);
        //默认item=默认sku
        $scope.sku=angular.copy(itemsList[0]);
    }

    //创建方法，用户点击规格时调用，并记录用户选择规格
    $scope.selectSpec=function (key,value) {
        $scope.specList[key] = value;

        //判断当前选中规格属于第几个商品，然后加载这个商品作为默认的sku
        for(var i=0;i<itemsList.length;i++){
            //获取第几个spec
            var currentSpec = angular.fromJson(itemsList[i].spec);
            if(angular.equals($scope.specList,currentSpec)){
                //当前被循环的itemList对象就是选中的SKU
                $scope.sku=angular.copy(itemsList[i]);
            }
        }
    }

    //判断用户是否选中了某规格
    $scope.isSelectedSpec=function (key,value) {

        if($scope.specList[key]==value){
            return 'selected';
        }

    }
})