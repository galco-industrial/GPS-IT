/*
 * gpsidf3.java
 *
 * Created on June 22, 2010, 5:38 PM
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
public class gpsidf3 extends HttpServlet {
    
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
        String familyAlias = ""; 
        ArrayList <String> indexList = null;
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
            if (b1.equals("Delete")) {
                // Set our local variables from form vars
                
                activeCode = request.getParameter("active").substring(0,1);
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
                    completedOK = true;
                    message = " Found Index Alias for division CP '" + familyAlias + " / " + subfamilyAlias + "' OK";
                    sWork = uStamp + message;
                    debug (debugLevel, 5, sWork);
                }
                rs.close();
                conn.closeStatement();
            }
            if (completedOK) {
                debug (debugLevel, 4, uStamp + " Attempting to delete division CP Index Alias " + familyAlias + " / " + subfamilyAlias
                       + " for family/subfamily " + familyCode + "/" + subfamilyCode);
                SQLCommand = "DELETE FROM pub.ps_index_entry";
                SQLCommand += " WHERE family_alias = '" + familyAlias + "'";
                SQLCommand += " AND subfamily_alias = '" + subfamilyAlias + "'";
                SQLCommand += " AND family_code = '" + familyCode + "'";
                SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
                SQLCommand += " AND division = 'CP'";
                completedOK = conn.runUpdate(SQLCommand);
                message = " Index Alias '" + familyAlias + " / " + subfamilyAlias + "' was";
                if (!completedOK) {
                    message += " NOT";
                }
                message += " deleted successfully for family/subfamily " + familyCode + "/" + subfamilyCode;
                debug (debugLevel, 4, uStamp + message);
            } else {
                message = " Could not find Index Alias for '" + familyAlias + " / " + subfamilyAlias + "' - unusual Error!";
                    sWork = uStamp + message;
                    debug (debugLevel, 0, sWork);
            }     
            
            indexList = new ArrayList <String> ();
            queryString = "SELECT family_code, subfamily_code, family_alias, subfamily_alias, ";
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
