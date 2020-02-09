//文件上传

app.service("uploadService",function ($http) {
    //上传文件
    this.uploadFile = function(){

        //H5支持的表单打包,FormData对象可以组装一组用 XMLHttpRequest发送请求的键/值对。它可以更灵活方便的发送表单数据
        var formData=new FormData();

        //往formData表单中添加数据
        formData.append("file",file.files[0]);

        //提交请求
        return $http({
            method:'POST',
            url:"/upload.shtml",
            data: formData,
            headers: {'Content-Type':undefined},
            transformRequest: angular.identity
        });
    }
})