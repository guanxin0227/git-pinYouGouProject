package com.pinyougou.manager.controller;

import com.pinyougou.fastdfs.UploadUtil;
import com.pinyougou.http.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *  @author: guanx
 *  @Date: 2020/2/8 22:13
 *  @Description: 文件上传功能
 */

@RestController
public class UploadController {

    @Value("${TRACKER_PATH}")
    private String TRACKER_PATH;

    @Value("${IMAGE_DOMAIN}")
    private String IMAGE_DOMAIN;

    /**
    * @Description
    * @Author  guanx
    * @Date   2020/2/8 22:14
    * @Param
    * @Return
    * @Exception  文件上传
    *
    */

    @RequestMapping(value = "/upload")
    public Result upload(MultipartFile file) throws Exception{   //file前台传的文件必须也为file

        //获取文件名字
        String originalFilename = file.getOriginalFilename();

        //截取的方式获取后缀
        //String suffix = originalFilename.substring(originalFilename.lastIndexOf("."),originalFilename.length());

        //获取文件扩展名
        String suffix = StringUtils.getFilenameExtension(originalFilename);

        //文件的字节数组
        byte[] bytes = file.getBytes();

        //FastDFS  String conffilename,byte[] buffer,String suffix
        String[] uploads = UploadUtil.upload(TRACKER_PATH, bytes, suffix);

        //拼接的图片访问地址
        String url = IMAGE_DOMAIN + uploads[0] + "/" + uploads[1];
        return new Result(true,url);

//        List<String> list = new ArrayList<>();
//        //获取文件名字
//        String originalFilename = file.getOriginalFilename();
//        //获取文件扩展名
//        String suffix = StringUtils.getFilenameExtension(originalFilename);
//        //文件保存的本地路径
//        String localhostUrl = PathConfig.localhost_url;
//
//        String fileUrl = localhostUrl + originalFilename;
//
//        File urlFile = new File(fileUrl);
//        try {
//            file.transferTo(urlFile);
//            list.add(fileUrl);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return new Result(true,fileUrl);
    }


}
