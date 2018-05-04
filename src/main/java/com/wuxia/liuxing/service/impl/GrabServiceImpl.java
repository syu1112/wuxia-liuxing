package com.wuxia.liuxing.service.impl;

import com.wuxia.liuxing.common.Constant;
import com.wuxia.liuxing.common.MD5Codec;
import com.wuxia.liuxing.kit.HttpKit;
import com.wuxia.liuxing.kit.MailKit;
import com.wuxia.liuxing.kit.XmlKit;
import com.wuxia.liuxing.service.GrabService;
import org.apache.commons.mail.EmailException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrabServiceImpl implements GrabService {
    private static Logger logger = LoggerFactory.getLogger(GrabServiceImpl.class);
    private static String url_tdliuxing = Constant.config.get("url.tdliuxing");
    /**
     * 抓取tdliuxing.com的流行列表数据,并将抓取结果发送至指定邮箱
     * @return
     */
    public void tdliuxing() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String yyyyMMdd = sdf.format(new Date());
            Map<String, Object> services = getServices();
            Map<String, Object> emails = getEmails();
            for(String key: services.keySet()) {
                String serviceId = key;
                String serviceName = services.get(key).toString();
                //解析网页
                List<Map<String, Object>> dataList = match(HttpKit.sendGet(url_tdliuxing + serviceId), yyyyMMdd);
                if (dataList.size()==0) {
                    logger.debug("grab tdliuxing,not matcher,serviceName:{}", serviceName);
                    continue;
                }
                //发送邮件
                sendEmail(emails, dataList);
                logger.info("grab tdliuxing,sended emails:{},serviceName:{}", emails.size(), serviceName);
                //休息2s
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            logger.error("grab tdliuxing,exception:{}", e);
            e.printStackTrace();
        }
    }

    /**
     * Tools
     * @return
     */
    private static List<Map<String, Object>> match(String result, String yyyyMMdd) {
        //正则匹配结果
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        String regex = "<div class=\"col-md-8 stats-info stats-last widget-shadow\">(.*?)</div>";
        Matcher matcher = Pattern.compile(regex).matcher(result);
        if (matcher.find()) {
            String tableResult = matcher.group(1);
            String tbodyReg = "<tr.*?style=\"color:red\">(.*?)</tr>";
            Matcher m = Pattern.compile(tbodyReg).matcher(tableResult);
            while (m.find()) {
                String trResult = m.group(1);
                //判断此条信息今天是否已通知过
                String encode = MD5Codec.encode(yyyyMMdd + trResult);
                if(Constant.sendedSet.contains(encode)) {
                    continue;
                }
                Constant.sendedSet.add(encode);
                //解析tr标签
                String tdReg = "<td>.*?<b>(.*?)</b>.*?</td>";
                Matcher mt = Pattern.compile(tdReg).matcher(trResult);
                Map<String, Object> dataMap = new HashMap<String, Object>();
                int index = 0;
                while (mt.find()) {
                    String tdResult = mt.group(1);
                    if(index==0) {
                        dataMap.put("product", tdResult);
                    }else if(index==1) {
                        dataMap.put("time", tdResult);
                    }else if(index==2) {
                        dataMap.put("desc", tdResult);
                    }
                    index++;
                }
                dataList.add(dataMap);
            }
        }
        return dataList;
    }

    private static void sendEmail(Map<String, Object> emails, List<Map<String, Object>> dataList) {
        MailKit mail = new MailKit();
        String content = "";
        content += "<div>";
        content += "    <style>";
        content += "        table,table tr th, table tr td { border:1px solid black; }";
        content += "        table { border-collapse: collapse;}";
        content += "    </style>";
        content += "    <table class=\"table\">";
        content += "        <thead>";
        content += "            <tr>";
        content += "                <th width=\"20%\">货物</th>";
        content += "                <th width=\"35%\">预估时间</th>";
        content += "                <th>航程时间</th>";
        content += "            </tr>";
        content += "        </thead>";
        content += "        <tbody>";
        for (Map<String, Object> dataMap: dataList) {
            content += "    <tr style=\"color:red\">";
            content += "        <td>"+dataMap.get("product")+"</td>";
            content += "        <td>"+dataMap.get("time")+"</td>";
            content += "        <td>"+dataMap.get("desc")+"</td>";
            content += "    </tr>";
        }
        content += "        </tbody>";
        content += "    </table>";
        content += "    <div style=\"margin-top: 10px;\">";
        content += "        <span>ps：流行时间是预估值仅供参考,游戏人数较少时误差大。</span>";
        content += "        <span style=\"color:red; display: none;\">以上内容来自http://tdliuxing.com</span>";
        content += "    </div>";
        content += "</div>";
        //发送邮件
        for(String email: emails.keySet()) {
            mail.mailto(email);
        }
        try {
            mail.sendMail("【天刀】流行货物提醒", content);
        } catch (EmailException e) {
            e.printStackTrace();
        }

    }

    private static Map<String, Object> getEmails() {
        Element root = XmlKit.use("email.xml", XmlKit.DEFIALT_ENCODING);

        Map<String, Object> emails = new HashMap<String, Object>();
        Iterator<Element> emailDoms = root.elementIterator();
        while(emailDoms.hasNext()){
            Element emailDom = emailDoms.next();
            emails.put(emailDom.attributeValue("value"), emailDom.getText());
        }
        return emails;
    }

    private static Map<String, Object> getServices() {
        Element root = XmlKit.use("service.xml", XmlKit.DEFIALT_ENCODING);

        Map<String, Object> services = new HashMap<String, Object>();
        Iterator<Element> serviceDoms = root.elementIterator();
        while(serviceDoms.hasNext()){
            Element serviceDom = serviceDoms.next();
            services.put(serviceDom.attributeValue("value"), serviceDom.getText());
        }
        return services;
    }

}
