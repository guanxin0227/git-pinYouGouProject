/*****
 * 定义一个控制层 controller
 * 发送HTTP请求从后台获取数据
 ****/
app.controller("itemCatController",function($scope,$http,$controller,itemCatService){

    //继承父控制器
    $controller("baseController",{$scope:$scope});

    //定义3个变量，保存面包屑显示
    $scope.entity_1={"name":"顶级分类","id":0};
    $scope.entity_2=null;
    $scope.entity_3=null;

    //定义一个参数，记录当前属于第几级分类，用于控制商品分类级数
    $scope.grand=1;
    //定义一个方法，每调用grand一次，+1
    $scope.loadChild=function(itemCat){
        $scope.grand+=1;

        if($scope.grand == 2){
            //当分类等级为2级时候，把itemCat赋值给面包屑
            $scope.entity_2=itemCat;
        }else if($scope.grand == 3){
            //当分类等级为3级时候，把itemCat赋值给面包屑
            $scope.entity_3=itemCat;
        }
    }

    //获取所有的ItemCat信息
    $scope.getPage=function(page,size){
        //发送请求获取数据
        itemCatService.findAll(page,size,$scope.searchEntity).success(function(response){
            //集合数据
            $scope.list = response.list;
            //分页数据
            $scope.paginationConf.totalItems=response.total;
        });
    }

    //添加或者修改方法
    $scope.save = function(){
        var result = null;
        if($scope.entity.id!=null){
            //执行修改数据
            result = itemCatService.update($scope.entity);
        }else{
            //增加操作
            result = itemCatService.add($scope.entity);
        }
        //判断操作流程
        result.success(function(response){
            //判断执行状态
            if(response.success){
                //重新加载新的数据
                $scope.reloadList();
            }else{
                //打印错误消息
                alert(response.message);
            }
        });
    }

    //根据ID查询信息
    $scope.getById=function(id){
        itemCatService.findOne(id).success(function(response){
            //将后台的数据绑定到前台
            $scope.entity=response;
        });
    }

    //批量删除
    $scope.delete=function(){
        itemCatService.delete($scope.selectids).success(function(response){
            //判断删除状态
            if(response.success){
                $scope.reloadList();
            }else{
                alert(response.message);
            }
        });
    }

    //根据父id查询所有子类
    $scope.findByParentId=function (id) {
        itemCatService.findByParentId(id).success(function (response) {
            $scope.list = response;
        })
    }
});
