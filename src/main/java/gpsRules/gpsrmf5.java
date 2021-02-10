/*
 * gpsrmf5.java
 *
 * Created on November 8, 2007, 2:51 PM
 */

package gpsRules;

import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 * Modified 7/20/2009 by DES to support parmStatus = Active
 * Modified 10/24/07 by DES to use the GPSrules Class to create a session scoped rules object
 * that stores the rules values during execution of gpsrcf1 thru gpsrcf5 instead of 
 * storing the data in individual session variables.
 *
 */
public class gpsrmf5 extends HttpServlet {
            
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
        
        String dataType = "";
        String errMsg = "";
        String junk = "";
        GPSrules ruleSet;
        String status = "";
        String work = "";
        
        try {    
            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
            }

            session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
            ruleSet = (GPSrules) session.getAttribute("sRuleSet");
            status = request.getParameter("status");
            
            /* Process Logical data types here */
        
            junk = (String) request.getParameter("3L");
            if (junk != null && junk.equals("Continue")) {
                dataType = ruleSet.getDataType();
                if (dataType == null || !dataType.equals("L") ) {         
                    request.setAttribute("message", "Module " + SERVLET_NAME + " detected an invalid Data Type code.");
                    view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    return;
                }
                ruleSet.setDefaultValueRaw(request.getParameter("defaultValue"));
                ruleSet.setSearchLogicalDefault(request.getParameter("searchLogicalDefault"));      
            }
    
            /* Process Numeric data types here */
            
            junk = request.getParameter("3N");
            if (junk != null && junk.equals("Continue")) {
                dataType = ruleSet.getDataType();
                if (dataType == null || !dataType.equals("N") ) {         
                    request.setAttribute("message", "Module " + SERVLET_NAME + " detected an invalid Data Type code.");
                    view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    return;
                }      
                ruleSet.setAllowDuplicates(request.getParameter("allowDuplicates").equals("Y"));
                ruleSet.setAllowFractions(request.getParameter("allowFractions").equals("Y"));
                ruleSet.setAllowSign(request.getParameter("allowSign").equals("Y"));
                ruleSet.setAllowTilde(request.getParameter("allowTilde").equals("Y"));
                ruleSet.setAllowZero(request.getParameter("allowZero").equals("Y"));
                ruleSet.setDefaultValueRaw(request.getParameter("defaultValueRaw"));  // Raw Value
                ruleSet.setDefaultValueCooked(request.getParameter("defaultValueCooked"));  // Cooked Value
                //ruleSet.setDeMultipliers(request.getParameter("deMultipliers"));
                ruleSet.setDeMultipliers(request.getParameter("txtDEMultipliers")); // String of values
                ruleSet.setDeObject(request.getParameter("deObject"));
                ruleSet.setDeSelectBoxName(request.getParameter("deSelectBoxName"));
                ruleSet.setDeTextBoxSize(Integer.parseInt(request.getParameter("deTextBoxSize")));
                //ruleSet.setDisplayMultipliers(request.getParameter("displayMultipliers"));
                ruleSet.setDisplayMultipliers(request.getParameter("txtDisplayMultipliers")); // String of values
                ruleSet.setDisplayUnits(request.getParameter("units"));
                ruleSet.setMaxValueRaw(request.getParameter("maxValueRaw")); // Raw format
                ruleSet.setMaxValueCooked(request.getParameter("maxValueCooked")); // Cooked format
                ruleSet.setMinDecimalDigits(Integer.parseInt(request.getParameter("minDecimalDigits")));
                ruleSet.setMinValueRaw(request.getParameter("minValueRaw")); // Raw format
                ruleSet.setMinValueCooked(request.getParameter("minValueCooked")); // Cooked format
                ruleSet.setParmDelimiter(request.getParameter("parmDelimiter"));
                ruleSet.setQobject(request.getParameter("qObject"));
                ruleSet.setQselectBoxName(request.getParameter("qSelectBoxName"));
                ruleSet.setQtextBoxSize(Integer.parseInt(request.getParameter("qTextBoxSize")));
                ruleSet.setSearchMax(Integer.parseInt(request.getParameter("searchMax")));
                ruleSet.setSearchMin(Integer.parseInt(request.getParameter("searchMin")));
                ruleSet.setSearchWeight(Integer.parseInt(request.getParameter("searchWeight")));
                ruleSet.setDecShift(Integer.parseInt(request.getParameter("txtUnitsShift")));
            }
            
            /* Process String data types here */
            
            junk = request.getParameter("3S");
            if (junk != null && junk.equals("Continue")) {
                dataType = ruleSet.getDataType();
                if (dataType == null || !dataType.equals("S") ) {         
                    request.setAttribute("message", "Module " + SERVLET_NAME + " detected an invalid Data Type code.");
                    view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    return;
                } 
                ruleSet.setAllowTilde(request.getParameter("allowTilde").equals("Y"));
                //session.setAttribute("cbCharSet", request.getParameter("cbCharSet"));
                ruleSet.setDefaultValueRaw(request.getParameter("defaultValueRaw"));  // Raw Value
                ruleSet.setDeleteLS(request.getParameter("deleteLS").equals("Y"));
                ruleSet.setDeleteNPC(request.getParameter("deleteNPC").equals("Y"));
                ruleSet.setDeleteSP(request.getParameter("deleteSP").equals("Y"));
                ruleSet.setDeleteTS(request.getParameter("deleteTS").endsWith("Y"));
                ruleSet.setDeObject(request.getParameter("deObject"));
                ruleSet.setDeSelectBoxName(request.getParameter("deSelectBoxName"));
                ruleSet.setDeTextBoxSize(Integer.parseInt(request.getParameter("deTextBoxSize")));
                ruleSet.setForceLC(request.getParameter("forceCase").equals("L"));
                ruleSet.setForceUC(request.getParameter("forceCase").equals("U"));
                ruleSet.setImageType(request.getParameter("imageType"));
                ruleSet.setMaxLength(Integer.parseInt(request.getParameter("maxLength")));
                ruleSet.setMinLength(Integer.parseInt(request.getParameter("minLength")));
                ruleSet.setOtherCharSet(request.getParameter("otherCharSet"));
                ruleSet.setParmDelimiter(request.getParameter("parmDelimiter"));
                ruleSet.setQobject(request.getParameter("qObject"));
                ruleSet.setQselectBoxName(request.getParameter("qSelectBoxName"));
                ruleSet.setQtextBoxSize(Integer.parseInt(request.getParameter("qTextBoxSize")));
                ruleSet.setReduceSP(request.getParameter("reduceSP").equals("Y"));
                ruleSet.setRegExpr(request.getParameter("regExpr").equals("Y"));
                ruleSet.setCharSet(request.getParameter("txtCharsAllowed"));
       
                // collect char set codes into 1 string
                String cbValues[] = request.getParameterValues("cbCharSet");
                String cbValue = "";
                if (cbValues != null) {
                    for (int i = 0; i < cbValues.length; i++) {
                        cbValue += cbValues[i];
                    }
                }
                ruleSet.setCharSetGroups(cbValue);
                
            }
            request.setAttribute("status", status);
            view = request.getRequestDispatcher("gpsrmf5.jsp");
            view.forward(request,response); 
        } catch (Exception e){
            request.setAttribute("message", "An error occurred getting display units or select box Names in " 
                    + SERVLET_NAME + "<br />" + e);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            e.printStackTrace();
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
