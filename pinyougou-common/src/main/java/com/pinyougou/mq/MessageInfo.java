package com.pinyougou.mq;

import java.io.Serializable;

/**
 *  @author: guanx
 *  @Date: 2020/2/29 17:00
 *  @Description: 封装MQ
 */
public class MessageInfo implements Serializable {

    private static final long serialVersionUID = -4206122703453814687L;

    public static final int METHOD_ADD = 1;
    public static final int METHOD_UPDATE = 2;
    public static final int METHOD_DELETE = 3;

    //执行的动作 增加，删除，修改
    private int method;

    //需要发送的操作数据
    private Object context;

    public MessageInfo(int method, Object context) {
        this.method = method;
        this.context = context;
    }

    public MessageInfo() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }
}
