/*
 * gpsobf2.java
 *
 * Created on July 28, 2008, 3:55 PM
 */

package gpsSelectBoxOptions;

import OEdatabase.WDSconnect;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 *
 *
 * Using the family code and subfamily code I will get a list of fields
 * that are numeric or string fields and have a text box as the DE object.
 * Then I call a JSP to allow the user to select which field
 * whose data I will export.
 *
 * Modification History
 *
 */
public class gpsobf2 extends HttpServlet {
    
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
        
        String auditUserID = "";
        String enableToolTips = "";
        String familyCode = "";
        String familyName = "";
        GPSfieldSet fieldSet = null;
        String productLineCode = "";
        String productLineName = "";
        GPSrules ruleSet = null;               // a convenient rules object to point to a ruleSets[] item
        GPSrules ruleSets[];                 // Class to create a collection of fields and their rules
        String message = "";
        String subfamilyCode = "";
        String subfamilyName = "";
        GPSunit units = null;
        String work = "";
        
        /* Check for invalid Call  i.e., validation key must be set to "OK" */

        work = request.getParameter("validation");
        if (!work.equals("OK")) {
            response.sendRedirect ("gpsnull.htm");
            return;
        }
    
        String b1 = request.getParameter("B1");
        if (b1.equals("Continue")) {
            auditUserID = request.getParameter("auditUserID");
            enableToolTips = request.getParameter("enableToolTips");
            familyCode = request.getParameter("familyCode");
            familyName = request.getParameter("familyName");
            productLineCode = request.getParameter("productLine");
            productLineName = request.getParameter("productLineName");
            subfamilyCode = request.getParameter("subfamilyCode");
            subfamilyName = request.getParameter("subfamilyName");
            
            session.setAttribute("auditUserID", auditUserID);
            session.setAttribute("enableToolTips", enableToolTips);
            session.setAttribute("sbFamilyCode", familyCode);
            session.setAttribute("sbProductLineCode", productLineCode);
            session.setAttribute("sbSubfamilyCode", subfamilyCode);
        } else {
            response.sendRedirect ("gpsnull.htm");
            return;
        }
    
        try {
            
            debug (debugLevel, 4, uStamp + " Looking up parametric rules for family/subfamily...");
            fieldSet = new GPSfieldSet();
            ruleSets = fieldSet.getRules(conn, familyCode, subfamilyCode, GPSfieldSet.SEQUENCE_NUMBER_ORDER);
            ArrayList <String> rulesList = new ArrayList <String> ();
            for (int i = 0; i < fieldSet.count(); i++) {
                work = ruleSets[i].getDataType();
                debug (debugLevel, 4, uStamp + " RuleSet Seq Num is " + ruleSets[i].getSeqNum());
                debug (debugLevel, 4, uStamp + " Field Name is " + ruleSets[i].getParmName());
                debug (debugLevel, 4, uStamp + " Data Type is " + ruleSets[i].getDataType());
                debug (debugLevel, 4, uStamp + " DE Object is " + ruleSets[i].getDeObject());
                debug (debugLevel, 4, uStamp + " DE Text Box Size is " + ruleSets[i].getDeTextBoxSize());
                if ("NS".contains(work)
                        && ruleSets[i].getDeObject().equals("T")
                        && ruleSets[i].getDeTextBoxSize() > 0) {
                    debug (debugLevel, 4, uStamp + " processing seq num " + ruleSets[i].getSeqNum());
                    debug (debugLevel, 4, uStamp + " rule scope is " + ruleSets[i].getRuleScope());
                    debug (debugLevel, 4, uStamp + " subfamily code is " + subfamilyCode);
                    rulesList.add(ruleSets[i].getArrayListElement());
                }
            }
            
            if (rulesList.size() == 0) {
                message = "No eligible parm fields were found for this Family/Subfamily.";
                debug (debugLevel, 2, uStamp + message); 
            }
            
            session.setAttribute("sRuleSets", ruleSets);
                  
            request.setAttribute("familyCode", familyCode);
            request.setAttribute("familyName", familyName);
            request.setAttribute("productLineCode", productLineCode);
            request.setAttribute("productLineName", productLineName);
            request.setAttribute("rulesList", rulesList);
            request.setAttribute("statusMessage", message);
            request.setAttribute("subfamilyCode", subfamilyCode);
            request.setAttribute("subfamilyName", subfamilyName);
            RequestDispatcher view = request.getRequestDispatcher("gpsobf2.jsp");
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
