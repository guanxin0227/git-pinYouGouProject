app.service('searchService',function ($http) {

    //实现搜索
    this.search=function (entity) {
        return $http.post('/item/search.shtml',entity);
    }
})