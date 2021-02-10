/*
 * gpsfmf3.java
 *
 * Created on February 13, 2007, 3:08 PM
 */

package gpsFamily;

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
 * Modified 03/25/2010 by DES to support allow_parent_all field
 * Modified 4/05/2010 by DES to support Index Keywords.
 * Modified 7/14/2010 by DES to remove Index Keywords.
 * Modified 12/09/2011 by DES to ALWAYS update the Index.
 * Modified 12/09/2011 by DES to ALWAYS update the Index.
 *
 * I make sure original data did not change and
 * make the modifications to the family code in WDS.
 *
 * Modified 9/29/2010 by DES to support division CP
 *
 */
public class gpsfmf3 extends HttpServlet {
    
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

        String allowParentAll = "";
        String auditTime = DateTime.getTimeHHMMSS("");
        String auditDate = DateTime.getDateYYYYMMDD();
        String auditUserID = "";
        String altFamilyCode = "";
        String altProductLineCode = "";        
        boolean completedOK = false;
        int displayOrder;
        String enableToolTips = "";
        String familyCode = "";
        String familyName = "";
        String index = "";
        String index2 = "";
        int j = 0;
        String message = "";
        String keywordsIndex = "";
        String keywordsPlural = "";
        String keywordsSingular = "";
        int oldDisplayOrder;
        String oldAllowParentAll = "";
        String oldAltProductLineCode = "";
        String oldAltFamilyCode = "";
        String oldFamilyName = "";
        String oldKeywordsIndex = "";
        String oldKeywordsPlural = "";
        String oldKeywordsSingular = "";
        String oldParentFamilyCode = "";
        String oldProductLineCode = "";
        String parentFamilyCode = "";
        String productLineCode = "";
        String queryString = "";
        int rc = 0;
        ResultSet rs = null;
        String SQLCommand = "";
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
                allowParentAll = request.getParameter("allowParentAll");
                altFamilyCode = request.getParameter("altFamilyCode");
                altProductLineCode = request.getParameter("altProductLine");
                auditTime = auditTime.subSequence(0,2) + ":" + auditTime.substring(2,4) + ":" + auditTime.substring(4); 
                auditUserID = request.getParameter("auditUserID");
                displayOrder = Integer.parseInt(request.getParameter("displayOrder"));
                enableToolTips = request.getParameter("enableToolTips");
                familyCode = request.getParameter("familyCode");
                familyName = request.getParameter("familyName");
                index = "1"; // request.getParameter("index");
                index2 = "1"; //request.getParameter("index2");
                keywordsPlural = request.getParameter("pKeywords");
                keywordsSingular = request.getParameter("sKeywords");
                oldAllowParentAll = request.getParameter("txtAllowParentAll");
                oldAltProductLineCode = request.getParameter("txtAltProductLineCode");
                oldAltFamilyCode = request.getParameter("txtAltFamilyCode");
                oldDisplayOrder = Integer.parseInt(request.getParameter("oldDisplayOrder"));
                oldFamilyName = request.getParameter("oldFamilyName");
                oldKeywordsIndex = ""; //request.getParameter("txtKeywordsIndex");
                oldKeywordsPlural = request.getParameter("txtKeywordsPlural");
                oldKeywordsSingular = request.getParameter("txtKeywordsSingular");
                oldParentFamilyCode = request.getParameter("txtParentFamilyCode");
                oldProductLineCode = request.getParameter("txtOldProductLineCode");
                parentFamilyCode = request.getParameter("parentFamilyCode");
                productLineCode = request.getParameter("productLine");
                                
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
        
        // Make sure the Family Code we want to modify did not change somehow
    
        try {
            queryString = "SELECT *";
            queryString += " FROM pub.ps_family";
            queryString += " WHERE family_code = '" + familyCode + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (!rs.next()) {
                    sWork = uStamp + " Error! Family Code " + familyCode + " does not exist!";
                    debug (debugLevel, 0, sWork);
                    request.setAttribute("message", sWork);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    rs.close();
                    conn.closeStatement();
                    conn.close();
                    return;
                } else {
                    work = rs.getBoolean("allow_parent_all") ? "Y" : "N";
                    if (!rs.getString("family_name").equals(oldFamilyName)
                            || !work.equals(oldAllowParentAll)
                            || !rs.getString("product_line_code").equals(oldProductLineCode)
                            || !rs.getString("alt_product_line_code").equals(oldAltProductLineCode)
                            || !rs.getString("alt_family_code").equals(oldAltFamilyCode)
                            || !rs.getString("parent_family_code").equals(oldParentFamilyCode)
                            // || !rs.getString("index_keywords").equals(oldKeywordsIndex)
                            || !rs.getString("keywords_plural").equals(oldKeywordsPlural)
                            || !rs.getString("keywords_singular").equals(oldKeywordsSingular)
                            ||  rs.getInt("display_order") != oldDisplayOrder) {
                        debug (debugLevel, 4, uStamp + " Error! family Code " + familyCode + " original value(s) have changed!");
                        debug (debugLevel, 4, uStamp + " Old Name = " + familyName + "; new Name = " + rs.getString("family_name"));
                        debug (debugLevel, 4, uStamp + " Old Order = " + displayOrder + "; new Order = " + rs.getInt("display_order"));
                        debug (debugLevel, 4, uStamp + " Old Product Line Code = " + productLineCode + "; new Product Line Code = " + rs.getString("product_line_code"));
                        request.setAttribute("message", "Unusual Error - Family Code " + familyCode 
                                + " original value(s) have changed!; Try again.");
                        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        rs.close();
                        conn.closeStatement();
                        conn.close();
                        return;
                    }
                }
                conn.closeStatement();
                rs.close();
            }
        
            // If we get here, the family code we are changing has not
            // been modified by anyone else in the mean time.
        
            // Ummmm Note that the family Code record was NOT locked here.
        
            debug (debugLevel, 6, uStamp + " Attempting to modify Family Code " + familyCode);
            SQLCommand = "UPDATE pub.ps_family";
            SQLCommand += " SET family_name = '" + familyName +"'";
            SQLCommand += ", display_order = " + displayOrder;
            SQLCommand += ", product_line_code = '" + productLineCode +"'";
            SQLCommand += ", alt_product_line_code = '" + altProductLineCode +"'";
            SQLCommand += ", alt_family_code = '" + altFamilyCode +"'";
            SQLCommand += ", parent_family_code = '" + parentFamilyCode +"'";
            // SQLCommand += ", index_keywords = '" + keywordsIndex +"'";
            SQLCommand += ", keywords_plural = '" + keywordsPlural +"'";
            SQLCommand += ", keywords_singular = '" + keywordsSingular +"'";
            if (allowParentAll.equals("Y")) {
                SQLCommand += ", allow_parent_all = 1";
            } else {
                SQLCommand += ", allow_parent_all = 0";
            }
            SQLCommand += " WHERE family_code = '" + familyCode + "'";
            debug (debugLevel, 6, uStamp + " attempting SQL command: " + SQLCommand);
            completedOK = conn.runUpdate(SQLCommand);
            message = "Family Code " + familyCode + " was";
            if (!completedOK) {
                message += " NOT";
            }
            message += " modified successfully by " + SERVLET_NAME;
            debug (debugLevel, 4, uStamp + " " + message);
            
            if (completedOK) {
                if (index.equals("1") && !oldFamilyName.equals(familyName)) {
                    // Attempt to delete old family name Index
                    debug (debugLevel, 4, uStamp + " Attempting to delete old Family Index " + oldFamilyName
                                + " for family " + familyCode);
                    SQLCommand = "DELETE FROM pub.ps_index_entry";
                    SQLCommand += " WHERE family_alias = '" + oldFamilyName + "'";
                    SQLCommand += " AND subfamily_alias = ''";
                    SQLCommand += " AND family_code = '" + familyCode + "'";
                    SQLCommand += " AND subfamily_code = '*'";
                    SQLCommand += " AND division = 'CP'";
                    completedOK = conn.runUpdate(SQLCommand);
                    message += "<br />Old Family Index '" + oldFamilyName + "' was";
                    if (!completedOK) {
                        message += " NOT";
                    }
                    message += " deleted successfully for family Code" + familyCode;
                    debug (debugLevel, 4, uStamp + message);
                            
                    // check to see if new name already exists...
                    
                    queryString = "SELECT *";
                    queryString += " FROM pub.ps_index_entry";
                    queryString += " WHERE family_alias = '" + familyName + "'";
                    queryString += " AND subfamily_alias = ''";
                    queryString += " AND family_code = '" + familyCode + "'";
                    queryString += " AND subfamily_code = '*'";
                    queryString += " AND division = 'CP'";
                    rs = conn.runQuery(queryString);
                    if (rs != null) {
                        if (rs.next()) {
                            completedOK = false;
                            message += "<br />** Warning! An Index entry with this Family name already exists! **";
                        }
                        rs.close();
                        conn.closeStatement();
                    }
                    if (completedOK) {
                        debug (debugLevel, 4, uStamp + " Attempting to create Index Alias '" + familyName
                                + "' for family Code " + familyCode);
                        SQLCommand = "INSERT INTO pub.ps_index_entry";
                        SQLCommand += " (division, family_code, subfamily_code, family_alias, subfamily_alias, audit_date, audit_time, audit_userid, active)";
                        SQLCommand += " VALUES ( 'CP','" + familyCode + "','*','" 
                          + familyName + "','', { d '" + auditDate  + "' },'" 
                          + auditTime + "','" + auditUserID  + "','1')";
                        completedOK = conn.runUpdate(SQLCommand);
                        message += "<br />Index entry for '" + familyName + "' was";
                        if (!completedOK) {
                            message += " NOT";
                        }
                        message += " created successfully for family Code " + familyCode;
                        debug (debugLevel, 4, uStamp + message);
                    }
                } else {
                    message += "<br />Family changes were NOT applied to the Index.";
                }
            }
            
            // Now see if we need to make changes to any subfamilies in the index:
            
            if (completedOK) {
                if (index2.equals("1") && !oldFamilyName.equals(familyName)) {
                    debug (debugLevel, 6, uStamp + " Attempting to update indeces for Subfamilies in Family Code " + familyCode );
                    SQLCommand = "UPDATE pub.ps_index_entry";
                    SQLCommand += " SET family_alias = '" + familyName +"'";
                    SQLCommand += " WHERE family_code = '" + familyCode + "'";
                    SQLCommand += " AND family_alias = '" + oldFamilyName + "'";
                    SQLCommand += " AND division = 'CP'";
                    debug (debugLevel, 6, uStamp + " attempting SQL command: " + SQLCommand);
                    completedOK = conn.runUpdate(SQLCommand);
                    message += "Subfamily Indeces for Family Code " + familyCode + " were";
                    if (!completedOK) {
                        message += " NOT";
                    }
                    message += " modified successfully by " + SERVLET_NAME;
                    debug (debugLevel, 4, uStamp + " " + message);
                }
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
            ArrayList lines = productLines.getArrayList("CP");
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

            request.setAttribute("lines", lines);
            request.setAttribute("familyCodes", familyCodes);
            //request.setAttribute("subfamilyCodesList", subfamilyCodes);
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsfmf1.jsp");
            view.forward(request,response);
            productLines = null;
            famCodes = null;
            //subfamCodes = null;
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
