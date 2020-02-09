/*****
 * 定义一个控制层 controller
 * 发送HTTP请求从后台获取数据
 ****/
app.controller("goodsController",function($scope,$http,$controller,goodsService,uploadService,itemCatService,typeTemplateService){

    //继承父控制器
    $controller("baseController",{$scope:$scope});

    //定义一个数据用于储存图片上传信息
    //$scope.image_entity={};

    //定义一个数据，用于存储所有上传图片
    //$scope.imageList=[];
    $scope.entity={goodsDesc:{itemImages:[]}};

    //移除集合中照片
    $scope.remove_image_entity=function(index){
        //$scope.imageList.splice(index,1);
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    //往集合中添加图片
    $scope.add_image_entity=function(){
        //$scope.imageList.push($scope.image_entity);
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //获取所有的Goods信息
    $scope.getPage=function(page,size){
        //发送请求获取数据
        goodsService.findAll(page,size,$scope.searchEntity).success(function(response){
            //集合数据
            $scope.list = response.list;
            //分页数据
            $scope.paginationConf.totalItems=response.total;
        });
    }

    //添加或者修改方法
    $scope.save = function(){

        //文本编辑器对象.html()  表示获取文本编辑器内容
        $scope.entity.goodsDesc.introduction = editor.html();

        var result = null;
        if($scope.entity.id!=null){
            //执行修改数据
            result = goodsService.update($scope.entity);
        }else{
            //增加操作
            result = goodsService.add($scope.entity);
        }
        //判断操作流程
        result.success(function(response){
            //判断执行状态
            if(response.success){
                //重新加载新的数据
                //$scope.reloadList();
                $scope.entity={};
                editor.html("");
            }else{
                //打印错误消息
                alert(response.message);
            }
        });
    }

    //根据ID查询信息
    $scope.getById=function(id){
        goodsService.findOne(id).success(function(response){
            //将后台的数据绑定到前台
            $scope.entity=response;
        });
    }

    //批量删除
    $scope.delete=function(){
        goodsService.delete($scope.selectids).success(function(response){
            //判断删除状态
            if(response.success){
                $scope.reloadList();
            }else{
                alert(response.message);
            }
        });
    }

    //文件上传
    $scope.uploadFile=function () {
        uploadService.uploadFile().success(function (response) {
            if(response.success){
                //获取文件上传后回显的url
                $scope.image_entity.url=response.message;
            }
        })
    }

    //查询商品1级分类
    $scope.findItemCat1List=function (id) {
        itemCatService.findByParentId(id).success(function (response) {
            $scope.itemCat1List=response;
        })
        //清空2级，3级目录
        $scope.itemCat2List=null;
        $scope.itemCat3List=null;
        //清空模板id
        $scope.entity.typeTemplateId=null;
    }

    //查询商品2级分类
    $scope.findItemCat2List=function (id) {
        itemCatService.findByParentId(id).success(function (response) {
            $scope.itemCat2List=response;
        })
        //3级目录
        $scope.itemCat3List=null;
        //清空模板id
        $scope.entity.typeTemplateId=null;
    }

    //查询商品3级分类
    $scope.findItemCat3List=function (id) {
        itemCatService.findByParentId(id).success(function (response) {
            $scope.itemCat3List=response;
        })
    }

    //查询第3级分类发生变化
    $scope.getTypeId=function (id) {
        itemCatService.findOne(id).success(function (response) {
            $scope.entity.typeTemplateId = response.typeId;
        })
    }

    //监控entity.typeTemplateId的变化
    $scope.$watch('entity.typeTemplateId',function (newValue,oldValue) {
        if(!isNaN(newValue)){
            typeTemplateService.findOne(newValue).success(function (response) {
                //获取品牌列表
                $scope.brandList=angular.fromJson(response.brandIds);

                //扩展属性
                $scope.entity.goodsDesc.customAttributeItems=angular.fromJson(response.customAttributeItems);

                //调用后台实现规格信息填充
                //$scope.specList=angular.fromJson(response.specIds);
                typeTemplateService.getOptionsByTypeId($scope.entity.typeTemplateId).success(function (response) {
                    $scope.specList=response;
                });
            });

        }
    });
});
