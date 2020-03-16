/*****
 * 定义一个控制层 controller
 * 发送HTTP请求从后台获取数据
 ****/
app.controller("payController",function($scope,$http,$controller,payService,$location){

    //继承父控制器
    $controller("baseController",{$scope:$scope});

    //获取支付金额
    $scope.getMoney=function(){
        return $location.search()['money'];
    }

    //创建二维码
    $scope.crateNative=function () {
        payService.createNative().success(function (response) {

            $scope.out_trade_no=response.out_trade_no;
            $scope.money=(response.total_fee/100).toFixed(2);
            $scope.code_url=response.code_url;

            //生成二维码
            var qr = new QRious({
                element: document.getElementById('qrious'),
                size: 250,
                value: $scope.code_url,
                level:'H'
            })
            //调用查询
            $scope.queryPayStatus($scope.out_trade_no);
        })
    }

    //查询支付状态
    $scope.queryPayStatus=function(out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(function (response) {
            if(response.success){
                //支付成功，跳转到支付成功页面
                location.href='/paysuccess.html?money='+$scope.money;
            }else if(response.message=='timeout'){
                $scope.crateNative();

            }else{
                //跳转到支付失败页
                location.href='/payfail.html';
            }
        })
    }
});