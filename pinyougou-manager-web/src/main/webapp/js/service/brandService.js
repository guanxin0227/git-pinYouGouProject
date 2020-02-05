//创建一个Service
app.service("brandService",function ($http) {

    //列表查询
    this.findAll = function (page,size,searchEntity) {
        return $http.post('/brand/list.shtml?page='+ page +'&size='+ size,searchEntity);
    }

    //增加
    this.add = function (entity) {
        return $http.post('/brand/add.shtml',entity);
    }
    //修改
    this.update = function (entity) {
        return $http.post('/brand/modify.shtml',entity);
    }

    //根据id查询
    this.findOne = function (id) {
        return $http.get('/brand/'+id+'.shtml');
    }

    //删除
    this.delete = function (ids) {
        return $http.get('/brand/delete.shtml',ids);
    }
});