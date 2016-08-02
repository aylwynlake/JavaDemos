package pers.ly.demos.log4j2;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

/**
 * Creates a PropertiesConfiguration from a properties file.
 *
 * @since 2.4
 */
@Plugin(name = "MyConfigurationFactory", category = ConfigurationFactory.CATEGORY)
@Order(10)
public class MyConfigurationFactory extends ConfigurationFactory {

    @Override
    protected String[] getSupportedTypes() {
        return new String[] {".properties"};
    }

    @Override
    public Configuration getConfiguration(final ConfigurationSource source) {
        return MyConfiguration.get(source);
    }
}