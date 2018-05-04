package com.wuxia.liuxing.Task;

import com.jfinal.plugin.cron4j.ITask;
import com.wuxia.liuxing.service.GrabService;
import com.wuxia.liuxing.service.impl.GrabServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrabTask implements ITask {
    private static Logger logger = LoggerFactory.getLogger(GrabTask.class);

    public void stop() {
        logger.debug("grab task stop...");
    }

    public void run() {
        logger.debug("grab task run...");
//        GrabService service = new GrabServiceImpl();
//        service.tdliuxing();
    }
}
