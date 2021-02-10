/*
 * gpsicf2.java
 *
 * Created on June 21, 2010, 2:29 PM
 */

package gpsIndex;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import sauter.util.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version
 * *
 * 09/28/2010 DES fixed to correctly support division CP and family code / subfamily code.
 *
 */
public class gpsicf2 extends HttpServlet {
    
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.5.01";
    
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
        
        String activeCode = "";
        String auditDate = "";
        String auditTime = DateTime.getTimeHHMMSS("");
        String auditUserID = "";
        boolean completedOK = true;
        String enableToolTips = "";
        String familyCode = "";
        String familyAlias = "";    
        String message = "";
        String productLine = "";
        String queryString = "";
        int rc = 0;
        ResultSet rs = null;
        String SQLCommand = "";
        String subfamilyAlias = "";
        String subfamilyCode = "";
        String work = "";
        
        try {    
        
            /* Check for invalid Call  i.e., validation key must be set to "OK" */

            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }

            //  Get Initial set up and save in Session variables if we got xtrol from gpsicf1.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Create")) {
                // Set our local variables from form vars
                
                activeCode = request.getParameter("active");
                auditUserID = request.getParameter("auditUserID");
                enableToolTips = request.getParameter("enableToolTips");
                familyAlias = request.getParameter("familyAlias");
                familyCode = request.getParameter("familyCode");
                productLine = request.getParameter("productLine");
                subfamilyAlias = request.getParameter("subfamilyAlias");
                subfamilyAlias = subfamilyAlias.equals("<none>") ? "" : subfamilyAlias;
                subfamilyCode = request.getParameter("subfamilyCode");
                subfamilyCode = subfamilyCode.equals("") ? "*" : subfamilyCode;
                
                //  Set/Update session vars
                session.setAttribute("aiActiveCode", activeCode);
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);
                session.setAttribute("aiProductLineCode", productLine);
                session.setAttribute("aiFamilyCode", familyCode);
                session.setAttribute("aiFamilyAlias", familyAlias);
                session.setAttribute("aiSubfamilyAlias", subfamilyAlias);
                session.setAttribute("aiSubfamilyCode", subfamilyCode);
                debug (debugLevel, 8, uStamp + " processed form variables.");
            } else {
                conn.close();
                response.sendRedirect ("gpsnull.htm");
                return;
            }
        } catch (Exception e){
            conn.close();
            e.printStackTrace();
            request.setAttribute("message", "An error occurred in module " + SERVLET_NAME);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        // Make sure the Alias we want to add did not sneak into database somehow
    
        try {
            auditDate = DateTime.getDateYYYYMMDD();
            auditTime = auditTime.subSequence(0,2) + ":" + auditTime.substring(2,4) + ":" + auditTime.substring(4); 
            debug (debugLevel, 10, uStamp + " Audit date is " + auditDate);
            queryString = "SELECT *";
            queryString += " FROM pub.ps_index_entry";
            queryString += " WHERE family_alias = '" + familyAlias + "'";
            queryString += " AND subfamily_alias = '" + subfamilyAlias + "'";
            queryString += " AND family_code = '" + familyCode + "'";
            queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            queryString += " AND division = 'CP'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    completedOK = false;
                    message = " Error! Index Alias for '" + familyAlias + " / " + subfamilyAlias + "' already exists for this Family Code / Subfamily Code in Division CP!";
                    sWork = uStamp + message;
                    debug (debugLevel, 0, sWork);
                }
                rs.close();
                conn.closeStatement();
            }
            if (completedOK) {
                debug (debugLevel, 4, uStamp + " Attempting to create Index Alias " + familyAlias + " / " + subfamilyAlias
                       + " for CP division family/subfamily " + familyCode + "/" + subfamilyCode);
                work = activeCode.equals("Y") ? "1" : "0";
                SQLCommand = "INSERT INTO pub.ps_index_entry";
                SQLCommand += " (division, family_code, subfamily_code, family_alias, subfamily_alias, audit_date, audit_time, audit_userid, active)";
                SQLCommand += " VALUES ( 'CP','" + familyCode + "','" + subfamilyCode + "','" 
                          + familyAlias + "','" + subfamilyAlias + "', { d '" + auditDate  + "' },'" 
                          + auditTime + "','" + auditUserID  + "','" + work + "')";
                completedOK = conn.runUpdate(SQLCommand);
                message = " Index Alias '" + familyAlias + " / " + subfamilyAlias + "' was";
                if (!completedOK) {
                    message += " NOT";
                }
                message += " created successfully for CP division family/subfamily " + familyCode + "/" + subfamilyCode;
                debug (debugLevel, 4, uStamp + message);
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
            
            request.setAttribute("lines", lines);
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsicf1.jsp");
            view.forward(request,response);
            debug (debugLevel, 4, uStamp + " re-invoked gpsicf1.jsp.");
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
