/**
 * 抽取公共controller
 */
app.controller('baseController',function ($scope) {
    //定义一个searchEntity对象，防止空指针
    $scope.searchEntity={};

    //定义一个集合存储当前选中的id
    $scope.selectIds=[];

    //给复选框一个点击事件，如果是勾选，则将勾选的ID加入到$scope.selectIds=[]。
    //				    如果是取消勾选，则将该ID从$scope.selectIds=[]移除。
    $scope.updateSelection=function ($event,id) {
        //如果是勾选，则将勾选的ID加入到$scope.selectIds=[]。
        if($event.target.checked){
            //往集合中添加数据使用push
            $scope.selectIds.push(id);
        }else{
            //获取ID在集合中的下标
            var index = $scope.selectIds.indexOf(id);

            //移除对应下标的数据,splice表示将集合中的对应下标数据移除一次
            $scope.selectIds.splice(index,1);
        }
        console.log($scope.selectIds);
    }

    /***
     * 分页控件配置
     * currentPage:当前页
     * totalItems:共有多少条记录
     * itemsPerPage:每页显示多少条
     * perPageOptions:每页多少条选项条
     * onChange:参数发生变化时执行
     * */
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            //监控paginationConf参数的变化:当分页参数发生变化，我们可以执行分页查询
            $scope.getPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        }
    };
});
