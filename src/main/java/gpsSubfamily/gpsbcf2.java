/*
 * gpsbcf2.java
 *
 * Created on February 20, 2007, 3:35 PM
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
 * Modified 9/01/2010 by DES to allow Index updates.
 * Modified 12/09/2011 by DES to automatically update the Index Alias table.
 *
 *
 * I accept the info to create a new subfamily from gpsbcf1.jsp
 * and attempt to create the new Subfamily in WDS
 *
 * * Modified 9/29/2010 by DES to support division CP
 *
 */
public class gpsbcf2 extends HttpServlet {
    
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
        String auditUserID = "";
        boolean completedOK = false;
        int displayOrder = 0;
        String enableToolTips = "";
        String familyCode = "";
        String familyName = "";
        //String index = "";
        boolean indexEntryExists = false;
        int indexLevel = 0;
        int j = 0;
        String keywordsIndex = "";
        String keywordsPlural = "";
        String keywordsSingular = "";
        String message = "";
        String queryString = "";
        int rc = 0;
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

            //  Get Initial set up and save in Session variables if we got xtrol from gpslcf1.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Create")) {
                // Set our local variables from form vars
                altFamilyCode = request.getParameter("altFamilyCode");
                altSubfamilyCode = request.getParameter("altSubfamilyCode");
                auditUserID = request.getParameter("auditUserID");
                displayOrder = Integer.parseInt(request.getParameter("displayOrder"));
                enableToolTips = request.getParameter("enableToolTips");
                familyCode = request.getParameter("familyCode");
                familyName = request.getParameter("familyName");
                //index =  request.getParameter("index");
                //if (index == null) {
                //    index = "0";
                //}
                indexLevel = Integer.parseInt(request.getParameter("indexLevel"));
                keywordsIndex = ""; //request.getParameter("iKeywords");
                keywordsPlural = request.getParameter("pKeywords");
                keywordsSingular = request.getParameter("sKeywords");
                subfamilyCode = request.getParameter("subfamilyCode");
                subfamilyName = request.getParameter("subfamilyName");
                                
                //  Set/Update session vars
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);
                debug (debugLevel, 4, uStamp + " processed form variables.");
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
    
        // Make sure the Subfamily Code we want to add did not sneak into database somehow
    
        try {
            queryString = "SELECT *";
            queryString += " FROM pub.ps_subfamily";
            queryString += " WHERE family_code = '" + familyCode + "'";
            queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    sWork = uStamp + " Error! Subfamily Code " + subfamilyCode + " already exists!";
                    debug (debugLevel, 0, sWork);
                    request.setAttribute("message", sWork);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    rs.close();
                    conn.closeStatement();
                    conn.close();
                    return;
                }
                rs.close();
                conn.closeStatement();
            }
            debug (debugLevel, 4, uStamp + " Attempting to create Subfamily Code " + subfamilyCode + " for family Code "
                    + familyCode);
            SQLCommand = "INSERT INTO pub.ps_subfamily";
            SQLCommand += " (family_code, subfamily_code, subfamily_name, display_order,";
            SQLCommand += " alt_family_code, alt_subfamily_code, ";
            SQLCommand += " keywords_singular, keywords_plural, index_keywords, index_level)";
            SQLCommand += " VALUES ( '" + familyCode + "','" + subfamilyCode + "','" + subfamilyName + "', "
                    + displayOrder + ", '" + altFamilyCode + "', '" + altSubfamilyCode + "', '"
                    + keywordsSingular + "', '" + keywordsPlural + "', '" + keywordsIndex + "', " + indexLevel + ")";
            completedOK = conn.runUpdate(SQLCommand);
            message = " Subfamily Code " + subfamilyCode + " for Family Code " + familyCode + " was";
            if (!completedOK) {
                message += " NOT";
            }
            message += " created successfully.";
            debug (debugLevel, 4, uStamp + message);
                        
            if (completedOK) {
                String auditTime = DateTime.getTimeHHMMSS("");
                String auditDate = DateTime.getDateYYYYMMDD();
                auditTime = auditTime.subSequence(0,2) + ":" + auditTime.substring(2,4) + ":" + auditTime.substring(4); 
                debug (debugLevel, 10, uStamp + " Audit date is " + auditDate);
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
                        message += "<br />** Warning! An Index entry with this Family/Subfamily name already exists! **";
                    }
                    rs.close();
                    conn.closeStatement();
                }
                    
                if (!indexEntryExists && indexLevel > 0) {
                    debug (debugLevel, 4, uStamp + " Attempting to create Index Alias '" + familyName + "/" + subfamilyName
                            + "' for family/subfamily Code " + familyCode + "/" + subfamilyCode);
                    SQLCommand = "INSERT INTO pub.ps_index_entry";
                    SQLCommand += " (division, family_code, subfamily_code, family_alias, subfamily_alias, audit_date, audit_time, audit_userid, active)";
                    SQLCommand += " VALUES ( 'CP','" + familyCode + "','" + subfamilyCode + "','" 
                      + familyName + "','" + subfamilyName + "', { d '" + auditDate  + "' },'" 
                      + auditTime + "','" + auditUserID  + "','1')";
                    completedOK = conn.runUpdate(SQLCommand);
                    message += "<br />Index entry for '" + familyName + "/" + subfamilyName  + "' was";
                    if (!completedOK) {
                        message += " NOT";
                    }
                    message += " created successfully for family/subfamily Code " + familyCode + "/" + subfamilyCode;
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
                message += "<br />Subfamily Index was not updated.";
            }
            
            GPSproductLines productLines = new GPSproductLines();
            rc = productLines.open(conn);
            if (rc != 0) {
                productLines = null;
                conn.close();
                sWork = uStamp + " failed to obtain Product Line Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }   
            ArrayList <String> lines = productLines.getArrayList("CP");
            
            GPSfamilyCodes famCodes = new GPSfamilyCodes();
            rc = famCodes.open(conn);
            if (rc < 0) {
                productLines = null;
                famCodes = null;
                conn.close();
                sWork = uStamp + " failed to obtain Family Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;                
            }
            ArrayList familyCodes = famCodes.getArrayList();
                        
            GPSsubfamilyCodes subfamCodes = new GPSsubfamilyCodes();
            rc = subfamCodes.open(conn);
            if (rc < 0) {
                productLines = null;
                famCodes = null;
                subfamCodes = null;
                conn.close();
                sWork = uStamp + " failed to obtain Subfamily Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;                
            }
            ArrayList subfamilyCodes = subfamCodes.getArrayList();

            // Add the following goodies to the Request object
            
            request.setAttribute("productLinesList", lines); 
            request.setAttribute("familyCodesList", familyCodes);
            request.setAttribute("subfamilyCodesList", subfamilyCodes); 
            request.setAttribute("statusMessage", message); // "Create Operation" Completion Code Message
            RequestDispatcher view = request.getRequestDispatcher("gpsbcf1.jsp");
            view.forward(request,response);
            debug (debugLevel, 4, uStamp + " Generated updated list of Family and Product Line Codes and re-invoked gpsbcf1.jsp.");
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
