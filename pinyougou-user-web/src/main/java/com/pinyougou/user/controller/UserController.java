package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.model.User;
import com.pinyougou.user.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public Result add(@RequestBody User user,String code){
        try{

            //判断验证码是否有效
            boolean bo = userService.checkCode(user.getPhone(),code);

            if(!bo){
                return new Result(false,"验证码无效");
            }

            //判断用户名是否存在
            int mcount = userService.getUserByUserName(user.getUsername());

            if(mcount>0){
                return new Result(false,"用户名已存在");
            }

            //判断手机号是否存在
            int phoneCount = userService.getPhoneCount(user.getPhone());

            if(phoneCount>0){
                return new Result(false,"手机号已被注册");
            }

            //增加
            int count = userService.add(user);

            if(count>0){
                return new Result(true,"增加成功");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return new Result(false,"增加失败");
    }

    /**
    * @Description 发送消息获取验证码
    * @Author  guanx
    * @Date   2020/3/2 11:23
    * @Param
    * @Return
    * @Exception
    *
    */
    @RequestMapping(value = "/create/code")
    public Result createCode(String phone){
        try {

            //创建验证码
            userService.createCode(phone);

            return new Result(true,"发送验证码成功");

        }catch (Exception e){
            e.printStackTrace();
        }

        return new Result(false,"发送验证码失败");
    }

    /**
    * @Description 获取当前用户名
    * @Author  guanx
    * @Date   2020/3/2 21:12
    * @Param
    * @Return
    * @Exception
    *
    */
    @RequestMapping(value = "/getUserName")
    public String getUserName(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
