package com.wuxia.liuxing.proxy.database;

import com.wuxia.liuxing.proxy.IPModel.IPMessage;
import com.wuxia.liuxing.proxy.IPModel.SerializeUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

import static java.lang.System.out;

public class MyRedis {
    private static String PREFIX = "wuxia:liuxing:";

    //将ip信息保存在Redis列表中
    public void setIPToList(List<IPMessage> ipMessages) {
        Jedis jedis = RedisDB.getJedis();
        try {
            for (IPMessage ipMessage : ipMessages) {
                //首先将ipMessage进行序列化
                byte[] bytes = SerializeUtil.serialize(ipMessage);
                jedis.rpush((PREFIX+"IPPool").getBytes(), bytes);
            }
        } finally {
            RedisDB.close(jedis);
        }
    }

    //将Redis中保存的对象进行反序列化
    public IPMessage getIPByList() {
        Jedis jedis = RedisDB.getJedis();
        try {
            Long llen = jedis.llen(PREFIX + "IPPool");
            if(llen==0) {
                out.println("代理库中无可用ip");
                return null;
            }
            int rand = (int)(Math.random()*llen);

            Object o = SerializeUtil.unserialize(jedis.lindex((PREFIX+"IPPool").getBytes(), rand));
            if (o instanceof IPMessage) {
                return (IPMessage)o;
            } else {
                out.println("不是IPMessage的一个实例~");
                return null;
            }
        } finally {
            RedisDB.close(jedis);
        }
    }

    public void deleteIPList() {
        Jedis jedis = RedisDB.getJedis();
        jedis.del(PREFIX+"IPPool");
        RedisDB.close(jedis);
    }
}
