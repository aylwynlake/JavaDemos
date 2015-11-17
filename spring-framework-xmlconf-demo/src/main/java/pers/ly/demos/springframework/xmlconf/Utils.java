package pers.ly.demos.springframework.xmlconf;

import java.util.Properties;

public class Utils {

	private static Properties databaseConfig;
	
	public static void setDatabaseConfig(Properties databaseConfig) {
		Utils.databaseConfig = databaseConfig;
	}

	public static Properties getDatabaseConfig() {
		return databaseConfig;
	}
}
