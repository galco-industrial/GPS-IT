/*
 * gpsdaf2.java
 *
 * Created on September 5, 2006, 5:29 PM
 */

package gpsParmData;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;
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
 * modified 09/10/2007 by DES to use Ajax techniques to look up family subfamily codes
 * and remember previous selections for repeat operations.
 *
 */
public class gpsdaf2 extends HttpServlet {
    
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsdaf2.java";
    private final String VERSION = "1.3.00";
          
     /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        Date now = new Date();
        DateFormat dfShort = DateFormat.getDateInstance(DateFormat.SHORT);
        DateFormat tfShort = DateFormat.getTimeInstance();
        String traxDate = dfShort.format(now);
        String traxTime = tfShort.format(now);
        String displayUnits;
        int fieldNum = 0;
        String errMsg = "";
        String auditUserID = "";
        String familyCode = "";
        String subfamilyCode = "";
        String enableToolTips = "";
        //String familyDescription = "";
        //String subfamilyDescription = "";
        String ruleSubfamilyCode = "";
        ResultSet rs = null;
        String work = "";
        String work2 = "";
        String work3 = "";
        String dataType = "";
        GPSfieldSet fieldSet = null;
        //GPSrules deRules;
        GPSrules fieldRules[];
        
        /* Get a handle on our session */
        HttpSession session = request.getSession();
        
        try {    
            String yadda = request.getParameter("validation");
            if (!yadda.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }        
            if (session.isNew()) {
                response.sendRedirect ("gpstimeout.htm");
                return;
            }
            String gDebug = (String) session.getAttribute("debug");
            if (gDebug != null) {
                debugSw = gDebug.equals("Y");
            }
            //debugSw = true;


            //  Get Initial set up and save in Session variables if we got xtrol from gpsdaf1.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Continue")) {
                session.setAttribute("auditUserID", request.getParameter("auditUserID"));
                session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
                session.setAttribute("productLineCode", request.getParameter("productLine"));
                session.setAttribute("productLineName", request.getParameter("productLineName"));
                familyCode = request.getParameter("familyCode");
                session.setAttribute("familyCode", familyCode);
                session.setAttribute("familyName", request.getParameter("familyName"));
                subfamilyCode = request.getParameter("subfamilyCode");
                session.setAttribute("subfamilyCode", subfamilyCode);
                session.setAttribute("subfamilyName", request.getParameter("subfamilyName"));
                session.setAttribute("traxDate", traxDate + " " + traxTime);
            }
        
        } catch (Exception e){
            request.setAttribute("message", "An error occurred in " + SERVLET_NAME + ": <br />" 
                    + e);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        // Next we extract all the relevant fields to build a data entry screen

        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        // Build queries to extract Rules for this Family and Subfamily from the rules database

        try {
            List <String> seqNumMap = new ArrayList <String> ();
            List <String> previousValue = new ArrayList <String> ();
            List <String> generatedScript = new ArrayList <String> ();

            fieldNum = 0;
            // Here is where we generate the custom JavaScript that builds the DHTML
            // for the data entry form objects
            fieldSet = new GPSfieldSet();
            fieldRules = fieldSet.getRules(conn, familyCode, subfamilyCode, GPSfieldSet.DATA_ENTRY_ORDER);
            for (fieldNum = 0; fieldNum < fieldSet.size(); fieldNum++) {
                dataType = fieldRules[fieldNum].getDataType();
            
                // Abort on invalid data Type
            
                if ("NSL".indexOf(dataType) == -1) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps4001");
                    conn.close();
                    return;
                }
                work = "    // Field " + fieldNum;
                generatedScript.add(work);
                        
                /*************************************
                *          Numeric Fields            *
                *************************************/
           
                if (dataType.equals("N")) {
                    work = "    aCharSet[f] = \"\";";
                    generatedScript.add(work);
                    displayUnits = fieldRules[fieldNum].getDisplayUnits();
                    work = "    aDecShift[f] = \"" 
                            + Integer.toString(fieldRules[fieldNum].getDecShift()) + "\";";
                    generatedScript.add(work);
                    work = "    aDefault[f] = \"" + fieldRules[fieldNum].getDefaultValueRaw() + "\";"; 
                    generatedScript.add(work);
                    work = "    aDelim[f] = \"" + fieldRules[fieldNum].getParmDelimiter() + "\";";   
                    generatedScript.add(work);
                    work = "    aDEMultipliers[f] = \"" + fieldRules[fieldNum].getDeMultipliers() + "\";"; 
                    generatedScript.add(work);
                    work = "    aDERequired[f] = \"" + (fieldRules[fieldNum].getDeRequired() ? "Y" : "N") + "\";";
                    generatedScript.add(work);
                    work = "    aDETextBoxSize[f] = \"" + Integer.toString(fieldRules[fieldNum].getDeTextBoxSize()) + "\";";
                    generatedScript.add(work);
                    work2 = "n";
                    if (fieldRules[fieldNum].getAllowDuplicates() ) { work2 += "D"; }
                    if (fieldRules[fieldNum].getAllowFractions() ) { work2 += "F"; }
                    if (fieldRules[fieldNum].getAllowSign() ) { work2 += "S"; }
                    if (fieldRules[fieldNum].getAllowTilde() ) { work2 += "T"; }
                    if (fieldRules[fieldNum].getAllowZero() ) { work2 += "Z"; }
                    work = "    aFlags[f] = \"" + work2 + "\";";
                    generatedScript.add(work);
                    work = "    aLabel[f] = \"" + fieldRules[fieldNum].getParmName() + "\";";
                    generatedScript.add(work);
                    work = "    aMax[f] = \"" + fieldRules[fieldNum].getMaxValueRaw() + "\";";
                    generatedScript.add(work);
                    work = "    aMin[f] = \"" + fieldRules[fieldNum].getMinValueRaw() + "\";";
                    generatedScript.add(work);
                    work = "    aToolTip[f] = \"" + fieldRules[fieldNum].getDeToolTip() + "\";";
                    generatedScript.add(work);
                    work = "    aUnits[f] = \"" + displayUnits + "\";";
                    generatedScript.add(work);
                
                    // if DETextBoxSize is zero we generate a select box here 
                    // from ps_listbox table here:
                
                    if (fieldRules[fieldNum].getDeTextBoxSize() == 0) {
                        work = fieldRules[fieldNum].getDeSelectBoxName();
                        if (work.length() == 0) {
                            response.sendRedirect ("gpsabend.jsp?rc=gps4002");
                            conn.close();
                            return;              
                        }
                        debug ("DE Select box name is " + work);
                        GPSselectBox selectBox = new GPSselectBox();
                        // Make sure Select Box was found
                        ruleSubfamilyCode = fieldRules[fieldNum].getSubfamilyCode();
                        if (selectBox.open(conn, familyCode, ruleSubfamilyCode, work) < 0) {
                            errMsg = "Cannot find Select Box " + work + " for Family/Subfamily " + familyCode + "/" + subfamilyCode;
                            debug (errMsg);
                            conn.close();
                            request.setAttribute("message", errMsg);
                            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                            view.forward(request,response);
                            return;
                        }
                        ArrayList oList = selectBox.getOptionList();
                        for (int i = 0; i < oList.size() ;  i++) {
                            work = (String) oList.get(i);
                            generatedScript.add(work);
                        }
                        selectBox = null;                     
                    }
                    // we be done with a numeric field
                }
                                    
                /*************************************
                *          String Fields             *
                *************************************/
           
                if (dataType.equals("S")) {
                    work = "    aCharSet[f] = \"" + fieldRules[fieldNum].getOtherCharSet() + "\";";
                    generatedScript.add(work);
                    work = "    aDecShift[f] = \"\";";
                    generatedScript.add(work);
                    work = "    aDefault[f] = \"" + fieldRules[fieldNum].getDefaultValueRaw() + "\";";
                    generatedScript.add(work);
                    work = "    aDelim[f] = \"" + fieldRules[fieldNum].getParmDelimiter() + "\";";
                    generatedScript.add(work);
                    work = "    aDEMultipliers[f] = \"\";";
                    generatedScript.add(work);
                    work = "    aDERequired[f] = \"" + (fieldRules[fieldNum].getDeRequired() ? "Y" : "N") + "\";";
                    generatedScript.add(work);
                    work = "    aDETextBoxSize[f] = \"" + Integer.toString(fieldRules[fieldNum].getDeTextBoxSize()) + "\";";
                    generatedScript.add(work);
                    work2 = "s";
                    if (fieldRules[fieldNum].getDeleteNPC() ) { work2 += "0"; }
                    if (fieldRules[fieldNum].getDeleteSP() ) { work2 += "1"; }
                    if (fieldRules[fieldNum].getDeleteLS() ) { work2 += "2"; }
                    if (fieldRules[fieldNum].getDeleteTS() ) { work2 += "3"; }
                    if (fieldRules[fieldNum].getReduceSP() ) { work2 += "4"; }
                    if (fieldRules[fieldNum].getForceUC() ) { work2 += "5"; }
                    if (fieldRules[fieldNum].getForceLC() ) { work2 += "6"; }
                    if (fieldRules[fieldNum].getAllowDuplicates() ) { work2 += "D"; }
                    if (fieldRules[fieldNum].getAllowDuplicates() ) { work2 += "T"; }
                    if (fieldRules[fieldNum].getRegExpr() ) { work2 += "R"; }
                    work2 += fieldRules[fieldNum].getCharSetGroups();
                    work = "    aFlags[f] = \"" + work2 + "\";";
                    generatedScript.add(work);
                    work = "    aLabel[f] = \"" + fieldRules[fieldNum].getParmName() + "\";";
                    generatedScript.add(work);
                    work = "    aMax[f] = \"" + Integer.toString(fieldRules[fieldNum].getMaxLength()) + "\";";
                    generatedScript.add(work);
                    work = "    aMin[f] = \"" + Integer.toString(fieldRules[fieldNum].getMinLength()) + "\";";
                    generatedScript.add(work);
                    work = "    aToolTip[f] = \"" + fieldRules[fieldNum].getDescription() + "\";";
                    generatedScript.add(work);
                    work = "    aUnits[f] = \"\";";
                    generatedScript.add(work);
                
                    // if DETextBoxSize is zero we generate a select box here 
                    // from ps_listbox table here:
               
                    if (fieldRules[fieldNum].getDeTextBoxSize() == 0) {
                        work = fieldRules[fieldNum].getDeSelectBoxName();
                        if (work.length() == 0) {
                            response.sendRedirect ("gpsabend.jsp?rc=gps4003");
                            conn.close();
                            return;              
                        }
                        debug ("DE Select box name is " + work);
                        GPSselectBox selectBox = new GPSselectBox();
                        // Make sure Select Box was found
                        ruleSubfamilyCode = fieldRules[fieldNum].getSubfamilyCode();
                        if (selectBox.open(conn, familyCode, ruleSubfamilyCode, work) < 0) {
                            errMsg = "Cannot find Select Box " + work + " for Family/Subfamily " + familyCode + "/" + subfamilyCode;
                            debug (errMsg);
                            conn.close();
                            request.setAttribute("message", errMsg);
                            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                            view.forward(request,response);
                            return;
                        }
                        ArrayList oList = selectBox.getOptionList();
                        for (int i = 0; i < oList.size() ;  i++) {
                            work = (String) oList.get(i);
                            generatedScript.add(work);
                        }
                        selectBox = null;                     
                    }     
                }  // we be done with a string field    
            
                                    
                /*************************************
                *          Logical Fields            *
                *************************************/
           
                if (dataType.equals("L")) {
                    work = "    aCharSet[f] = \"\";";
                    generatedScript.add(work);
                    work = "    aDecShift[f] = \"\";";
                    generatedScript.add(work);
                    work2 = fieldRules[fieldNum].getDefaultValueRaw();
                    work = "";
                    if (work2.equals("N")) { work = "N"; }
                    if (work2.equals("Y")) { work = "Y"; }
                    work = "    aDefault[f] = \"" + work + "\";";
                    generatedScript.add(work);
                    work = "    aDelim[f] = \"\";";
                    generatedScript.add(work);
                    work = "    aDEMultipliers[f] = \"\";";
                    generatedScript.add(work);
                    work = "    aDERequired[f] = \"" + (fieldRules[fieldNum].getDeRequired() ? "Y" : "N") + "\";";
                    generatedScript.add(work);
                    work = "    aDETextBoxSize[f] = \"\";";
                    generatedScript.add(work);
                    work = "    aFlags[f] = \"l\";";
                    generatedScript.add(work);
                    work = "    aLabel[f] = \"" + fieldRules[fieldNum].getParmName() + "\";";
                    generatedScript.add(work);
                    work = "    aMax[f] = \"\";";
                    generatedScript.add(work);
                    work = "    aMin[f] = \"\";";
                    generatedScript.add(work);
                    work = "    aToolTip[f] = \"" + fieldRules[fieldNum].getDeToolTip() + "\";";
                    generatedScript.add(work);
                    work = "    aUnits[f] = \"\";";
                    generatedScript.add(work);
                
                    // End of Logical field javascript
                }    
            
                                    
                /*************************************
                *          Date Fields               *
                *************************************/
           
                if (dataType.equals("D")) {

                // This code needs to be fixed:
                //work = "    aCharSet[f] = \"\";";
                //generatedScript.add(work);
                //work = "    aDecShift[f] = \"\";";
                //generatedScript.add(work);
                //work2 = rs.getString("default_value");
                //work = "";
                //if (work2.equals("N")) { work = "N"; }
                //if (work2.equals("Y")) { work = "Y"; }
                //work = "    aDefault[f] = \"" + work + "\";";
                //generatedScript.add(work);
                //work = "    aDelim[f] = \"\";";
                //generatedScript.add(work);
                //work = "    aDEMultipliers[f] = \"\";";
                //generatedScript.add(work);
                //work = "    aDERequired[f] = \"" + (deRules.getDERequired() ? "Y" : "N") + "\";";
                //generatedScript.add(work);
                //work = "    aDETextBoxSize[f] = \"\";";
                //generatedScript.add(work);
                //work = "    aFlags[f] = \"l\";";
                //generatedScript.add(work);
                //work = "    aLabel[f] = \"" + deRules.getParmName() + "\";";
                //generatedScript.add(work);
                //work = "    aMax[f] = \"\";";
                //generatedScript.add(work);
                //work = "    aMin[f] = \"\";";
                //generatedScript.add(work);
                //work = "    aToolTip[f] = \"" + deRules.getDescription() + "\";";
                //generatedScript.add(work);
                //work = "    aUnits[f] = \"\";";
                //generatedScript.add(work);
                
                // End of Date field javascript
                }  
            
                work = "    f++;";
                generatedScript.add(work);
                seqNumMap.add(Integer.toString(fieldRules[fieldNum].getSeqNum()));
            }
        
            // close our Database Connection
        
            // rs.close();
            conn.close();
            if (fieldNum == 0) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " could not find any Rules for this Family/Subfamily code.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
        
            //request.setAttribute("generatedScript", generatedScript);
            session.setAttribute("generatedScript", generatedScript);
            session.setAttribute("seqNumMap", seqNumMap);
            session.setAttribute("previousValue", previousValue);
            session.setAttribute("previousPartNum", "");
            session.setAttribute("statusMessage", "");
        } catch (Exception e){
            request.setAttribute("message", "An error occurred in " + SERVLET_NAME + ": <br />" + e);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            conn.close();
            return;
        }
    
    // Now that we have the fields, we extract additional data to build the JavaScript code
    // that will build the DHTML inside the browser

        if (!errMsg.equals("")) {
            request.setAttribute("message", errMsg);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } else {
            RequestDispatcher view = request.getRequestDispatcher("gpsdaf2.jsp");
            view.forward(request,response);  
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
