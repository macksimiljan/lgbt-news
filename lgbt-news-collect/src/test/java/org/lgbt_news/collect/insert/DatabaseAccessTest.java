package org.lgbt_news.collect.insert;

import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * @author max
 */
public class DatabaseAccessTest {
    @Test
    public void test_getDbConnection() {
        DatabaseAccess db = new DatabaseAccess();
        Connection conn = db.getDbConnection();
        String url = null;
        try {
            url = conn.getMetaData().getURL();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String expected = "jdbc:mysql://localhost/lgbt_news?autoReconnect=true&useSSL=false";
        assertEquals(expected, url);
        db.closeDbConnection();
    }

    @Test
    public void test_closeDbConnection() {
        DatabaseAccess db = new DatabaseAccess();
        Connection conn = db.getDbConnection();
        try {
            assertFalse(conn.isClosed());
            db.closeDbConnection();
            assertTrue(conn.isClosed());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}