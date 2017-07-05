/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author mcaikovs
 */
public class MyConnection {

    String USER_NAME = "cbio_user";
    String PASSWORD = "Tomcat0-";
 static   String CONNECTION_STRING = "jdbc:mysql://sarcdwh-dev/cbioportal?user=cbio_user&password=Tomcat0-&zeroDateTimeBehavior=convertToNull&useSSL=false&logger=com.mysql.jdbc.log.MyStandardLogger&profileSQL=true";
    static Connection conn;

    public static Connection getConnection() throws SQLException {

        if (conn == null) {
            conn = DriverManager.getConnection(CONNECTION_STRING);
        }

        return conn;
    }
}
