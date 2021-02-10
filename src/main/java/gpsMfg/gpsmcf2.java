/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gpsMfg;

import OEdatabase.WWWconnect;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import sauter.util.DateTime;
/**
 *
 * @author dunlop
 */
public class gpsmcf2 extends HttpServlet {
   private final String SERVLET_NAME = "gpsmcf2.java";
    private final String VERSION = "7.1.00";
    
    
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
        RequestDispatcher view = null;
        
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
            sWork = uStamp + " failed to connect to WWW database; aborting.";
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }  
        
        String auditUserID = "";
        boolean completedOK = false;
        String enableToolTips = "";
        String mfgCode = "";
        String mfgName = "";
        String mfgAliasName = "";
        String activeCode = "";
        String work = "";
        String queryString = "";
        ResultSet rs = null;
        String SQLCommand = "";
        String message = "";
        
        try {    
        
            /* Check for invalid Call  i.e., validation key must be set to "OK" */
            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }
            //  Get Initial set up and save in Session variables if we got xtrol from gpsmcf1.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Create")) {
                // Set our local variables from form vars
                mfgAliasName = request.getParameter("mfgAlias");
                mfgName = request.getParameter("names");
                activeCode = request.getParameter("active");
                auditUserID = request.getParameter("auditUserID");                
                enableToolTips = request.getParameter("enableToolTips");          
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
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        int div = mfgName.lastIndexOf('-'); 
        if (div != -1){ 
            mfgCode = mfgName.substring(div + 1);            
        }
                
        try {
            queryString = "SELECT *";
            queryString += " FROM pub.ps_index_mfg";
            queryString += " WHERE mfg_alias = '" + mfgAliasName + "'";            
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    message = " Error! Manufacturer Alias " + mfgAliasName + " already exists!";
                    debug (debugLevel, 0, sWork);
                    request.setAttribute("statusMessage", message); // "Create Operation" Completion Code Message
                    view = request.getRequestDispatcher("gpsmcf1.jsp");
                    view.forward(request,response);                        
                    rs.close();
                    conn.closeStatement();
                    conn.close();
                    return;
                }
                rs.close();
                conn.closeStatement();
            }
            
            queryString = "SELECT *";
            queryString += " FROM pub.ps_index_mfg";
            queryString += " WHERE mfg_alias = '" + mfgAliasName + "'";
            queryString += " AND mfg_code = '" + mfgCode + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    sWork = uStamp + " Error! Manufacturer Alias " + mfgAliasName + "/" + mfgCode + " already exists!";
                    debug (debugLevel, 0, sWork);
                    request.setAttribute("message", sWork);                    
                    view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    rs.close();
                    conn.closeStatement();
                    conn.close();
                    return;
                }
                rs.close();
                conn.closeStatement();
            }
            
            debug (debugLevel, 4, uStamp + " Attempting to create Manufacturer Alias " + mfgAliasName 
                    + " for Manufacturer Code " + mfgCode);
            String auditDate = DateTime.getDateYYYYMMDD();            
            String auditTime = DateTime.getTimeHHMMSS("");
            auditTime = auditTime.subSequence(0,2) + ":" + auditTime.substring(2,4) + ":" + auditTime.substring(4);
            work = activeCode.equals("Y") ? "1" : "0";
            SQLCommand = "INSERT INTO pub.ps_index_mfg";
            SQLCommand += " (mfg_code,mfg_alias,audit_date,audit_time,audit_userid,active)";
            SQLCommand += " VALUES ( '" + mfgCode + "','" + mfgAliasName + "',{ d '" + auditDate  + "' },'" + auditTime + "','" + auditUserID.toLowerCase() + "','" + work + "')";
           
            completedOK = conn.runUpdate(SQLCommand);
            
            message = " Manufacturer Alias Name " + mfgAliasName + " for Manufacturer Code " + mfgCode + " was";
            if (!completedOK) {
                message += " NOT";
            }
            message += " created successfully.";
            debug (debugLevel, 4, uStamp + message);
            if (completedOK) {
                conn.close();
            }
            
            request.setAttribute("statusMessage", message); // "Create Operation" Completion Code Message
            view = request.getRequestDispatcher("gpsmcf1.jsp");
            view.forward(request,response);
            debug (debugLevel, 4, uStamp + " Re-invoked gpsmcf1.jsp.");      
            
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
