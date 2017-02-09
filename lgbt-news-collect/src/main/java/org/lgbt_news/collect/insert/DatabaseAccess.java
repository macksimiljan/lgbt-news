package org.lgbt_news.collect.insert;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Provides database connection as specified in 'config.properties'.
 *
 * @author max
 */
public class DatabaseAccess {

    private Connection dbConnection;
    private String url;
    private String user;
    private String pw;

    public DatabaseAccess() {
        try {
            readProperties();
            connectToDb();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
            handleException(e.getClass().getName());
        }
    }

    public Connection getDbConnection() {
        return dbConnection;
    }

    public void closeDbConnection() {
        try {
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void readProperties() throws IOException {
        Properties properties = new Properties();
        BufferedInputStream stream = null;
        try {
            stream = new BufferedInputStream(new FileInputStream("./src/main/resources/config.properties"));
            properties.load(stream);
            url = properties.getProperty("url");
            user = properties.getProperty("user");
            pw = properties.getProperty("pw");
        } catch (java.io.IOException e) {
            throw new IOException("Cannot load properties from file.");
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void connectToDb() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        dbConnection = DriverManager.getConnection(url, user, pw);
    }

    private void handleException(String exceptionType) {
        if (exceptionType.contains("IOException"))
            System.err.println("Could not read necessary database properties from file!");
        if (exceptionType.contains("SQLException"))
            System.err.println("Could not connect to database at "+url+"!");
        if (exceptionType.contains("ClassNotFoundException"))
            System.err.println("Could not load MySql driver!");

        System.exit(1);
    }


}
