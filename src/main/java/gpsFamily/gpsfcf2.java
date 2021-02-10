/*
 * gpsfcf2.java
 *
 * Created on February 9, 2007, 4:58 PM
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
 * Modified 4/05/2010 by DES to support Index Keywords.
 * Modified 7/14/2010 by DES to remove Index Keywords.
 * Modified 9/01/2010 by DES to allow Index updates.
 * Modified 9/29/2010 by DES to support division CP
 * Modified 12/09/2011 by DES to ALWAYS update the Index.
 *
 */
public class gpsfcf2 extends HttpServlet {
    
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
        String altFamilyCode = "";
        String altProductLineCode = "";
        String auditUserID = "";
        boolean completedOK = false;
        int displayOrder = 0;
        String enableToolTips = "";
        String familyCode = "";
        String familyName = "";
        String index = "";
        String keywordsPlural = "";
        String keywordsSingular = "";
        String parentFamilyCode = "";
        String productLineCode = "";
        int j = 0;
        String message = "";
        String queryString = "";
        int rc = 0;
        String SQLCommand = "";
        String work = "";
        ResultSet rs = null;
        
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
                allowParentAll = request.getParameter("allowParentAll");
                altFamilyCode = request.getParameter("altFamilyCode");
                altProductLineCode = request.getParameter("altProductLine");
                auditUserID = request.getParameter("auditUserID");
                familyCode = request.getParameter("familyCode");
                familyName = request.getParameter("familyName");
                index =  "1"; // request.getParameter("index");
                if (index == null) {
                    index = "0";
                }
                keywordsPlural = request.getParameter("pKeywords");
                keywordsSingular = request.getParameter("sKeywords");
                parentFamilyCode = request.getParameter("parentFamilyCode");
                productLineCode = request.getParameter("productLine");
                enableToolTips = request.getParameter("enableToolTips");
                displayOrder = Integer.parseInt(request.getParameter("displayOrder"));
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
    
        // Make sure the Family Code we want to add did not sneak into database somehow
    
        try {
            queryString = "SELECT *";
            queryString += " FROM pub.ps_family";
            queryString += " WHERE family_code = '" + familyCode + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    sWork = uStamp + " Error! Family Code " + familyCode + " already exists!";
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
            debug (debugLevel, 4, uStamp + " Attempting to create Family Code " + familyCode);
            SQLCommand = "INSERT INTO pub.ps_family";
            SQLCommand += " (family_code, family_name, product_line_code, display_order, ";
            SQLCommand += " parent_family_code, alt_product_line_code, alt_family_code, ";
            SQLCommand += " keywords_singular, keywords_plural, index_keywords, allow_parent_all)";
            SQLCommand += " VALUES ( '" + familyCode + "','" + familyName + "', '" + productLineCode + "', " 
                    + displayOrder + ", '" + parentFamilyCode + "', '" + altProductLineCode + "', '"
                    + altFamilyCode + "', '" + keywordsSingular + "', '" + keywordsPlural + "', ''";
            if (allowParentAll.equals("Y")) {
                SQLCommand += ", 1)";
            } else {
                SQLCommand += ", 0)";
            }
            completedOK = conn.runUpdate(SQLCommand);
            message = "Family Code " + familyCode + " was";
            if (!completedOK) {
                message += " NOT";
            }
            message += " created successfully.";
            debug (debugLevel, 4, uStamp + " " + message);
            
            if (completedOK) {
                if (index.equals("1")) {
                    String auditTime = DateTime.getTimeHHMMSS("");
                    String auditDate = DateTime.getDateYYYYMMDD();
                    auditTime = auditTime.subSequence(0,2) + ":" + auditTime.substring(2,4) + ":" + auditTime.substring(4); 
                    debug (debugLevel, 10, uStamp + " Audit date is " + auditDate);
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
                    message += "<br />Family was NOT added to the Index.";
                }
            }
        
            GPSproductLines productLines = new GPSproductLines();
            rc = productLines.open(conn);
            if (rc != 0) {
                sWork = uStamp + " failed to obtain Product Line Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                conn.close();
                return;
            }   
            ArrayList lines = productLines.getArrayList("CP");
            GPSfamilyCodes famCodes = new GPSfamilyCodes();
            rc = famCodes.open(conn);
            if (rc < 0) {
                sWork = uStamp + " failed to obtain Family Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                famCodes = null;
                conn.close();
                return;                
            }
            ArrayList familyCodes = famCodes.getArrayList();
            request.setAttribute("lines", lines);
            request.setAttribute("familyCodes", familyCodes);
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsfcf1.jsp");
            view.forward(request,response);
            productLines = null;
            famCodes = null;
            debug (debugLevel, 4, uStamp + " Generated updated list of Product Line Codes and re-invoked gpsfcf1.jsp.");
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
