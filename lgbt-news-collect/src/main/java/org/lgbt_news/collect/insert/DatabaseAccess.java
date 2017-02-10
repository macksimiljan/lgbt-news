package org.lgbt_news.collect.insert;

import org.apache.log4j.Logger;
import org.lgbt_news.collect.utils.PropertyPoint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides database connection as specified in 'config.properties'.
 *
 * @author max
 */
public class DatabaseAccess {

    static final Logger logger = Logger.getLogger("infoLogger");

    private Connection dbConnection;
    private String url;
    private String user;
    private String pw;

    public DatabaseAccess() {
        try {
            init();
            connectToDb();
        } catch (SQLException | ClassNotFoundException e) {
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
        }
    }

    private void init() {
        url = PropertyPoint.getDatabaseUrl();
        user = PropertyPoint.getDatabaseUser();
        pw = PropertyPoint.getDatabasePassword();
    }

    private void connectToDb() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        dbConnection = DriverManager.getConnection(url, user, pw);
    }

    private void handleException(String exceptionType) {
        if (exceptionType.contains("SQLException"))
            System.err.println("Could not connect to database at "+url+"!");
        if (exceptionType.contains("ClassNotFoundException"))
            System.err.println("Could not load MySql driver!");

        System.exit(1);
    }


}
