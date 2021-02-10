/*
 * gpsruf5.java
 *
 * Created on November 30, 2007, 4:59 PM
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
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * Rule to copy has been reviewed; save data in class and see if user wants
 * to make any more changes
 *
 */
public class gpsruf5 extends HttpServlet {
    
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsruf5.java";
    private final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
               
        String dataType = "";
        String errMsg = "";
        String junk = "";
        GPSrules ruleSet;
        
        try {    
            String yadda = request.getParameter("validation");
            if (!yadda.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
            }
     
            HttpSession session = request.getSession();
            String gDebug = (String) session.getAttribute("debug");
            if (gDebug != null) {
                debugSw = gDebug.equals("Y");
            }
            debugSw = true;
            if (session.isNew()) {
                response.sendRedirect ("gpstimeout.htm");
            }
            
            session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
            ruleSet = (GPSrules) session.getAttribute("sRuleSet");

            /* Process Date data types here 
            junk = (String) request.getParameter("3D");
            if (junk != null && junk.equals("Continue")) {
                session.setAttribute("dateFormat", request.getParameter("dateFormat"));
                session.setAttribute("defaultValue", request.getParameter("defaultValue"));
                session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
                session.setAttribute("maxDate", request.getParameter("maxDate"));
                session.setAttribute("maxTime", request.getParameter("maxTime"));
                session.setAttribute("minDate", request.getParameter("minDate"));
                session.setAttribute("minTime", request.getParameter("minTime"));
                session.setAttribute("searchMax", request.getParameter("searchMax"));
                session.setAttribute("searchMin", request.getParameter("searchMin"));
                session.setAttribute("searchWeight", request.getParameter("searchWeight"));
                session.setAttribute("timeFormat", request.getParameter("timeFormat"));
            }
            */
            
            /* Process Logical data types here */
        
            junk = (String) request.getParameter("3L");
            if (junk != null && junk.equals("Continue")) {
                dataType = ruleSet.getDataType();
                if (dataType == null || !dataType.equals("L") ) {         
                    request.setAttribute("message", "Module " + SERVLET_NAME + " detected an invalid Data Type code.");
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
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
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
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
                debug ("gpsrmf5.java says DE select box name is '" + ruleSet.getDeSelectBoxName() + "'");
            }
            
            /* Process String data types here */
            
            junk = request.getParameter("3S");
            if (junk != null && junk.equals("Continue")) {
                dataType = ruleSet.getDataType();
                if (dataType == null || !dataType.equals("S") ) {         
                    request.setAttribute("message", "Module " + SERVLET_NAME + " detected an invalid Data Type code.");
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
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
        } catch (Exception e){
            errMsg = "An error occurred in " + SERVLET_NAME + "<br />" + e;
            e.printStackTrace();
        } finally {
            if (!errMsg.equals("")) {
                request.setAttribute("message", errMsg);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
            } else {
                RequestDispatcher view = request.getRequestDispatcher("gpsruf5.jsp");
                view.forward(request,response);  
            }
        }
    }

    private void debug (String x) {
        if (debugSw) {
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
