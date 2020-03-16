app.service("payService",function($http){
    //创建二维码
    this.createNative=function () {
        return $http.get('/pay/createNative.shtml');
    }

    //查询支付状态
    this.queryPayStatus=function (out_trade_no) {
        return $http.get('/pay/queryPayStatus.shtml?out_trade_no='+out_trade_no);
    }
});