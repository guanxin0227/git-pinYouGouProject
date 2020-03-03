/**
 * @Description 购物车Controller
 * @Author  guanx
 * @Date   2020/3/3 15:33
 * @Param
 * @Return
 * @Exception
 *
 */
app.controller('cartController',function ($scope,cartService) {

    //查询购物车数据
    $scope.findCartList=function () {
        cartService.findCartList().success(function (response) {
           $scope.cartList = response;

           //调用计算数量和金额
            sum($scope.cartList);
        });
    }

    //添加购物车
    $scope.addCart=function (itemId,num) {
        cartService.addCart(itemId,num).success(function (response) {
            if(response.success){
                //查询购物车数据
                $scope.findCartList();
            }else{
                alert(response.message);
            }
        })
    }

    //总金额，总件数计算
    sum = function (cartList) {

        //定义一个参数，用于储存总数量，总金额
        $scope.totalValue={totalNum:0,totalMoney:0.0};

        for(var i=0;i<cartList.length;i++){

            //cart对象
            var cart = cartList[i];

            //获取购物车商品明细表
            var itemList = cart.orderItemList;

            for(var j=0;j<itemList.length;j++){

                //总数量
                $scope.totalValue.totalNum += itemList[j].num;

                //总金额
                $scope.totalValue.totalMoney += itemList[j].totalFee;

            }
        }
    }
})