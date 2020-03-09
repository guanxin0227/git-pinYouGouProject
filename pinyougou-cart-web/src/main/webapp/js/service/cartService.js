/**
* @Description 购物车Service
* @Author  guanx
* @Date   2020/3/3 15:33
* @Param  
* @Return      
* @Exception   
* 
*/
app.service('cartService',function ($http) {

    //查询购物车数据
    this.findCartList=function(){
        return $http.post('/cart/cartList.shtml');
    }

    //添加购物车
    this.addCart=function (itemId,num) {
        return $http.get('/cart/add.shtml?itemId=' + itemId + '&num=' + num);
    }

    //查询用户地址
    this.getAddressList=function () {
        return $http.get('/address/user/list.shtml');
    }
})