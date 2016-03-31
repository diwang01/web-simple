package com.letv.listener;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by wangdi5 on 2016/3/31.
 */
public class LogbackConfigListener implements ServletContextListener {


    private static final Logger logger = LoggerFactory.getLogger(LogbackConfigListener.class);


    private static final String CONFIG_LOCATION = "logbackConfigLocation";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        //从web.xml中加载指定文件名的日志配置文件
        String logbackConfigLocation = servletContextEvent.getServletContext()
                .getInitParameter(CONFIG_LOCATION);
        String fn = servletContextEvent.getServletContext().getRealPath(logbackConfigLocation);
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.reset();
            JoranConfigurator joranConfigurator = new JoranConfigurator();
            joranConfigurator.setContext(loggerContext);
            joranConfigurator.doConfigure(fn);
            logger.debug("loaded slf4j configure file from {}", fn);
        }
        catch (JoranException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
