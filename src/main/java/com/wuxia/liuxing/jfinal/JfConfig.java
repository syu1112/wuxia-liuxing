package com.wuxia.liuxing.jfinal;

import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.template.Engine;
import com.wuxia.liuxing.Task.ClearTask;
import com.wuxia.liuxing.Task.GrabTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JfConfig extends JFinalConfig {
    private static Logger logger = LoggerFactory.getLogger(JfConfig.class);
    private static final Prop prop = PropKit.use("jfinal.properties");

    public static void main(String[] args) {
        /**
         * 特别注意：IDEA 之下建议的启动方式，仅比 eclipse 之下少了最后一个参数
         */
        JFinal.start("src/main/webapp", 80, "/");
    }

    public void configConstant(Constants constants) {
        constants.setDevMode(prop.getBoolean("config.devMode", false));
    }

    public void configRoute(Routes routes) {

    }

    public void configEngine(Engine engine) {

    }

    public void configPlugin(Plugins plugins) {
        Cron4jPlugin cp = new Cron4jPlugin();

        //周一至周六，18-23点提醒
        cp.addTask("*/3 18-23 * * mon-sat", new GrabTask());
        //周日全天提醒
        cp.addTask("*/3 * * * sun", new GrabTask());
        //每日清除一次内存
        cp.addTask("0 0 * * *", new ClearTask());
        plugins.add(cp);
        logger.debug("jfinal plugin, add task:{}", "grabTask");
    }

    public void configInterceptor(Interceptors interceptors) {

    }

    public void configHandler(Handlers handlers) {

    }

    // 系统启动完成后回调
    public void afterJFinalStart() {
        System.out.println("############################################");
        System.out.println("###########【TD】流行货物提醒系统###########");
        System.out.println("############################################");
    }

    // 系统关闭之前回调
    public void beforeJFinalStop() {

    }
}
