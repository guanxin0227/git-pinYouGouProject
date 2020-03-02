package com.pinyougou.user.service;

import com.pinyougou.model.User;

/**
* @Description 用户注册
* @Author  guanx
* @Date   2020/3/1 18:38
* @Param
* @Return
* @Exception
*
*/
public interface UserService {

    /**
    * @Description 增加用户信息
    * @Author  guanx
    * @Date   2020/3/1 18:53
    * @Param
    * @Return
    * @Exception
    *
    */
    int add(User user);

    /**
    * @Description 创建验证码
    * @Author  guanx
    * @Date   2020/3/2 11:29
    * @Param
    * @Return
    * @Exception
    *
    */
    void createCode(String phone);

    /**
    * @Description 判断验证码是否有效
    * @Author  guanx
    * @Date   2020/3/2 14:03
    * @Param
    * @Return
    * @Exception
    *
    */
    boolean checkCode(String phone, String code);

    /**
    * @Description 判断用户名是否存在
    * @Author  guanx
    * @Date   2020/3/2 14:08
    * @Param
    * @Return
    * @Exception
    *
    */
    int getUserByUserName(String username);

    /**
    * @Description 判断手机号是否存在
    * @Author  guanx
    * @Date   2020/3/2 14:12
    * @Param
    * @Return
    * @Exception
    *
    */
    int getPhoneCount(String phone);
}
