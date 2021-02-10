/*
 * gpsidf2.java
 *
 * Created on June 22, 2010, 3:34 PM
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
 *
 * 09/28/2010 DES fixed to correctly support division CP and family code / subfamily code.
 *
 */
public class gpsidf2 extends HttpServlet {
    
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
        
        boolean activeBoolean = false;
        String activeCode = "";
        String auditDate = "";
        String auditTime = "";
        String auditUserID = "";
        boolean completedOK = false;
        String enableToolTips = "";
        String familyCode = "";
        String familyName = "";
        String familyAlias = "";  
        String message = "";
        String productLine = "";
        String queryString = "";
        int rc = 0;
        ResultSet rs = null;
        String SQLCommand = "";
        String subfamilyAlias = "";
        String subfamilyCode = "";
        String subfamilyName = "";
        String work = "";
        
        try {    
        
            familyAlias = request.getParameter("familyalias");
            subfamilyAlias = request.getParameter("subfamilyalias");
            subfamilyAlias = subfamilyAlias.equals("<none>") ? "" : subfamilyAlias;
            familyCode = request.getParameter("familycode");
            subfamilyCode = request.getParameter("subfamilycode");
            debug (debugLevel, 8, uStamp + " processed form variables.");
            debug (debugLevel, 4, uStamp + " Attempting to Read Index Alias " + familyAlias + " / " + subfamilyAlias
                    + " for Family/Subfamily Code:" + familyCode + "/" + subfamilyCode);
            queryString = "SELECT family_code, subfamily_code, family_alias, subfamily_alias, ";
            queryString += " audit_date, audit_time, audit_userid, active ";
            queryString += " FROM pub.ps_index_entry";
            queryString += " WHERE family_alias = '" + familyAlias + "'";
            queryString += " AND subfamily_alias = '" + subfamilyAlias + "'";
            queryString += " AND family_code = '" + familyCode + "'";
            queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            queryString += " AND division = 'CP'";
            rs = conn.runQuery(queryString);
            if (rs != null && rs.next()) {
                activeBoolean = rs.getBoolean("active");
                activeCode = activeBoolean ? "Yes" : "No";
                auditDate = rs.getDate("audit_date").toString();
                auditTime = rs.getString("audit_time");
                auditUserID = rs.getString("audit_userid");
                familyAlias = rs.getString("family_alias");
                familyCode = rs.getString("family_code");
                subfamilyAlias = rs.getString("subfamily_alias");
                subfamilyCode = rs.getString("subfamily_code");
                subfamilyCode = subfamilyCode.equals("") ? "*" : subfamilyCode;
                completedOK = true;
            }
            rs.close();
            conn.closeStatement();
            
            if (completedOK) {
                message = "";
                familyName = GPSfamilyCodes.lookUpFamilyName(conn, familyCode);
                subfamilyName = GPSsubfamilyCodes.lookUpSubfamilyName(conn, familyCode, subfamilyCode);
                request.setAttribute("statusMessage", message);
                request.setAttribute("activeCode", activeCode);
                request.setAttribute("familyAlias", familyAlias);
                request.setAttribute("subfamilyAlias", subfamilyAlias);
                request.setAttribute("familyCode", familyCode);
                request.setAttribute("familyName", familyName);
                request.setAttribute("subfamilyCode", subfamilyCode);
                request.setAttribute("subfamilyName", subfamilyName);
                request.setAttribute("auditUserID", auditUserID);
                request.setAttribute("auditDate", auditDate);
                request.setAttribute("auditTime", auditTime);
                RequestDispatcher view = request.getRequestDispatcher("gpsidf2.jsp");
                view.forward(request,response);
                return;
            }
            conn.close();
            sWork = uStamp + " could not find Family Alias/Subfamily Alias: " + familyAlias + "/" + subfamilyAlias;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        } catch (Exception e) {
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
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
