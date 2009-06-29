package com.googlecode.nagioswas;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class Config {

    private static final String CONFIG_FILE_NAME = "check_was.servers";
    private static Properties cachedConfig;
    
    private static Properties lazyLoad() {
        if(cachedConfig == null) {
            cachedConfig  = new Properties();
            File file = null;
            try {
                String pluginHome = System.getProperty("plugin.home");
                if(pluginHome == null) {
                    file = new File(CONFIG_FILE_NAME);
                } else {
                    file = new File(pluginHome, CONFIG_FILE_NAME);
                }
                
                cachedConfig.load(new FileInputStream(file));
            } catch (IOException e) {
                throw new RuntimeException("Failed to load config file from " + file.getAbsolutePath(), e);
            }
        }
        
        return cachedConfig;
    }
    
    public static String getString(String profile, String name) {
        Properties config = lazyLoad();
        
        return config.getProperty(profile + "." + name);
    }

    public static String getStringNotNull(String profile, String name) {
        Properties config = lazyLoad();
        
        String fullName = profile + "." + name;
        String value = config.getProperty(fullName);
        
        if(value != null) {
            return value;
        } else {
            throw new RuntimeException(fullName + " must be provided, check configuration");
        }
    }
    
    public static int getInt(String profile, String name) {
        return Integer.parseInt(getString(profile, name));
    }

    public static boolean getBoolean(String profile, String name) {
        return Boolean.parseBoolean(getString(profile, name));
    }

}
