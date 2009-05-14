package com.googlecode.nagioswas;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class Config {

    private static Properties cachedConfig;
    
    private static Properties lazyLoad() {
        if(cachedConfig == null) {
            cachedConfig  = new Properties();
            try {
                cachedConfig.load(new FileInputStream("check_was.servers"));
            } catch (IOException e) {
                throw new RuntimeException("Failed to load profile.properties", e);
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
