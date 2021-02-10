/*
 * gpsrcf2.java
 *
 * Created on April 12, 2007, 5:01 PM
 */

package gpsRules;

import OEdatabase.WDSconnect;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I get set up info for jsp that will get common rule data
 * I also build list of available field numbers for this rule
 *
 * Modifications
 *
 * 09/07/2007   DES 
 * Made changes to support selectBoxFilter, matchOrder, PreviewOrder, and Series Implicit
 * 
 * Modified 10/24/07 by DES to use the GPSrules Class to create a session scoped rules object
 * that stores the rules values during execution of gpsrcf1 thru gpsrcf5 instead of 
 * storing the data in individual session variables.
 *
 * 04/07/2008 DES
 *
 * Make changes to check to make sure that no paramteric data exists if adding a ruleset
 * for a global or local field.
 *
 */
public class gpsrcf2 extends HttpServlet {
            
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.5.00";
     
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
        
        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {        
            sWork = uStamp + " failed to connect to WDS database; aborting.";
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }

        String dataType = "";
        String errMsg = "";
        String familyCode = "";
        boolean globalDataExists = false;
        boolean localDataExists = false;
        String productLineCode = "";
        String ruleScope = "";
        String subfamilyCode = "";
        String seqNumbers = ",";
        String seqNum = "";
        GPSrules ruleSet;
        String work = "";
        
        try {    // Check for invalid Call  i.e., validation key must be set to "OK" 
            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                conn.close();
                response.sendRedirect ("gpsnull.htm");
            }
       
            String b1 = request.getParameter("B1");
            if (b1.equals("Continue")) {
                ruleSet = (GPSrules) session.getAttribute("sRuleSet");
                session.setAttribute("auditUserID", request.getParameter("auditUserID"));
                session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
                
                // Initialize Rule Set object depending on Data Type Code     
                        
                dataType = request.getParameter("dataType");
                if (dataType == null || "LNS".indexOf(dataType) == -1) {         
                    conn.close();
                    request.setAttribute("message", "Module " + SERVLET_NAME + " detected an invalid Data Type code.");
                    view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    return;
                }
                if (dataType.equals("L")) {
                    ruleSet.initLogicalRuleSet();
                } else if (dataType.equals("N")) {
                    ruleSet.initNumericRuleSet();
                } else if (dataType.equals("S")) {
                    ruleSet.initStringRuleSet();
                }

                familyCode = request.getParameter("familyCode");
                productLineCode = request.getParameter("productLine");
                ruleScope = request.getParameter("ruleScope");
                subfamilyCode = request.getParameter("subfamilyCode");
                
                ruleSet.setAuditUserID(request.getParameter("auditUserID"));
                ruleSet.setFamilyCode(familyCode);
                ruleSet.setFamilyName(request.getParameter("familyName"));
                ruleSet.setProductLineCode(productLineCode);
                ruleSet.setProductLineName(request.getParameter("productLineName"));
                ruleSet.setRuleScope(ruleScope);
                ruleSet.setSubfamilyCode(subfamilyCode);
                ruleSet.setSubfamilyName(request.getParameter("subfamilyName")); 
                
                // See if Parametric data already exists in this family/subfamily
                
                if (ruleScope.equals("L")) {
                    localDataExists = GPSpart.doesPSDataExist(conn, familyCode, subfamilyCode);
                }
                
                // See if Parametric data already exists in this family
                
                globalDataExists = localDataExists;
                if (!localDataExists && ruleScope.equals("G")) {
                    globalDataExists = GPSpart.doesPSDataExist(conn, familyCode, "*");
                }
                
                if (localDataExists || globalDataExists) {
                    ruleSet.setForceInactive(true);
                    ruleSet.setParmStatus("I");
                }
                                
                request.setAttribute("localDataExists", (localDataExists ? "Y" : "N"));
                request.setAttribute("globalDataExists", (globalDataExists ? "Y" : "N"));
                
                session.setAttribute("sbFamilyCode", familyCode);
                session.setAttribute("sbProductLineCode", productLineCode);
                session.setAttribute("sbSubfamilyCode", subfamilyCode);

                // Build string of existing fields for this scope
                
                seqNumbers = GPSrules.getPreExistingSeqNums(conn, familyCode, subfamilyCode);
                session.setAttribute("seqNumbers", seqNumbers);
                conn.close();
            }
            if (b1.equals("Review and Make Corrections")) {
                session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
            }
            view = request.getRequestDispatcher("gpsrcf2.jsp");
            view.forward(request,response);  
        } catch (Exception e){
            conn.close();
            e.printStackTrace();
            errMsg = "An error occurred in " + SERVLET_NAME + ":<br />" + e ;
            request.setAttribute("message", errMsg);
            view = request.getRequestDispatcher("showMessage.jsp");
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
