// This file is: JavaProject/AP_project/src/edu/univ/erp/data/ConfigLoader.java
package edu.univ.erp.data;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
            }
            // Load the properties file
            properties.load(input);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // This method will be called by your other classes
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}