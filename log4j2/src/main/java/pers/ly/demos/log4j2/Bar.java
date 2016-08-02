package pers.ly.demos.log4j2;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
 
public class Bar {
  static final Logger logger = LogManager.getLogger(Bar.class.getName());
 
  public Bar() {
      logger.info("info msg");
      logger.debug("debug msg");
  }
  public boolean doIt() {
    logger.traceEntry();
    logger.error("Did it again!");
    return logger.traceExit(false);
  }
}