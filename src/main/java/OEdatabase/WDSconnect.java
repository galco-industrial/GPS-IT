/*
 * WDSconnect.java
 *
 * Modified on October 13, 2008, 4:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package OEdatabase;

import java.sql.*;
import javax.naming.*;
import javax.sql.DataSource;
/**
 *
 * @author Sauter
 */
public class WDSconnect {
    
    private Connection conn = null;
    private String errorMessage = "";
    private ResultSet rs = null;
    private Statement st = null;
    
    /** Creates a new instance of WDSconnect */
    public WDSconnect() {
    }
        
    public boolean close() { //Close the connection
        try {
            errorMessage = "";
            if (conn != null) {
                closeStatement();
                conn.close();
                conn = null;
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "WDS Connection Close Error: " + e.toString();
            return false;
        }
    }
    
    public boolean closeStatement() {
        try {
            errorMessage = "";
            if (st != null ) {
                st.close();
                st = null;
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "WDS Statement Close Error: " + e.toString();
            return false;
        }
    }
   
    public boolean commit() {
        try {
            errorMessage = "";
            if (conn != null) {
                conn.commit(); 
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "WDS Commit Error: " + e.toString();
            return false;
        } 
    }
        
    public boolean connect() {
        try {
            Context ctx = new InitialContext();
            Context secCxt = (Context)ctx.lookup("java:comp/env");
            DataSource ds = (DataSource)secCxt.lookup("jdbc/connectWDS");
            conn = ds.getConnection();
            errorMessage = "";
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("***WDS Database Connection Error: " + e.toString());
            return false;
        } 
    }
 
    public boolean disableTransactions() {
        try {
            errorMessage = "";
            if (conn != null) {
                conn.setAutoCommit(true);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "WDS Disable Transactions Error: " + e.toString();
            return false;
        } 
    }
    
    public boolean enableTransactions() {
        try {
            errorMessage = "";
            if (conn != null) {
                conn.setAutoCommit(false);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "WDS Enable Transactions Error: " + e.toString();
            return false;
        } 
    }

    public String getError() {
        return errorMessage;
    }
    
    public boolean rollback() {
        try {
            errorMessage = "";
            if (conn != null) {
                conn.rollback();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "WWW Rollback Error: " + e.toString();
            return false;
        } 
    }
   
    public ResultSet runQuery(String sqlQuery) { // Query the database (SELECT statement)
        try {
            errorMessage = "";
            if (conn != null) {
                st = conn.createStatement();
                rs = st.executeQuery(sqlQuery);
                return rs;
            }
            return null;
        } catch(SQLException e) {
            e.printStackTrace();
            errorMessage = "WDS Query Error: " + e.toString();
            return null;
        }
    }

    public boolean runUpdate(String update) { 
        // Update the database (UPDATE [table], INSERT INTO [table], DELETE FROM [table] statements)
        try {
            errorMessage = "";
            if (conn != null) {
                st = conn.createStatement();
                st.executeUpdate(update);
                st.close();
                st = null;
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "WDS Update Error: " + e.toString();
            return false;
        }
    }

}
