/*
 * gpsidf1.java
 *
 * Created on June 21, 2010, 4:58 PM
 */

package gpsIndex;

import OEdatabase.WDSconnect;
import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version
 * *
 * 09/28/2010 DES fixed to correctly support division CP and family code / subfamily code.
 *
 */
public class gpsidf1 extends HttpServlet {
    
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
        String familyAlias = "";
        String familyCode = "";
        String listItem = "";
        String message = "";
        String queryString = "";
        ResultSet rs = null;
        String subfamilyAlias = "";
        String subfamilyCode = "";
        String work = "";
        ArrayList <String> indexList = null;
    
        // Build query to extract existing Index Aliases from the database
    
        try {
            indexList = new ArrayList <String> ();
            queryString = "SELECT division, family_code, subfamily_code, family_alias, subfamily_alias, ";
            queryString += " audit_date, audit_time, audit_userid, active ";
            queryString += " FROM pub.ps_index_entry";
            queryString += " WHERE division = 'CP'";
            queryString += " ORDER BY family_alias, subfamily_alias";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    auditDate = rs.getDate("audit_date").toString();
                    auditTime = rs.getString("audit_time");
                    auditUserID = rs.getString("audit_userid");
                    familyAlias = rs.getString("family_alias");
                    familyCode = rs.getString("family_code");
                    subfamilyAlias = rs.getString("subfamily_alias");
                    //subfamilyAlias = subfamilyAlias.equals("") ? "&lt;none&gt;" : subfamilyAlias;
                    subfamilyCode = rs.getString("subfamily_code");
                    subfamilyCode = subfamilyCode.equals("") ? "*" : subfamilyCode;
                    activeBoolean = rs.getBoolean("active");
                    activeCode = activeBoolean ? "Yes" : "No";
                    work = "'" + familyAlias
                            + "','" + subfamilyAlias
                            + "','" + familyCode
                            + "','" + subfamilyCode
                            + "','" + auditUserID
                            + "','" + auditDate
                            + "','" + auditTime
                            + "','" + activeCode
                            + "'";
                    indexList.add(work);
                }
                rs.close();
                conn.closeStatement();
                conn.close();
            }
            //session.setAttribute("sessionIndexList", indexList);
            
            request.setAttribute("indexList", indexList);
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsidf1.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
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
