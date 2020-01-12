package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesSingleton {

    private static String propertiesPath = "src/main/resources/api.properties";

    private static Properties properties = null;

    public static Properties get() {
        if (properties == null) {
            try (InputStream inputStream = new FileInputStream(propertiesPath)) {
                properties = new Properties();
                properties.load(inputStream);

                return properties;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            return null;
        } else {
            return properties;
        }
    }
}
