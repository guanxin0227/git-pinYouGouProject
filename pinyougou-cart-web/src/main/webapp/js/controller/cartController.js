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

    //记录用户选择收货地址
    $scope.selectAeeress=function (address) {
        $scope.addressInfo=address;
    }

    //获取用户地址
    $scope.getAddressList=function () {
        cartService.getAddressList().success(function (response) {
            $scope.addressList = response;

            //查找默认地址
            for(var i=0;i<$scope.addressList.length;i++){
                if($scope.addressList[i].isDefault=='1'){
                    $scope.addressInfo=angular.copy($scope.addressList[i]);
                }
            }
        })

    }

    //支付功能  1表示在线支付，2表示货到付款
    $scope.order={paymentType:1};

    //切换支付方式
    $scope.selectPayType=function (type) {
        $scope.order.paymentType=type;
    }

    //添加保存订单信息
    $scope.submitOrder=function () {

        //收货地址
        $scope.order.receiverAreaName=$scope.addressInfo.address;
        //收货人手机号
        $scope.order.receiverMobile=$scope.addressInfo.mobile;
        //收货人名称
        $scope.order.receiver=$scope.addressInfo.contact;

        cartService.submitOrder($scope.order).success(function (response) {
            if(response.success){
                location.href='/paysuccess.html';
            }else{
                location.href='/payfail.html';
            }
        })
    }

})