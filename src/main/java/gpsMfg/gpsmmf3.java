/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gpsMfg;

import OEdatabase.WWWconnect;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;
import sauter.util.DateTime;

/**
 *
 * @author dunlop
 */
public class gpsmmf3 extends HttpServlet {
    private boolean debugSw = true;
    private final String SERVLET_NAME = "gpsmmf2.java";
    private final String VERSION = "7.1.00";
    
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
       
        // Connect to WWW database 
        WWWconnect conn = new WWWconnect();
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        boolean activeBoolean = false;
        String activeCode = "";
        boolean aliasActiveHasChanged = false;
        String auditDate = "";
        String auditTime = DateTime.getTimeHHMMSS("");
        String auditUserID = "";
        String mfgCode = "";
        String mfgAlias = ""; 
        boolean completedOK = false;     
        String enableToolTips = "";
        ArrayList <String> mfgNames = null;
        String message = "";
        String oldActiveCode = "";
        String oldmfgAliasCode = "";
        String oldmfgAliasName = "";
        String queryString = "";
        int rc = 0;
        ResultSet rs = null;
        String SQLCommand = "";     
        String work = "";     
        RequestDispatcher view;
        
        // Check Permissions !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        try {         
            // Check for invalid Call  i.e., validation key must be set to "OK" 
            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }             
            //  Get Initial set up and save in Session variables if we got xtrol from gpsmmf2.
            String b1 = request.getParameter("B1");
            if (b1.equals("Modify")) {
                activeCode = request.getParameter("active").substring(0,1);
                auditUserID = request.getParameter("auditUserID");
                enableToolTips = request.getParameter("enableToolTips");             
                oldmfgAliasCode = request.getParameter("mfgAliasCode");
                oldmfgAliasName = request.getParameter("mfgAliasName");
                oldActiveCode = request.getParameter("oldActive");                        
                
                //  Set/Update session vars
                session.setAttribute("aiActiveCode", activeCode);
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);                
                debug (debugLevel, 8, uStamp + " processed form variables.");
             } else {
                conn.close();
                response.sendRedirect ("gpsnull.htm");
                return;
            }
            
        } catch (Exception e){
            e.printStackTrace();
            request.setAttribute("message", "An error occurred in module " + SERVLET_NAME);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        // Make sure the new Alias values do not exist    
        try {
            // Are we changing the Manufacturer Alias?
            work = activeCode.equals("Yes") ? "1" : "0";
            aliasActiveHasChanged = !(oldActiveCode.equals(activeCode));
            completedOK = true;
            // If alias name(s) changed, make sure the new name does not already exist
            if (aliasActiveHasChanged) {
                queryString = "SELECT *";
                queryString += " FROM pub.ps_index_mfg";
                queryString += " WHERE mfg_alias = '" + oldmfgAliasName + "'";
                queryString += " AND mfg_code = '" + oldmfgAliasCode + "'";
                queryString += " AND active = '" + work + "'";
                rs = conn.runQuery(queryString);
                if (rs != null) {
                    if (rs.next()) {
                        completedOK = false;
                        message = " Unable to Modify; Manufacturer Alias for '" + oldmfgAliasName + " / " + oldmfgAliasCode + "' already exists.";
                        sWork = uStamp + message;
                        debug (debugLevel, 0, sWork);
                    }
                    rs.close();
                    conn.closeStatement();
                }                
            }
            // Now delete the old record and add the new
            if (completedOK) {
                debug (debugLevel, 4, uStamp + " Attempting to delete Manufacturer Alias " + oldmfgAliasName + " / " + oldmfgAliasCode);
                SQLCommand = "DELETE FROM pub.ps_index_mfg";
                SQLCommand += " WHERE mfg_alias = '" + oldmfgAliasName + "'";
                SQLCommand += " AND mfg_code = '" + oldmfgAliasCode + "'";                
                completedOK = conn.runUpdate(SQLCommand);
                if (!completedOK) {
                    message = " Error attempting to update Manufcaturer Alias '" + oldmfgAliasName + " / " + oldmfgAliasCode + "'";
                }
            }
            if (completedOK) {
                debug (debugLevel, 4, uStamp + " Attempting to delete Manufacturer Alias " + oldmfgAliasName + " / " + oldmfgAliasCode);
                work = activeCode.equals("Yes") ? "1" : "0";
                debug (debugLevel, 4, uStamp + "active = " + work);
                debug (debugLevel, 4, uStamp + "activeCode = " + activeCode);
                auditDate = DateTime.getDateYYYYMMDD();
                auditTime = auditTime.subSequence(0,2) + ":" + auditTime.substring(2,4) + ":" + auditTime.substring(4); 
                SQLCommand = "INSERT INTO pub.ps_index_mfg";
                SQLCommand += " (mfg_code, mfg_alias, ";
                SQLCommand += " audit_date, audit_time, audit_userid, active)";
                SQLCommand += " VALUES ('" + oldmfgAliasCode + "','" 
                          + oldmfgAliasName + "', { d '" + auditDate  + "' },'" 
                          + auditTime + "','" + auditUserID.toLowerCase()  + "','" + work + "')";
                completedOK = conn.runUpdate(SQLCommand);
                message = " Manufacturer Alias '" + oldmfgAliasName + " / " + oldmfgAliasCode + "' was";
                if (!completedOK) {
                    message += " NOT";
                }
                message += " updated successfully.";
                debug (debugLevel, 4, uStamp + message);
            }
            mfgNames = new ArrayList <String> ();
            queryString = "SELECT mfg_code, mfg_alias, ";
            queryString += " audit_date, audit_time, audit_userid, active ";
            queryString += " FROM pub.ps_index_mfg";
            queryString += " ORDER BY mfg_alias";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    mfgAlias = rs.getString("mfg_alias");
                    mfgCode = rs.getString("mfg_code");                    
                    activeBoolean = rs.getBoolean("active");
                    activeCode = activeBoolean ? "Yes" : "No";
                    work = "'" + mfgCode                            
                            + "','" + mfgAlias
                            + "','" + activeCode
                            + "'";
                    mfgNames.add(work);
                }
                rs.close();
                conn.closeStatement();
            }
                       
            request.setAttribute("mfgNames", mfgNames);
            request.setAttribute("statusMessage", message);
            view = request.getRequestDispatcher("gpsmmf1.jsp");
            view.forward(request,response);        
         } catch (Exception e) {
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            view = request.getRequestDispatcher("showMessage.jsp");
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
