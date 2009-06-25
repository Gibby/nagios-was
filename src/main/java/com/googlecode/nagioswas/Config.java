package com.googlecode.nagioswas;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class Config {

    private static final String CONFIG_FILE_NAME = "check_was.servers";
    private static Properties cachedConfig;
    
    private static Properties lazyLoad() {
        if(cachedConfig == null) {
            cachedConfig  = new Properties();
            try {
                cachedConfig.load(new FileInputStream(CONFIG_FILE_NAME));
            } catch (IOException e) {
                throw new RuntimeException("Failed to load " + CONFIG_FILE_NAME, e);
            }
        }
        
        return cachedConfig;
    }
    
    public static String getString(String profile, String name) {
        Properties config = lazyLoad();
        
        return config.getProperty(profile + "." + name);
    }
    
    
    public static int getInt(String profile, String name) {
        return Integer.parseInt(getString(profile, name));
    }

    public static boolean getBoolean(String profile, String name) {
        return Boolean.parseBoolean(getString(profile, name));
    }

}
