/*
 * gpsruf7.java
 *
 * Created on February 18, 2008, 4:42 PM
 */

package gpsRules;

import OEdatabase.WDSconnect;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I am used to copy a rule set to a new field in a family / subfamily. 
 * I receive a rule set object and will attempt to create a new
 * ruleset for the specified family and subfamily.
 * If necessary I will create a new select box with options for all the
 * associated fields.
 * I must also check to make sure that such select boxes and or field rulesets
 * do not already exist in the new family/subfamily.
 *
 *
 */
public class gpsruf7 extends HttpServlet {
        
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsruf7.java";
    private final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
/* Get a handle on our session */
        HttpSession session = request.getSession();
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
        debugSw = true;
        
        String auditUserID = "";
        int cloneResult = 0;
        String deSelectBoxName = "";
        String message = "";
        String newFamilyCode = "";
        String newFamilyName = "";
        String newProductLineCode = "";
        String newProductLineName = "";
        String newRuleScope = "";
        int newSeqNum = -1;
        String newSubfamilyCode = "";
        String newSubfamilyName = "";
        boolean okSoFar = true;
        String oldFamilyCode = "";
        String oldFamilyName = "";
        String oldProductLineCode = "";
        String oldProductLineName = "";
        String oldRuleScope = "";
        int oldSeqNum = -1;
        String oldSubfamilyCode = "";
        String oldSubfamilyName = "";
        String qSelectBoxName = "";
        boolean rc = false;
        
        GPSrules ruleSet;
        GPSrules ruleSet2;
        GPSselectBox sbWork;

        
        
        // Check Permissions here *************************
    
        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        
        // Get the product line, family, subfamily code, scope, and field Number for the new ruleset
        
        newProductLineCode = request.getParameter("productLine");
        newProductLineName = request.getParameter("productLineName");
        newFamilyCode = request.getParameter("familyCode");
        newFamilyName = request.getParameter("familyName");
        newSubfamilyCode = request.getParameter("subfamilyCode");
        newSubfamilyName = request.getParameter("subfamilyName");
        newRuleScope = request.getParameter("ruleScope");
        newSeqNum = Integer.parseInt(request.getParameter("newSeqNum"));
        auditUserID = (String) session.getAttribute("auditUserID");
        
        // make sure no ruleset currently exists for the potential new ruleset.
        
        try {
            ruleSet2 = new GPSrules();
            if (ruleSet2.read(conn, newFamilyCode, newSubfamilyCode, newRuleScope, newSeqNum)) {
                                
                // Error Rule exists!
                
                message = "Error - a RuleSet already exists for ";
                if (newRuleScope.equalsIgnoreCase("G")) {
                    message += "Global";
                }
                if (newRuleScope.equalsIgnoreCase("L")) {
                    message += "Local";
                }
                message += " Field " + newSeqNum + " in Family Code " + newFamilyCode
                        + " / Subfamily Code " + newSubfamilyCode;
                request.setAttribute("statusMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("gpsruf7.jsp");
                view.forward(request,response);
                return;
            }
            ruleSet2 = null;
        } catch (Exception e) {
            conn.close();
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + " <br />" + e);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }

        // Get a handle to the session-scoped ruleSet
        
        ruleSet = (GPSrules) session.getAttribute("sRuleSet");
           
        // Get the old product line, family, subfamily, scope, and field number

        oldProductLineCode = ruleSet.getProductLineCode();
        oldProductLineName = ruleSet.getProductLineName();
        oldFamilyCode = ruleSet.getFamilyCode();
        oldFamilyName = ruleSet.getFamilyName();
        oldSubfamilyCode = ruleSet.getSubfamilyCode();
        oldSubfamilyName = ruleSet.getSubfamilyName();
        oldRuleScope = ruleSet.getRuleScope();
        oldSeqNum = ruleSet.getSeqNum();
        
        // Here's the deal on cloning of Select Boxes:
            // If a Rule Set is being cloned
            //   and there are NO select boxes involved,
            //     then no new select boxes are created in the target family/subfamily
            //   If Select Boxes are involved and
            //     select boxes by the same name already exist in the family/subfamily
            //       no new select boxes are created.
            //     else if select boxes by the same name do not exist,
            //       a new select box is created in the target family/subfamily
            //       containing a copy of the source select box options
                
        deSelectBoxName = ruleSet.getDeSelectBoxName();
        qSelectBoxName = ruleSet.getQselectBoxName();
                
            /***********************************************************************8
            **  WARNING! THIS needs to be reviewed and cleaned up before attempting to run it
            ***********************************************************************/
        
        try {
    
            // begin transaction
            
            rc = conn.enableTransactions();
                    
            // Copy the old select boxes into the new select boxes
            
            if (deSelectBoxName != null && deSelectBoxName.length() > 0) {
                sbWork = new GPSselectBox();
                cloneResult = sbWork.cloneSelectBox(conn, oldFamilyCode, oldSubfamilyCode,
                    deSelectBoxName, newFamilyCode, newSubfamilyCode, deSelectBoxName);
                sbWork = null;
                if (cloneResult < 0) {
                    // REPORT AN ERROR HERE, roll back AND ABORT
                    conn.rollback();
                    conn.disableTransactions();
                    conn.close();
                    request.setAttribute("message", "A database error occurred while processing select box "
                        + deSelectBoxName);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    return;
                }
            }   
            if (qSelectBoxName != null && qSelectBoxName.length() > 0) {
                sbWork = new GPSselectBox();
                cloneResult = sbWork.cloneSelectBox(conn, oldFamilyCode, oldSubfamilyCode,
                    qSelectBoxName, newFamilyCode, newSubfamilyCode, qSelectBoxName);
                sbWork = null;
                if (cloneResult < 0) {
                    // REPORT AN ERROR HERE, roll back AND ABORT
                    conn.rollback();
                    conn.disableTransactions();
                    conn.close();
                    request.setAttribute("message", "A database error occurred while processing select box "
                        + qSelectBoxName);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    return;
                }
            }
            
            // Save the new family code / subfamily code / scope / field number in the sesssion-scoped
            // Ruleset object.
            
            
            ruleSet.setFamilyCode(newFamilyCode);
            ruleSet.setSubfamilyCode(newSubfamilyCode);
            ruleSet.setSeqNum(newSeqNum);
            ruleSet.setRuleScope(newRuleScope);
                    
        // write the new ruleset
        
            rc = ruleSet.writeRules(conn, newFamilyCode, newSubfamilyCode, Integer.toString(newSeqNum), "A", auditUserID);
            message = "RuleSet for ";
                if (newRuleScope.equalsIgnoreCase("G")) {
                    message += "Global";
                }
                if (newRuleScope.equalsIgnoreCase("L")) {
                    message += "Local";
                }
                message += " Field " + newSeqNum + " in Family Code " + newFamilyCode
                        + " / Subfamily Code " + newSubfamilyCode;
                
            if (rc == false) {
                // rollback Transaction!
                conn.rollback();
                message += " was NOT copied successfully.";
            } else {
                //end transaction
                conn.commit();
                message += " was copied successfully.";
            }
            conn.disableTransactions();
            conn.close();
            
            // Call the JSP and report Results
            
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsruf7.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            conn.rollback();
            conn.disableTransactions();
            conn.close();
            message = "ACK! Something ugly just happened in " + SERVLET_NAME;
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsruf7.jsp");
            view.forward(request,response);
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
