/*
 * gpsbmf4.java
 *
 * Created on February 21, 2007, 3:36 PM
 */

package gpsSubfamily;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;
import sauter.util.*;

/**
 *
 * @author Sauter
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 * Modified 6/09/2009 by DES to support alt/keywords/buys and index fields
 * Modified 4/05/2010 by DES to support Index Keywords.
 * Modified 7/14/2010 by DES to remove Index Keywords.
 *
 * 
 * I make sure original data did not change and
 * make the modifications to the subfamily code in WDS.
 *
 * * Modified 9/29/2010 by DES to support division CP
 * * Modified 12/09/2011 by DES to automatically update the Index Alias table.
 */

public class gpsbmf4 extends HttpServlet {
    
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.5.05";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
            
        /* Boilerplate */
        int debugLevel = 0;
        HttpSession session = null;
        String sWork = "";
        String userID = "";
        String userRole = "";
        String uStamp = "";
        
        session = request.getSession();
        if (session.isNew()) {
            response.sendRedirect ("gpstimeout.htm");
            return;
        }
        sWork = (String) session.getAttribute("debugLevel");
        if (sWork != null) {
            debugLevel = Integer.parseInt(sWork);
        }
        sWork = (String) session.getAttribute("userID");
        if (sWork != null) {
            userID = sWork;
        }
        sWork = (String) session.getAttribute("userRole");
        if (sWork != null) {
            userRole = sWork;
        }
        uStamp = SERVLET_NAME + " Version " + VERSION + " User ID '" + userID + "' Role '" + userRole + "'";
        debug (debugLevel, 10, uStamp + " executing.");        
        /* End Boilerplate */
            
        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {        
            sWork = uStamp + " failed to connect to WDS database; aborting.";
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        String altFamilyCode = "";
        String altSubfamilyCode = "";
        String auditTime = DateTime.getTimeHHMMSS("");
        String auditDate = DateTime.getDateYYYYMMDD();
        String auditUserID = "";
        boolean completedOK = false;
        int displayOrder;
        String enableToolTips = "";
        String familyCode = "";
        String familyName = "";
        //String index = "";
        boolean indexEntryExists = false;
        int indexLevel = 1;
        int j = 0;
        String keywordsIndex = "";
        String keywordsPlural = "";
        String keywordsSingular = "";
        String message = "";
        String oldAltFamilyCode = "";
        String oldAltSubfamilyCode = "";
        int oldDisplayOrder;
        int oldIndexLevel = 0;
        String oldKeywordsIndex = "";
        String oldKeywordsPlural = "";
        String oldKeywordsSingular = "";
        String oldSubfamilyName = "";
        String queryString = "";
        ResultSet rs = null;
        String SQLCommand = "";
        String subfamilyCode = "";
        String subfamilyName = "";
        String work = "";
                
        try {    
        
            /* Check for invalid Call  i.e., validation key must be set to "OK" */

            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }

            //  Get Initial set up and save in Session variables if we got xtrol from gpslmf2.jsp.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Modify")) {
                // Set our local variables from form vars
                altFamilyCode = request.getParameter("altFamilyCode");
                altSubfamilyCode = request.getParameter("altSubfamilyCode");
                auditTime = auditTime.subSequence(0,2) + ":" + auditTime.substring(2,4) + ":" + auditTime.substring(4); 
                auditUserID = request.getParameter("auditUserID");
                enableToolTips = request.getParameter("enableToolTips");
                displayOrder = Integer.parseInt(request.getParameter("displayOrder"));
                //index = request.getParameter("index");
                oldAltFamilyCode = request.getParameter("txtAltFamilyCode");
                oldAltSubfamilyCode = request.getParameter("txtAltSubfamilyCode");
                oldDisplayOrder = Integer.parseInt(request.getParameter("oldDisplayOrder"));
                oldIndexLevel = Integer.parseInt(request.getParameter("txtIndexLevel"));
                oldKeywordsIndex = ""; // request.getParameter("txtKeywordsIndex");
                oldKeywordsPlural = request.getParameter("txtKeywordsPlural");
                oldKeywordsSingular = request.getParameter("txtKeywordsSingular");
                oldSubfamilyName = request.getParameter("oldSubfamilyName");
                familyCode = request.getParameter("familyCode");
                familyName = request.getParameter("txtFamilyName");
                indexLevel = Integer.parseInt(request.getParameter("indexLevel"));
                keywordsIndex = request.getParameter("iKeywords");
                keywordsPlural = request.getParameter("pKeywords");
                keywordsSingular = request.getParameter("sKeywords");
                subfamilyCode = request.getParameter("subfamilyCode");
                subfamilyName = request.getParameter("subfamilyName");
                
                //  Set/Update session vars
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);
                debug (debugLevel, 8, uStamp + " processed form variables.");
            } else {
                response.sendRedirect ("gpsnull.htm");
                return;
            }
        
        } catch (Exception e){
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        // Make sure the Family Code we want to modify did not change somehow
    
        try {
            queryString = "SELECT *";
            queryString += " FROM pub.ps_subfamily";
            queryString += " WHERE family_code = '" + familyCode + "'";
            queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (!rs.next()) {
                    sWork = uStamp + " Error! Subfamily Code '" + subfamilyCode + "' for Family Code '" + familyCode + "' does not exist!";
                    debug (debugLevel, 0, sWork);
                    request.setAttribute("message", sWork);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    rs.close();
                    conn.closeStatement();
                    conn.close();
                    return;
                } else {
                    if (!rs.getString("subfamily_name").equals(oldSubfamilyName)
                            || rs.getInt("display_order") != oldDisplayOrder
                            || !rs.getString("alt_family_code").equals(oldAltFamilyCode)
                            || !rs.getString("alt_subfamily_code").equals(oldAltSubfamilyCode)
                            // || !rs.getString("index_keywords").equals(oldKeywordsIndex)
                            || !rs.getString("keywords_singular").equals(oldKeywordsSingular)
                            || !rs.getString("keywords_plural").equals(oldKeywordsPlural)
                            || rs.getInt("index_level") != oldIndexLevel) {
                        debug (debugLevel, 0, uStamp + " Error! Subfamily Code '" + subfamilyCode + "' for Family Code '" 
                                + familyCode + "' original value(s) have changed!");
                        request.setAttribute("message", "Unusual Error - Subfamily Code " + subfamilyCode + " original value(s) have changed! Try again.");
                        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        rs.close();
                        conn.closeStatement();
                        conn.close();
                        return;
                    }
                }
                rs.close();
            }
        
            // If we get here, the subfamily code we are changing has not
            // been modified by anyone else in the mean time.
        
            // Ummmm Note that the subfamily Code record was NOT locked here.
        
            debug (debugLevel, 4, uStamp + " Attempting to modify Subfamily Code '" + subfamilyCode + "' for Family Code '" + familyCode + "'.");
            SQLCommand = "UPDATE pub.ps_subfamily";
            SQLCommand += " SET subfamily_name = '" + subfamilyName +"'";
            SQLCommand += ", display_order = " + displayOrder;
            SQLCommand += ", alt_family_code = '" + altFamilyCode + "'";
            SQLCommand += ", alt_subfamily_code = '" + altSubfamilyCode + "'";
            // SQLCommand += ", index_keywords = '" + keywordsIndex +"'";
            SQLCommand += ", keywords_singular = '" + keywordsSingular + "'";
            SQLCommand += ", keywords_plural = '" + keywordsPlural + "'";
            SQLCommand += ", index_level = " + indexLevel;
            SQLCommand += " WHERE family_code = '" + familyCode + "'";
            SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
            debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
            completedOK = conn.runUpdate(SQLCommand);
            message = " Subfamily Code " + subfamilyCode + " was";
            if (!completedOK) {
                message += " NOT";
            }
            message += " modified successfully.";
            debug (debugLevel, 2, uStamp + message);
            
            
            /* old code prior to 12/09/2011:
            if (completedOK) {
                if (index.equals("1") && !oldSubfamilyName.equals(subfamilyName)) {
                    // Attempt to delete old subfamily name Index
                    debug (debugLevel, 4, uStamp + " Attempting to delete old Subfamily Index " + oldSubfamilyName
                                + " for subfamily code " + subfamilyCode);
                    SQLCommand = "DELETE FROM pub.ps_index_entry";
                    SQLCommand += " WHERE family_alias = '" + familyName + "'";
                    SQLCommand += " AND subfamily_alias = '" + oldSubfamilyName + "'";
                    SQLCommand += " AND family_code = '" + familyCode + "'";
                    SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
                    SQLCommand += " AND division = 'CP'";
                    completedOK = conn.runUpdate(SQLCommand);
                    message += "<br />Old Subfamily Index '" + oldSubfamilyName + "' was";
                    if (!completedOK) {
                        message += " NOT";
                    }
                    message += " deleted successfully for subfamily Code" + subfamilyCode;
                    debug (debugLevel, 4, uStamp + message);
                            
                    // check to see if new name already exists...
                    
                    queryString = "SELECT *";
                    queryString += " FROM pub.ps_index_entry";
                    queryString += " WHERE family_alias = '" + familyName + "'";
                    queryString += " AND subfamily_alias = '" + subfamilyName + "'";
                    queryString += " AND family_code = '" + familyCode + "'";
                    queryString += " AND subfamily_code = '" + subfamilyCode + "'";
                    queryString += " AND division = 'CP'";
                    rs = conn.runQuery(queryString);
                    if (rs != null) {
                        if (rs.next()) {
                            completedOK = false;
                            message += "<br />** Warning! An Index entry with this Subfamily name already exists! **";
                        }
                        rs.close();
                        conn.closeStatement();
                    }
                    if (completedOK) {
                        debug (debugLevel, 4, uStamp + " Attempting to create Index Alias '" + subfamilyName
                                + "' for subfamily Code " + subfamilyCode);
                        SQLCommand = "INSERT INTO pub.ps_index_entry";
                        SQLCommand += " (division, family_code, subfamily_code, family_alias, subfamily_alias, audit_date, audit_time, audit_userid, active)";
                        SQLCommand += " VALUES ( 'CP','" + familyCode + "','" + subfamilyCode + "','" 
                          + familyName + "','" + subfamilyName + "', { d '" + auditDate  + "' },'" 
                          + auditTime + "','" + auditUserID  + "','1')";
                        completedOK = conn.runUpdate(SQLCommand);
                        message += "<br />Index entry for '" + subfamilyName + "' was";
                        if (!completedOK) {
                            message += " NOT";
                        }
                        message += " created successfully for subfamily Code " + subfamilyCode;
                        debug (debugLevel, 4, uStamp + message);
                    }
                } else {
                    message += "<br />Subfamily changes were NOT applied to the Index.";
                }
            }
            
            */
            
            if (completedOK) {
                // Did subfamily name change?
                if (!oldSubfamilyName.equals(subfamilyName)) {
                    // Attempt to delete old subfamily name Index
                    debug (debugLevel, 4, uStamp + " Attempting to delete old Subfamily Index '" + oldSubfamilyName
                                + "' for subfamily code " + subfamilyCode);
                    SQLCommand = "DELETE FROM pub.ps_index_entry";
                    SQLCommand += " WHERE family_alias = '" + familyName + "'";
                    SQLCommand += " AND subfamily_alias = '" + oldSubfamilyName + "'";
                    SQLCommand += " AND family_code = '" + familyCode + "'";
                    SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
                    SQLCommand += " AND division = 'CP'";
                    completedOK = conn.runUpdate(SQLCommand);
                    message += "<br />Old Subfamily Index '" + oldSubfamilyName + "' was";
                    if (!completedOK) {
                        message += " NOT";
                    }
                    message += " deleted successfully for subfamily Code" + subfamilyCode;
                    debug (debugLevel, 4, uStamp + message);
                }
            }
            
            if (completedOK) {
                                                          
                // check to see if name already exists...
                    
                queryString = "SELECT *";
                queryString += " FROM pub.ps_index_entry";
                queryString += " WHERE family_alias = '" + familyName + "'";
                queryString += " AND subfamily_alias = '" + subfamilyName + "'";
                queryString += " AND family_code = '" + familyCode + "'";
                queryString += " AND subfamily_code = '" + subfamilyCode + "'";
                queryString += " AND division = 'CP'";
                indexEntryExists = false;
                rs = conn.runQuery(queryString);
                if (rs != null) {
                    if (rs.next()) {
                        indexEntryExists = true;
                        message += "<br />** An Index entry with this Subfamily name currently exists. **";
                    }
                    rs.close();
                    conn.closeStatement();
                }
                if (!indexEntryExists && indexLevel > 0) {
                    debug (debugLevel, 4, uStamp + " Attempting to create Index Alias '" + subfamilyName
                            + "' for subfamily Code " + subfamilyCode);
                    SQLCommand = "INSERT INTO pub.ps_index_entry";
                    SQLCommand += " (division, family_code, subfamily_code, family_alias, subfamily_alias, audit_date, audit_time, audit_userid, active)";
                    SQLCommand += " VALUES ( 'CP','" + familyCode + "','" + subfamilyCode + "','" 
                      + familyName + "','" + subfamilyName + "', { d '" + auditDate  + "' },'" 
                      + auditTime + "','" + auditUserID  + "','1')";
                    completedOK = conn.runUpdate(SQLCommand);
                    message += "<br />Index entry for '" + subfamilyName + "' was";
                    if (!completedOK) {
                        message += " NOT";
                    }
                    message += " created successfully for subfamily Code " + subfamilyCode;
                    debug (debugLevel, 4, uStamp + message);
                }
                if (indexEntryExists && indexLevel == 0) {
                    // Attempt to delete the subfamily name Index
                    debug (debugLevel, 4, uStamp + " Attempting to delete Subfamily Index '" + subfamilyName
                                + "' for subfamily code " + subfamilyCode);
                    SQLCommand = "DELETE FROM pub.ps_index_entry";
                    SQLCommand += " WHERE family_alias = '" + familyName + "'";
                    SQLCommand += " AND subfamily_alias = '" + subfamilyName + "'";
                    SQLCommand += " AND family_code = '" + familyCode + "'";
                    SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
                    SQLCommand += " AND division = 'CP'";
                    completedOK = conn.runUpdate(SQLCommand);
                    message += "<br />Subfamily Index '" + subfamilyName + "' was";
                    if (!completedOK) {
                        message += " NOT";
                    }
                    message += " deleted successfully for subfamily Code" + subfamilyCode;
                    debug (debugLevel, 4, uStamp + message);
                }
            } else {
                message += "<br />Subfamily changes were NOT applied to the Index.";
            }
                        
            GPSproductLines productLines = new GPSproductLines();
            if (productLines.open(conn) != 0) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                conn.close();
                return;
            }   

            ArrayList <String> productLinesList = productLines.getArrayList("CP");
            request.setAttribute("lines", productLinesList);
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsbmf1.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } finally {
            conn.close();        
        } 
    }
    
    private void debug (int limit, int level, String x) {
        if (limit >= level) {
            System.out.println(x);
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
