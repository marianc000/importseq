/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.Test;
import static org.junit.Assert.*;
import static persistence.MyConnection.getConnection;

/**
 *
 * @author mcaikovs
 */
public class MyConnectionTest {

    public MyConnectionTest() {
    }

    @Test
    public void testGetConnection() throws SQLException {
        Connection conn = getConnection();
        assertNotNull(conn);
        conn.close();
    }

}
