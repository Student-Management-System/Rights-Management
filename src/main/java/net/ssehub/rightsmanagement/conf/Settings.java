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
        try (InputStream configFile = Settings.class.getResourceAsStream(settingsFile)) {
            prop.load(configFile);            
        } catch (IOException ex) {
            ex.printStackTrace();
         } 
    }
    
    /**
     * Returns the value of the specified setting.
     * @param key The setting for which the value should be returned.
     * @return The value of the specified setting, or <tt>null</tt> if it wasn't specified.
     */
    public String get(String key) {
        return prop.getProperty(key);
    }
    
    /**
     * Returns the value of the specified setting as integer.
     * @param key The setting for which the value should be returned.
     * @return The value of the specified setting, or <tt>0</tt> if it wasn't specified.
     */
    public int getAsInt(String key) {
        String strValue = get(key);
        int result = 0;
        if (null != strValue) {
            try {
                result = Integer.valueOf(strValue);
            } catch (NumberFormatException exc) {
                //TODO SE: Log exception
                exc.printStackTrace();
            }
        }
        return result;
    }

}
