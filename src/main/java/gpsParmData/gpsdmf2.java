/*
 * gpsdmf2.java
 *
 * Created on March 18, 2008, 5:07 PM
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
import sauter.util.*;

/**
 *
 * @author Sauter
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I look up the ruleSet for the parametric fields for the family/subfamily
 * that this part number belongs to. Then I look up the parametric data and stick
 * the corresponding data raw and cooked values inside the placeholders in the ruleset
 * Then I build an arraylist that contains the values the JSP needs to build the form.
 * I pass the arraylist, part number, etc to the JSP to allow the user to update the data.
 *
 * Modified 02/26/2010 by DES to escape quotes embedded inside a tool tip.
 * Modified 10/05/2016 by DES to fix missing ";" in statement
 *                            work = "    aPreviousValue[f2++] = \"" + work + "\";";
 *
 */
public class gpsdmf2 extends HttpServlet {
    
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsdmf2.java";
    private final String VERSION = "1.3.02";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
      
        String auditUserID = "";
        String dataType = "";
        String displayUnits;
        String enableToolTips = "";
        int iwork;
        String message = "";
        String familyCode = "";
        String familyName = "";
        //String familyDescription = "";
        String fieldName = "";
        int fieldNum = 0;
        String partNum = "";
        String productLineCode = "";
        String productLineName = "";
        String ruleSubfamilyCode = "";
        String subfamilyCode = "";
        String subfamilyName = "";
        //String subfamilyDescription = "";
        String work = "";
        String work2 = "";
        String work3 = "";       

        GPSrules fieldRules[];       
        GPSfieldSet fieldSet = null;
        GPSpart partRecord = null;
        GPSparmSet parmSet = null;
        //ResultSet rs = null;
        
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
            debugSw = true;

            //  Get Initial set up and save in Session variables if we got xtrol from gpsdmf1.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Continue")) {
                auditUserID = request.getParameter("auditUserID");
                session.setAttribute("auditUserID", auditUserID);
                enableToolTips = request.getParameter("enableToolTips");
                session.setAttribute("enableToolTips", enableToolTips);
                partNum = request.getParameter("partNum");
            }
        
        } catch (Exception e){
            request.setAttribute("message", "An error occurred in " + SERVLET_NAME + ": <br />" 
                    + e);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        if (partNum == null || partNum.equals("")) {
            //Abort if missing or invalid part number
            message = "Error! Missing or invalid Part Number.";
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsdmf1.jsp");
            view.forward(request,response);
            return;
        }
        
    
        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        // create Part Record and look up part information
        
        partRecord = new GPSpart();
        
        try {
            if (!partRecord.read(conn, partNum)) {
                //Abort if error reading part number
                message = "Error! Cannot find Part Number " + partNum;
                request.setAttribute("statusMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("gpsdmf1.jsp");
                view.forward(request,response);
                return;
            }
            
            // Get family code, subfamily code
            // make sure codes are valid and it has ps data
            
            familyCode = partRecord.getFamilyCode();
            if (familyCode == null || familyCode.equals("")) {
                //Abort if bad family code
                message = "Error! Invalid family code found for Part Number " + partNum;
                request.setAttribute("statusMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("gpsdmf1.jsp");
                view.forward(request,response);
                return;
            }
            familyName = GPSfamilyCodes.lookUpFamilyName(conn, familyCode);
            productLineCode = GPSfamilyCodes.lookUpFamilyProductLineCode(conn, familyCode);
            productLineName = GPSproductLines.lookUpProductLineName(conn, productLineCode);
            
            subfamilyCode = partRecord.getSubfamilyCode();
            if (subfamilyCode == null || subfamilyCode.equals("")) {
                //Abort if bad subfamily code
                message = "Error! Invalid subfamily code found for Part Number " + partNum;
                request.setAttribute("statusMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("gpsdmf1.jsp");
                view.forward(request,response);
                return;
            }
            subfamilyName = GPSsubfamilyCodes.lookUpSubfamilyName(conn, familyCode, subfamilyCode);
            
            if (!partRecord.getHasPSData()) {
                //Abort if no PS Data
                message = "Error! No Parametric data was found for Part Number " + partNum;
                request.setAttribute("statusMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("gpsdmf1.jsp");
                view.forward(request,response);
                return;
            }
            
            
            // NEXT we will get a ruleset for this family subfamily
            
            List <String> seqNumMap = new ArrayList <String> ();
            List <String> previousValue = new ArrayList <String> ();
            List <String> generatedScript = new ArrayList <String> ();

            fieldNum = 0;
            // Here is where we generate the custom JavaScript that builds the DHTML
            // for the data entry form objects
            fieldSet = new GPSfieldSet();
            fieldRules = fieldSet.getRules(conn, familyCode, subfamilyCode, GPSfieldSet.DATA_ENTRY_ORDER);
            
            // then we will get the PS data and stick the raw and cooked values in the ruleset here
            
            parmSet = new GPSparmSet();
            if (!parmSet.read(conn, partNum)) {
                //Abort if no PS Data
                message = "Error! No Parametric data could be read for Part Number " + partNum;
                request.setAttribute("statusMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("gpsdmf1.jsp");
                view.forward(request,response);
                return;
            }
                        
            // then we will build the Arraylist for the UPDATE JSP to build a form with
            
            for (fieldNum = 0; fieldNum < fieldSet.size(); fieldNum++) {
                dataType = fieldRules[fieldNum].getDataType();
            
                // Abort on invalid data Type
            
                if ("NSL".indexOf(dataType) == -1) {
                    message = "Permanent Error! Module " + SERVLET_NAME 
                        + " found invalid data type code: " + dataType;
                    debug (message);
                    request.setAttribute("message", message);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    conn.close();
                    return;
                }
                work = "    // Field " + fieldNum;
                generatedScript.add(work);
                iwork = fieldRules[fieldNum].getSeqNum();
                work = "    // SeqNum " + iwork;
                generatedScript.add(work);
                fieldName = fieldRules[fieldNum].getParmName();
                work = parmSet.getParmValueRaw(iwork);
                if (work == null) {
                    message = "Permanent Error! Module " + SERVLET_NAME
                        + " found null parametric value for field Sequence Number " + iwork
                            + ": " + fieldName;
                    debug (message);
                    request.setAttribute("message", message);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    conn.close();
                    return;
                }
                if (work.contains("\"")) {
                    message = "Permanent Error! Module " + SERVLET_NAME
                        + " found parametric value containing quotation marks in field Sequence Number " + iwork
                            + ": " + fieldName;
                    debug (message);
                    request.setAttribute("message", message);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    conn.close();
                    return;
                }
                work = "    aPreviousValue[f2++] = \"" + work + "\";";
                debug ("Work contains : >>>" + work + "<<<");
                previousValue.add(work);        
                               
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
                            message = "Permanent Error! Module " + SERVLET_NAME 
                                + " found deTextBoxSize was zero and Select Box Name was blank.";
                            debug (message);
                            request.setAttribute("message", message);
                            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                            view.forward(request,response);
                            conn.close();
                            return;            
                        }
                        debug ("Processing DE Select box name: " + work);
                        GPSselectBox selectBox = new GPSselectBox();
                        // Make sure Select Box was found
                        ruleSubfamilyCode = fieldRules[fieldNum].getSubfamilyCode();
                        if (selectBox.open(conn, familyCode, ruleSubfamilyCode, work) < 0) {
                            message = "Permanent Error! " + SERVLET_NAME
                                    + " Could not find Select Box " + work 
                                    + " for Family/Subfamily " + familyCode + "/" + ruleSubfamilyCode;
                            debug (message);
                            conn.close();
                            request.setAttribute("message", message);
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
                    work = "    aCharSet[f] = \"" + escapeBackSlash(fieldRules[fieldNum].getOtherCharSet()) + "\";";
                    generatedScript.add(work);
                    work = "    aDecShift[f] = \"\";";
                    generatedScript.add(work);
                    work = "    aDefault[f] = \"" + escapeBackSlash(fieldRules[fieldNum].getDefaultValueRaw()) + "\";";
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
                            message = "Permanent Error! Module " + SERVLET_NAME 
                                + " found deTextBoxSize was zero and Select Box Name was blank.";
                            debug (message);
                            request.setAttribute("message", message);
                            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                            view.forward(request,response);
                            conn.close();
                            return;              
                        }
                        debug ("DE Select box name is " + work);
                        GPSselectBox selectBox = new GPSselectBox();
                        // Make sure Select Box was found
                        ruleSubfamilyCode = fieldRules[fieldNum].getSubfamilyCode();
                        if (selectBox.open(conn, familyCode, ruleSubfamilyCode, work) < 0) {
                            message = "Permanent Error! " + SERVLET_NAME
                                    + " Could not find Select Box " + work 
                                    + " for Family/Subfamily " + familyCode + "/" + ruleSubfamilyCode;
                            debug (message);
                            conn.close();
                            request.setAttribute("message", message);
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
                    work = "    aToolTip[f] = \"" + EditText.escapeQuote(fieldRules[fieldNum].getDeToolTip()) + "\";";
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
        
            conn.close();
            if (fieldNum == 0) {
                message = "Module " + SERVLET_NAME 
                    + " could not find any RuleSets for this Family/Subfamily code.";
                debug (message);
                request.setAttribute("message", message);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
                        
            // then we will call the UPDATE JSP
            
            session.setAttribute("generatedScript", generatedScript);
            session.setAttribute("seqNumMap", seqNumMap);
            session.setAttribute("previousValue", previousValue);
            request.setAttribute("statusMessage", "");
            request.setAttribute("partNum", partNum);
            request.setAttribute("productLineName", productLineName);
            request.setAttribute("familyName", familyName);
            request.setAttribute("subfamilyName", subfamilyName);
            RequestDispatcher view = request.getRequestDispatcher("gpsdmf2.jsp");
            view.forward(request,response); 
            return;
                      
        } catch (Exception e) {
            // Oops a boo boo
            e.printStackTrace();
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed with permanent errors.");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            conn.close();
            return;
            
        }
     
    }
        
    private void debug (String x) {
        if (debugSw) {
            System.out.println(x);
        }
    }
    
    private String escapeBackSlash (String text) {
        return text.replace("\\", "\\\\");
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
