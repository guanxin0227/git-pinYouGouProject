app.controller('searchController',function ($scope,$location,searchService) {

    //定义一个存储数据，用于存储选择的筛选条件
    $scope.searchMap={"keyword":"","category":"","brand":"",spec:{},"price":"","pageNum":1,"size":10,"sort":"","sortField":""};

    //定义一个集合
    $scope.resultMap={brandList:[]};

    //加载搜索关键字
    $scope.loadKeyword=function(){
        //获取地址栏传过来的关键字
        var keyword = $location.search()['keyword'];

        //搜索操作
        if(null != keyword){
            $scope.searchMap.keyword = keyword;
        }
        //执行搜索
        $scope.search();
    }

    //搜索品牌
    $scope.keywordsLoadBrand=function(){
        if($scope.resultMap.brandList != null){
            for(var i=0;i<$scope.resultMap.brandList.length;i++){
                //获取品牌名
               var brandName = $scope.resultMap.brandList[i].text;
               var index = $scope.searchMap.keyword.indexOf(brandName);
               if(index>=0){
                   //将品牌加入到brand
                   $scope.searchMap.brand=brandName;
                   return true;
               }
            }
        }
            return false;
    }

    //排序搜索
    $scope.sortSearch=function(sort,sortField){
        $scope.searchMap.sort=sort;
        $scope.searchMap.sortField=sortField;

        //搜索
        $scope.search();

    }

    //搜索条件移除
    $scope.removeItemSearch=function(key){
        if(key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = '';
        }else{
            delete $scope.searchMap.spec[key];
        }
        //搜索
        $scope.search();
    }

    //点击分类时候，将选中分类记录  点击品牌时候，将选中分类记录  点击规格时候，将选中分类记录
    $scope.addIteamSearch=function(key,value){

        if(key == 'category' || key == 'brand' || key == 'price'){
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

           //调用品牌搜索
           // $scope.keywordsLoadBrand();

           //分页
            $scope.pageHandler(response.total, $scope.searchMap.pageNum);
        });
    }

    //分页搜索，pageNum需要跳转的页码
    $scope.pageSearch=function(pageNum){
        if(!isNaN(pageNum)){
            $scope.searchMap.pageNum=parseInt(pageNum);
        }
        if(isNaN(pageNum)){
            $scope.searchMap.pageNum=1;
        }
        if($scope.searchMap.pageNum>$scope.page.totalPage){
            $scope.searchMap.pageNum = $scope.page.totalPage
        }
        $scope.search();
    }

    //分页
    $scope.page={
        size:10,  //每页显示条数
        total:0,  //共多少条记录
        pageNum:1,  //当前页
        offset:1,  //偏移量
        lpage:1,  //起始页
        rpage:1,  //结束页
        totalPage:1,  //总页数
        pages:[],   //页码
        nextPage:1,  //下一页
        prePage:1,  //上一页
        hasPre:0,  //是否有上一页
        hasNext:0  //是否有下一页
    }

    //分页计算
    $scope.pageHandler=function(total,pageNum){

        //将pageNum给page.pageNum
        $scope.page.pageNum = pageNum;

        //总页数
        var totalPage = total%$scope.page.size==0 ? total/$scope.page.size : parseInt(( total/$scope.page.size)+1);

        $scope.page.totalPage=totalPage;

        //偏移量
        var offset = $scope.page.offset;

        //起始页，结束页
        var lpage = $scope.page.lpage;
        var rpage = $scope.page.rpage;

        //判断
        if(pageNum-offset>0){
            lpage = pageNum - offset;
            rpage = pageNum + offset;
        }

        if(pageNum-offset<=0){
            lpage = 1;
            rpage = pageNum + offset + Math.abs(pageNum-offset) +1;
        }

        if(rpage>totalPage){
            lpage = lpage - (rpage - totalPage);
            rpage = totalPage;
        }

        if(lpage<=0){
            lpage = 1;
        }

        //页码封装
        $scope.page.pages=[];
        for(var i=lpage;i<=rpage;i++){
            $scope.page.pages.push(i);
        }

        //上一页
        if((pageNum-1)>=1){
            $scope.page.prePage=(pageNum-1);
            $scope.page.hasPre=1;
        }else{
            $scope.page.hasPre=0;
        }

        //下一页
        if(pageNum<totalPage){
            $scope.page.nextPage=(pageNum+1);
            $scope.page.hasNext=1;
        }else{
            $scope.page.hasNext=0;
        }
    }
})