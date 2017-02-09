package org.lgbt_news.collect.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author max
 */
public class PropertyPoint {

    private static final String FILE = "./lgbt-news-collect/src/main/resources/config.properties";


    private static List<String> nytKeys;
    private static String databaseUrl;
    private static String databaseUser;
    private static String databasePassword;

    public static List<String> getNytKeys() {
        if (nytKeys == null)
            readProperties();
        return nytKeys;
    }

    public static String getDatabaseUrl() {
        if (databaseUrl == null)
            readProperties();
        return databaseUrl;
    }

    public static String getDatabaseUser() {
        if (databaseUser == null)
            readProperties();
        return databaseUser;
    }

    public static String getDatabasePassword() {
        if (databasePassword == null)
            readProperties();
        return databasePassword;
    }

    private static void readProperties() {
        Properties properties = new Properties();
        BufferedInputStream stream = null;
        try {
            stream = new BufferedInputStream(new FileInputStream(FILE));
            properties.load(stream);
            databaseUrl = properties.getProperty("url");
            databaseUser = properties.getProperty("user");
            databasePassword = properties.getProperty("pw");
            nytKeys = new ArrayList<>(1);
            nytKeys.add(properties.getProperty("nyt"));
        } catch (java.io.IOException e) {
            System.err.println("Cannot load properties from "+FILE+"! " +
                    "Check whether file exists and all keys are correct!\n\tYou are here: "+System.getProperty("user.dir"));
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
