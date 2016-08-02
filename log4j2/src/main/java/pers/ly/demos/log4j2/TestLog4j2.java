package pers.ly.demos.log4j2;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class TestLog4j2 {
    static {
        String path = TestLog4j2.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File file = new File(path + "my-log4j2.properties");
        LoggerContext context = (LoggerContext) LogManager.getContext(null,false,null,file.toURI());
        
        // this will force a reconfiguration
        //context.setConfigLocation(file.toURI());
        
        logger  = LogManager.getLogger(TestLog4j2.class);
    }
    
    private static final Logger logger;
    
    public static void main(String[] args) throws InterruptedException {
        logger.trace("Entering application.");
        Bar bar = new Bar();
        if (!bar.doIt()) {
            logger.error("Didn't do it.");
        }
        logger.trace("Exiting application.");
        
        Logger fileLogger = LogManager.getLogger("file");
        fileLogger.info("haha");
        
        Thread.sleep(10000);
        
        fileLogger = LogManager.getLogger("file");
        fileLogger.info("haha");
    }
}
