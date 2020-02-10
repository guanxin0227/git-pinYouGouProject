/*****
 * 定义一个控制层 controller
 * 发送HTTP请求从后台获取数据
 ****/
app.controller("goodsController",function($location,$scope,$http,$controller,goodsService,uploadService,itemCatService,typeTemplateService){

    //继承父控制器
    $controller("baseController",{$scope:$scope});

    //定义一个数据用于储存图片上传信息
    //$scope.image_entity={};

    //定义一个数据，用于存储所有上传图片,规格结果集
    //$scope.imageList=[];
    $scope.entity={goodsDesc:{itemImages:[],specificationItems:[]}};

    //定义商家状态集合
    $scope.status=["未审核","审核通过","审核不通过","关闭"];

    //定义商品分类集合
    $scope.itemCatShowList={};
    //查询商品分类集合
    $scope.getItemCatShowList=function(){
        itemCatService.findAllList().success(function (response) {
            //迭代数据
            for(var i=0; i<response.length; i++){
                var key = response[i].id;
                var value = response[i].name;
                $scope.itemCatShowList[key]=value;
            }
        })
    }

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
            if(response.success) {
                //重新加载新的数据
                // $scope.reloadList();
                // $scope.entity={};
                // editor.html("");

                //跳转到列表页面
                location.href = '/admin/goods.html';

            }else{
                //打印错误消息
                alert(response.message);
            }
        });
    }

    //获取点击修改时候，url参数
    $scope.getUrlParam=function(){
        //获取地址栏id
        var id = $location.search()['id'];

        //调用查询
        if(id != null){
            $scope.getById(id);
        }
    }

    //列表点修改时候,扩展属性显示问题
    var modifyTypeTemplateId = 0;
    var modifyCustomAttributeItems = {};

    //根据ID查询信息
    $scope.getById=function(id){
        goodsService.findOne(id).success(function(response){

            //将后台的数据绑定到前台
            $scope.entity=response;

            //列表点修改时候，数据被清空，修改页面需进行以下操作

            //记录模板id
            modifyTypeTemplateId = $scope.entity.typeTemplateId;

            //记录规格选项
            modifyCustomAttributeItems = angular.copy($scope.entity.goodsDesc.customAttributeItems);

            //重复查询,
            $scope.findItemCat1List(0);
            $scope.findItemCat2List($scope.entity.category1Id);
            $scope.findItemCat3List($scope.entity.category2Id);

            //重复查询后
            $scope.entity.typeTemplateId=modifyTypeTemplateId;

            //文本编辑器赋值
            editor.html($scope.entity.goodsDesc.introduction);

            //图片信息转JSON
            $scope.entity.goodsDesc.itemImages=angular.fromJson($scope.entity.goodsDesc.itemImages);

            //扩展属性转JSON
            $scope.entity.goodsDesc.customAttributeItems=angular.fromJson($scope.entity.goodsDesc.customAttributeItems);

            //规格属性转JSON
            $scope.entity.goodsDesc.specificationItems=angular.fromJson($scope.entity.goodsDesc.specificationItems);
            //将Item的spec转成json
            for (var i=0;i<$scope.entity.items.length;i++){
                $scope.entity.items[i].spec=angular.fromJson($scope.entity.items[i].spec);
            }
        });
    }

    //列表点修改时候,规格显示问题
    $scope.checkedAttribute=function(atrributeName,attributeValue){

        //判断atrributeName在$scope.entity.goodsDesc.specificationItems中是否存在
        var result = searchObjectByKey($scope.entity.goodsDesc.specificationItems,atrributeName);

        //存在判断规格选项attributeValue是否存在
        if(result != null){
            var index = result.attributeValue.indexOf(attributeValue);
            if(index>=0){
                //存在，返回true
                return true;
            }
        }
        return false;
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

                //列表点修改时候,扩展属性显示问题
                if($location.search()['id'] != null && newValue == modifyTypeTemplateId){

                    $scope.entity.goodsDesc.customAttributeItems = angular.fromJson(modifyCustomAttributeItems);

                }else{

                    //扩展属性
                    $scope.entity.goodsDesc.customAttributeItems=angular.fromJson(response.customAttributeItems);
                }

                //调用后台实现规格信息填充
                //$scope.specList=angular.fromJson(response.specIds);
                typeTemplateService.getOptionsByTypeId($scope.entity.typeTemplateId).success(function (response) {
                    $scope.specList=response;
                });
            });

        }
    });

    //存储选中的规格数据
    $scope.updateSpecAttribute=function ($event,atrributeName,attributeValue) {
        //判断当前规格名字，在集合中是否存在  比如说传入参数(atrributeName)为:屏幕尺寸
        var result = searchObjectByKey($scope.entity.goodsDesc.specificationItems,atrributeName);

        // 如果存在，则直接将该对象返回过来result
        if(result!=null){
            //如果是勾选事件
            if($event.target.checked){
                //将勾选中的规格选项(attributeValue:例如3G)加入到result的attributeValue属性中
                result.attributeValue.push(attributeValue);
            }else{
                //将勾选的选项移除
                var valueIndex = result.attributeValue.indexOf(attributeValue);
                result.attributeValue.splice(valueIndex,1);

                if(result.attributeValue.length<=0){
                    //如果选项个数为0，移除该规格
                    var nameIndex = $scope.entity.goodsDesc.specificationItems.indexOf(result);
                    $scope.entity.goodsDesc.specificationItems.splice(nameIndex,1);
                }

            }

        }else{
            //如果不存在,构建一条数据加入集合
            var newSpec = {"attributeName":atrributeName,"attributeValue":[attributeValue]};
            $scope.entity.goodsDesc.specificationItems.push(newSpec);
        }
    };
    //接上，调用此方法 list=specificationItems:[]
    searchObjectByKey=function (list,attributeName) {
        for (var i=0;i<list.length;i++){
            if(list[i]['attributeName']==attributeName){
                return list[i];
            }
        }
    };

    //上次组合的商品集合数据传入，当前选中的规格名字和规格选项集合传入
    addCloumn=function (itemlist,attributeValue,attributeName) {
        var items=[];
        for(var i=0;i<itemlist.length;i++){
            //循环跟上一次重组的商品结果集再次重组
            for(var j=0;j<attributeValue.length;j++){
                //深克隆
                var newItem =angular.copy( itemlist[i] );
                //      spec:{"机身内存":"128G"}
                newItem.spec[attributeName]=attributeValue[j];

                //将重组的集合数据存入到items1
                items.push(newItem);
            }
        }
        return items;
    }

    //SKU重组
    $scope.createItems=function () {
        //没有选中任何规格的时候，有一个默认商品
        var item={"price":0,"num":0,"status":1,"isDefault":"0",spec:{}};
        $scope.entity.items=[item];

        //遍历所有规格，上次组合的商品集合数据传入，当前选中的规格名字和规格选项集合传入
        for(var i=0;i<$scope.entity.goodsDesc.specificationItems.length;i++){
            var attributeValue = $scope.entity.goodsDesc.specificationItems[i].attributeValue;
            var attributeName = $scope.entity.goodsDesc.specificationItems[i].attributeName;
            //(itemlist,attributeValue,attributeName)
            $scope.entity.items = addCloumn($scope.entity.items,attributeValue,attributeName);
        }
    }
});
