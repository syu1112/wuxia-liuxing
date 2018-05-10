package com.wuxia.liuxing.service.impl;

import com.wuxia.liuxing.common.Constant;
import com.wuxia.liuxing.common.MD5Codec;
import com.wuxia.liuxing.kit.HttpKit;
import com.wuxia.liuxing.kit.MailKit;
import com.wuxia.liuxing.kit.XmlKit;
import com.wuxia.liuxing.proxy.IPModel.IPMessage;
import com.wuxia.liuxing.proxy.database.MyRedis;
import com.wuxia.liuxing.proxy.htmlparse.URLFecter;
import com.wuxia.liuxing.proxy.httpbrowser.MyHttpResponse;
import com.wuxia.liuxing.proxy.ipfilter.IPFilter;
import com.wuxia.liuxing.proxy.ipfilter.IPUtils;
import com.wuxia.liuxing.service.GrabService;
import org.apache.commons.mail.EmailException;
import org.dom4j.Element;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrabServiceImpl implements GrabService {
    private static Logger logger = LoggerFactory.getLogger(GrabServiceImpl.class);
    private static String url_tdliuxing = Constant.config.get("url.tdliuxing");
    private static IPMessage ipMessage = null;
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

                String html = "";
                if(ipMessage==null) {
                    //使用本机ip爬取xici代理网第一页
                    List<IPMessage> ipMessages = new ArrayList<IPMessage>();
                    ipMessages = URLFecter.urlParse(ipMessages);
                    //对得到的IP进行筛选，将IP速度在两秒以内的并且类型是https的留下，其余删除
                    ipMessages = IPFilter.Filter(ipMessages);
                    for(IPMessage data : ipMessages) {
                        html = MyHttpResponse.getHtml(url_tdliuxing + serviceId, data.getIPAddress(), data.getIPPort());
                        if(null==html || "".equals(html) || html.contains("Unauthorized ...")) {
                            continue;
                        }else {
                            ipMessage = data;
                            System.out.println("ipMessage:"+ipMessage.toString());
                            break;
                        }
                    }
                }else {
                    html = MyHttpResponse.getHtml(url_tdliuxing + serviceId, ipMessage.getIPAddress(), ipMessage.getIPPort());
                }
                if(null==html || "".equals(html) || html.contains("Unauthorized ...")) {
                    System.err.println("代理IP不可用："+ipMessage!=null?ipMessage.toString():"localhost");
                    ipMessage = null;
                    continue;
                }
                //解析网页
                List<Map<String, Object>> dataList = match(html, yyyyMMdd);
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
        Document document = Jsoup.parse(result);
        Elements trs = document.select("div[class=col-md-8 stats-info stats-last widget-shadow]").get(0).select("table").select("tbody").select("tr[style=color:red]");
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        for(org.jsoup.nodes.Element element: trs) {
            Elements td = element.select("td");
            //判断此条信息今天是否已通知过
            String trStr = td.get(0).text() + td.get(1).text() + td.get(2).text();
            String encode = MD5Codec.encode(yyyyMMdd + trStr);
            if(Constant.sendedSet.contains(encode)) {
                continue;
            }
            Constant.sendedSet.add(encode);
            //存入
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("product", td.get(0).text());
            dataMap.put("time", td.get(1).text());
            dataMap.put("desc", td.get(2).text());
            dataList.add(dataMap);
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
