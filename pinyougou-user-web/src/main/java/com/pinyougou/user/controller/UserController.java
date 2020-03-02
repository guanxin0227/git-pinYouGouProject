package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.model.User;
import com.pinyougou.user.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
* @Description 用户注册
* @Author  guanx
* @Date   2020/3/1 18:40
* @Param
* @Return
* @Exception
*
*/
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
    * @Description 增加用户信息
    * @Author  guanx
    * @Date   2020/3/1 18:58
    * @Param
    * @Return
    * @Exception
    *
    */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public Result add(@RequestBody User user){
        try{

            //增加
            int mcount = userService.add(user);

            if(mcount>0){
                return new Result(true,"增加成功");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return new Result(false,"增加失败");
    }
}
