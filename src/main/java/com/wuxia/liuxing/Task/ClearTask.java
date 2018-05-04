package com.wuxia.liuxing.Task;

import com.jfinal.plugin.cron4j.ITask;
import com.wuxia.liuxing.common.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

/**
 * 清除累积内存
 */
public class ClearTask implements ITask {
    private static Logger logger = LoggerFactory.getLogger(ClearTask.class);
    public void stop() {
        logger.debug("clear task stop...");
    }

    public void run() {
        logger.debug("clear task run...");
        Constant.sendedSet = new HashSet<String>();
        logger.debug("clear task end...");
    }
}
