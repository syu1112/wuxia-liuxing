package com.wuxia.liuxing.kit;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author Allen
 *
 */
public class XmlKit {

    public static final String DEFIALT_ENCODING = "UTF-8";
    
    public static Element use(String filePath) {
        try {
            //创建SAXReader对象  
            SAXReader reader = new SAXReader();
            //读取文件 转换成Document  
            Document document = reader.read(new File(filePath));
            //获取根节点
            return document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Element use(String fileName, String encoding) {
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            InputStreamReader isr = new InputStreamReader(in, encoding);
            //创建SAXReader对象  
            SAXReader reader = new SAXReader();
            //读取文件 转换成Document  
            Document document = reader.read(isr);
            //获取根节点
            return document.getRootElement();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
