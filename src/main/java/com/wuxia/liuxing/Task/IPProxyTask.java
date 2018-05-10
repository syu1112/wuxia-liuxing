package com.wuxia.liuxing.Task;

import com.jfinal.plugin.cron4j.ITask;
import com.wuxia.liuxing.common.Constant;
import com.wuxia.liuxing.proxy.IPModel.IPMessage;
import com.wuxia.liuxing.proxy.database.MyRedis;
import com.wuxia.liuxing.proxy.htmlparse.IPPool;
import com.wuxia.liuxing.proxy.htmlparse.IPThread;
import com.wuxia.liuxing.proxy.htmlparse.URLFecter;
import com.wuxia.liuxing.proxy.ipfilter.IPFilter;
import com.wuxia.liuxing.proxy.ipfilter.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 代理IP
 */
public class IPProxyTask implements ITask {
    private static Logger logger = LoggerFactory.getLogger(IPProxyTask.class);

    MyRedis redis = new MyRedis();

    public void stop() {
        logger.debug("ipproxy task stop...");
    }

    public void run() {
        logger.debug("ipproxy task run...");
        //清除代理Ip库
        redis.deleteIPList();
        //使用本机ip爬取xici代理网第一页
        List<IPMessage> ipMessages = new ArrayList<IPMessage>();
        ipMessages = URLFecter.urlParse(ipMessages);
        //对得到的IP进行筛选，将IP速度在两秒以内的并且类型是https的留下，其余删除
        ipMessages = IPFilter.Filter(ipMessages);
        //对拿到的ip进行质量检测，将质量不合格的ip在List里进行删除
        IPUtils.IPIsable(ipMessages);

        List<Thread> threads = new ArrayList<Thread>();
        //构造种子url(4000条ip)
        List<String> urls = new ArrayList<String>();
        for (int i = 2; i <= 41; i++) {
            urls.add("http://www.xicidaili.com/nn/" + i);
        }

        /**
         * 对urls进行解析并进行过滤,拿到所有目标IP(使用多线程)
         *
         * 基本思路是给每个线程分配自己的任务，在这个过程中List<IPMessage> ipMessages
         * 应该是共享变量，每个线程更新其中数据的时候应该注意线程安全
         */
        IPPool ipPool = new IPPool(ipMessages);
        for (int i = 0; i < 20; i++) {
            //给每个线程进行任务的分配
            Thread thread = new IPThread(urls.subList(i*2, i*2+2), ipPool);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(IPMessage ipMessage : ipMessages){
            System.out.println(ipMessage.getIPAddress());
            System.out.println(ipMessage.getIPPort());
            System.out.println(ipMessage.getIPType());
            System.out.println(ipMessage.getIPSpeed());
        }
        //将爬取下来的ip信息写进Redis数据库中(List集合)
        redis.setIPToList(ipMessages);
        logger.debug("ipproxy task end...");
    }
}
