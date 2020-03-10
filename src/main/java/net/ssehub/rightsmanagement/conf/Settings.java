package net.ssehub.rightsmanagement.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Settings {
    
    public static final Settings INSTANCE = new Settings();
    
    private static final String settingsFile = "settings.properties";
    
    private Properties prop = new Properties();
    
    /**
     * Singleton constructor.
     */
    private Settings() {
        System.out.println(Settings.class.getResource(settingsFile));
        try (InputStream configFile = Settings.class.getResourceAsStream(settingsFile)) {
            prop.load(configFile);            
        } catch (IOException ex) {
            ex.printStackTrace();
         } 
    }
    
    public String get(String key) {
        return prop.getProperty(key);
    }
    
    public int getAsInt(String key) {
        String strValue = get(key);
        return Integer.valueOf(strValue);
    }

}
