package com.pinyougou.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *  @author: guanx
 *  @Date: 2020/3/17 21:39
 *  @Description: 定时任务调度
 */
@Component
public class SeckillGoodsTask {

    @Scheduled(cron = "0/15 * * * * ?")
    public void pushSeckillGoods2Redis(){
        System.out.println("]]]]]]");
    }
}
