package pers.ly.demos.log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.Component;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.builder.impl.DefaultConfigurationBuilder;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.status.StatusLogger;

public class MyConfiguration extends BuiltConfiguration implements Reconfigurable {
    public MyConfiguration(ConfigurationSource source, Component rootComponent) {
        super(source, rootComponent);
        // TODO Auto-generated constructor stub
    }

    protected static final org.apache.logging.log4j.Logger LOGGER = StatusLogger.getLogger();
    
    private static final String name = "MyConfiguration";
    
    private static final ConfigurationBuilder<MyConfiguration> builder;
    
    static {
        builder = new DefaultConfigurationBuilder<>(MyConfiguration.class);
        builder.setConfigurationName(name);
        builder.setMonitorInterval("5");
        builder.setStatusLevel(Level.ERROR);
        
        builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).
            addAttribute("level", Level.DEBUG));
        
        AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE").
            addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        appenderBuilder.add(builder.newLayout("PatternLayout").
            addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable"));
        appenderBuilder.add(builder.newFilter("MarkerFilter", Filter.Result.DENY,
            Filter.Result.NEUTRAL).addAttribute("marker", "FLOW"));
        builder.add(appenderBuilder);
        
        builder.add(builder.newLogger("org.apache.logging.log4j", Level.DEBUG).
            add(builder.newAppenderRef("Stdout")).
            addAttribute("additivity", false));
        
        builder.add(builder.newRootLogger(Level.ERROR).add(builder.newAppenderRef("Stdout")));
        
        
    }
    
    private static MyConfiguration configInstance;
    
    static Configuration get(final ConfigurationSource source) {
        builder.setConfigurationSource(source);
        
        configInstance = builder.build();
        
        final InputStream configStream = source.getInputStream();
        final Properties properties = new Properties();
        try {
            properties.load(configStream);
        } catch (final IOException ioe) {
            throw new ConfigurationException("Unable to load " + source.toString(), ioe);
        }
        
        if(properties.containsKey("fileLogger")) {
            String fileName = properties.getProperty("fileLogger");
            String logName = "file";
            
            // 如果log没有配置, 配置名字就为""
            if("".equals(getConfigName(logName))) {
                Layout layout = PatternLayout.createLayout(PatternLayout.SIMPLE_CONVERSION_PATTERN, null, configInstance, null,
                    null,false, false, null, null);
                Appender appender = FileAppender.createAppender("target/"+fileName, "false", "false", "File", "true",
                    "false", "false", "4000", layout, null, "false", null, configInstance);
                appender.start();
                configInstance.addAppender(appender);
                
                AppenderRef ref = AppenderRef.createAppenderRef("File", null, null);
                AppenderRef[] refs = new AppenderRef[] {ref};
                LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.INFO, logName,
                    "true", refs, null, configInstance, null );
                loggerConfig.addAppender(appender, null, null);
                configInstance.addLogger("file", loggerConfig); 
            }
        }
        
        LOGGER.info("MyConfiguration get comlete");
        //LOGGER.error("MyConfiguration get comlete");
        return configInstance;
    }
    

    @Override
    public Configuration reconfigure() {
        try {
            final ConfigurationSource source = configInstance.getConfigurationSource().resetInputStream();
            if (source == null) {
                return null;
            }
            final MyConfigurationFactory factory = new MyConfigurationFactory();
            return factory.getConfiguration(source);
        } catch (final IOException ex) {
            LOGGER.error("Cannot locate file {}: {}", configInstance.getConfigurationSource(), ex);
        }
        return null;
    }
    
    private static String getConfigName(String logName) {
        LoggerConfig loggerConfig = configInstance.getLoggerConfig(logName);
        return loggerConfig.getName();
    }
    
}