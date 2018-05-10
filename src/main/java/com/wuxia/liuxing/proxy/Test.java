package com.wuxia.liuxing.proxy;

import com.wuxia.liuxing.proxy.IPModel.IPMessage;
import com.wuxia.liuxing.proxy.IPModel.SerializeUtil;
import com.wuxia.liuxing.proxy.database.MyRedis;
import com.wuxia.liuxing.proxy.database.RedisDB;
import com.wuxia.liuxing.proxy.htmlparse.IPPool;
import com.wuxia.liuxing.proxy.htmlparse.IPThread;
import com.wuxia.liuxing.proxy.htmlparse.URLFecter;
import com.wuxia.liuxing.proxy.httpbrowser.MyHttpResponse;
import com.wuxia.liuxing.proxy.ipfilter.IPFilter;
import com.wuxia.liuxing.proxy.ipfilter.IPUtils;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class Test {


    public static void main(String[] args) {
//        //使用本机ip爬取xici代理网第一页
//        List<IPMessage> ipMessages = new ArrayList<IPMessage>();
//        ipMessages = URLFecter.urlParse(ipMessages);
//        //对得到的IP进行筛选，将IP速度在两秒以内的并且类型是https的留下，其余删除
//        ipMessages = IPFilter.Filter(ipMessages);
//        //对拿到的ip进行质量检测，将质量不合格的ip在List里进行删除
//        IPUtils.IPIsable(ipMessages);
//        //将爬取下来的ip信息写进Redis数据库中(List集合)
//        MyRedis redis = new MyRedis();
//        redis.setIPToList(ipMessages);
//        //从redis数据库中随机拿出一个IP
//        IPMessage ipMessage = redis.getIPByList();
//        out.println(ipMessage.getIPAddress());
//        out.println(ipMessage.getIPPort());

        MyRedis redis = new MyRedis();
//        Jedis jedis = RedisDB.getJedis();
//        List<String> lrange = jedis.lrange("wuxia:liuxing:" + "IPPool", 0, -1);
//        List<IPMessage> ipMessages = new ArrayList<IPMessage>();
//        for(String str: lrange) {
//            Object o = SerializeUtil.unserialize(str.getBytes());
//            if (o instanceof IPMessage) {
//                ipMessages.add((IPMessage) o);
//            } else {
//                out.println("不是IPMessage的一个实例~");
//            }
//        }
//        RedisDB.close(jedis);
//
//        //对得到的IP进行筛选，将IP速度在两秒以内的并且类型是https的留下，其余删除
//        ipMessages = IPFilter.Filter(ipMessages);
//        //对拿到的ip进行质量检测，将质量不合格的ip在List里进行删除
//        IPUtils.IPIsable(ipMessages);
//        //重置缓存
//        redis.deleteIPList();
//        redis.setIPToList(ipMessages);

        IPMessage ipMessage = redis.getIPByList();
        String html = MyHttpResponse.getHtml("http://tdliuxing.com/liuxing/27", ipMessage.getIPAddress(), ipMessage.getIPPort());
        System.out.println(html);
    }
}
