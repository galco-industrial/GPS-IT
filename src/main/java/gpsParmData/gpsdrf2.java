/*
 * gpsdrf2.java
 *
 * Created on October 17, 2007, 2:00 PM
 */

package gpsParmData;

import OEdatabase.WDSconnect;
import gps.util.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import sauter.util.*;

/**
 *
 * @author Sauter
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I receive up to 5 part numbers. I create a GPSparmSet object for each part number.
 * Then I look up the corresponding raw parametric data values.
 * Then I call GPSparmSet methods to calculate the cooked values.
 * Then I dispatch to gpsdrf2.jsp to display the cooked parametric data side by side in a
 * browser window.
 *
 */
public class gpsdrf2 extends HttpServlet {
                
    private boolean debugSw = false;
    private static final String SERVLET_NAME = "gpsdrf2.java";
    private static final String VERSION = "1.3.00";
           
     /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
  
        String close;
        String cooked;
        GPScvt cvt = null;
        String errorMessage = "";
        String familyCode = "";
        String familyName = "";
        GPSfieldSet fieldSet = null;
        List <String> generatedRows = new ArrayList <String>();
        int i;
        int j;
        GPSparmSet parmSet = null;
        GPSpart part = null;
        String partNum = "";
        String [] partNumbers = new String[5];
        String prevFamilyCode = "";
        String prevSubfamilyCode = "";
        String raw;
        int rc = 0;
        GPSrules rules[] = null;
        GPSselectBox selectBox = null;
        String selectBoxName = "";
        int seqNum;
        String subfamilyCode = "";
        GPSunit units = null;
                   
        HttpSession session = request.getSession(); // Get a handle on our session
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
        //debugSw = true;
        
	if (session.isNew()) {                      // check for timeout
           response.sendRedirect ("gpstimeout.htm");
           return;
        }
        
        // extract the part number(s) from the request object and put them in an array
        
        close = request.getParameter("close");
        partNumbers[0] = request.getParameter("partNumber1");
        partNumbers[1] = request.getParameter("partNumber2");
        partNumbers[2] = request.getParameter("partNumber3");
        partNumbers[3] = request.getParameter("partNumber4");
        partNumbers[4] = request.getParameter("partNumber5");
                        
        WDSconnect conn = new WDSconnect();  // Connect to WDS database    
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        debug(SERVLET_NAME + " is connected to the Database");
                
        try {
            // process the array of part numbers and create a GPSparmSet object
            // for each one that I find:
            for (i = 0; i < 5; i++) {
                
                partNum = partNumbers[i];
                if (partNum != null && !partNum.equals("")) {
                    debug("Preparing to create Part Number object for PN " + partNum);
                    
                    // Read the PN and make sure it exists
        
                    part = new GPSpart();   // create a part number object
                    if (!part.read(conn, partNum)) {
                        errorMessage = "Module " + SERVLET_NAME + " failed to create partNum object for PN " + partNum;
                        debug (errorMessage);
                        request.setAttribute("message", errorMessage);
                        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        return;
                    }
                    
                    debug ("Part Number Object successfully created for PN " + partNum);
                    if (!part.getHasPSData()) {
                        errorMessage = "Unexpected Error - Has_PS_Data flag for " + partNum + " is false!";
                        debug (errorMessage);
                        request.setAttribute("message", errorMessage);
                        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        return;
                    }
                    
                    // Get the family and subfamily code for this Part Number
                    
                    familyCode = part.getFamilyCode().trim();
                    if (familyCode.length() == 0) {
                        errorMessage = "Unexpected Error - Family code for PN " + partNum + " is blank";
                        debug (errorMessage);
                        request.setAttribute("message", errorMessage);
                        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        return;
                    }
                    subfamilyCode = part.getSubfamilyCode().trim();
                    if (subfamilyCode.length() == 0) {
                        errorMessage = "Unexpected Error - Subfamily code for PN " + partNum + " is blank";
                        debug (errorMessage);
                        request.setAttribute("message", errorMessage);
                        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        return;
                    }
                    
                    // NOTE: Fix me later if I am doing a family level search where 
                    // multiple subfamilies might be involved!!!!!!!!!!!!!!!!!!!!!!!!!!1
                    
                    // Make sure family code has not changed
                    
                    if (prevFamilyCode.length() != 0 && !familyCode.equals(prevFamilyCode)) {
                        errorMessage = "Unexpected Error - Family code for this PN changed from '" 
                                + prevFamilyCode + "' to '" + familyCode + "'.";
                        debug (errorMessage);
                        request.setAttribute("message", errorMessage);
                        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        return;
                    }
                    
                    // Code below sets Family Name which the jsp expects
                    GPSfamilyCodes fcodes = new GPSfamilyCodes();
                    fcodes.open(conn);
                    familyName = fcodes.getFamilyName(familyCode);
                    session.setAttribute("sbFamilyName", familyName);
                    fcodes = null;
                    
                    // Create a fieldSet object and get field set rules in display order
            
                    if (!subfamilyCode.equals(prevSubfamilyCode)) {
                        prevSubfamilyCode = subfamilyCode;
                        fieldSet = new GPSfieldSet();
                        rules = fieldSet.getRules(conn, familyCode, subfamilyCode, GPSfieldSet.DISPLAY_ORDER);
                        errorMessage = "Fieldset object has been created for rules for family code '"
                                + familyCode + "' subfamily code '" + subfamilyCode;
                        debug (errorMessage);
                        
                        // For each parametric field in this fieldset:
                        //      look up any units data we need for numeric items
                        //      load any select boxes we need fo string and numeric items
                        
                        for (j = 0; j < fieldSet.count(); j++) {
                            seqNum = rules[j].getSeqNum();
                            
                            // Process Numeric fields here
                            debug ("Processing rules for field # " + seqNum + " Field name: " + rules[j].getParmName());
                            if ( rules[j].getDataType().equals("N") ) {
                                units = new GPSunit();
                                // Look up and set Display units info here
                                units.open(conn, rules[j].getDisplayUnits());
                                rules[j].setDecShift(units.getMultiplierExp());
                                units = null;
                            }
                            
                            // Strings and Numerics here
                            
                            if ( "SN".contains(rules[j].getDataType())) {    
                                
                                // If a DE select box is applicable
                                // try to load a select box object for it
                                debug ("Checking for a Select Box Name...");
                                selectBoxName = rules[j].getDeSelectBoxName();
                                if (rules[j].getDeTextBoxSize() == 0 
                                        && selectBoxName.length() != 0 ) {
                                    debug ("I am going to try to open Select Box named " + selectBoxName);
                                    selectBox = new GPSselectBox();  // create a new select box object
                                    rc = selectBox.open(conn, rules[j].getFamilyCode(), 
                                            rules[j].getSubfamilyCode(), selectBoxName);
                                    if (rc < 0) {
                                        errorMessage = "Unexpected error " + rc 
                                                + "opening Select Box " + selectBoxName;
                                        debug (errorMessage);   
                                        request.setAttribute("message", errorMessage);
                                        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                                        view.forward(request,response);
                                        return;
                                    }
                                    rules[j].setDeSelectBox(selectBox);
                                    debug ("Created and stored the select box object.");
                                } else {
                                    rules[j].setDeSelectBox(null);
                                    debug ("No Select Box for this field.");
                                }
                            }
                        }   // END for j
                    }  //END if (!subfamilyCode.equals(prevSubfamilyCode)) {
                    
                    // Fieldset is now loaded and initialized with rules and decimal shift values
                    // and select box options

                    // Now Create GPSparmSet object and get the parametric data for this PN
                    
                    generatedRows.add("j = 0;");
                    if ( i == 0 ) {
                        generatedRows.add("cell [j++] = new Array(" 
                            + "\"Part Number\",\""
                            + EditText.encodeEntity(partNum)
                            + "\",\"\",\"\",\"\",\"\");");
                        generatedRows.add("cell [j++] = new Array(" 
                            + "\"Manufacturer\",\""
                            + EditText.encodeEntity(part.getSalesSubcat())
                            + "\",\"\",\"\",\"\",\"\");");
                        generatedRows.add("cell [j++] = new Array(" 
                            + "\"Description\",\""
                            + EditText.encodeEntity(part.getDescription())
                            + "\",\"\",\"\",\"\",\"\");");
                        generatedRows.add("cell [j++] = new Array(" 
                            + "\"Subfamily\",\""
                            + EditText.encodeEntity(subfamilyCode)
                            + "\",\"\",\"\",\"\",\"\");");
                        generatedRows.add("cell [j++] = new Array(" 
                            + "\"Dimensions\",\""
                            + EditText.encodeEntity(part.getDimensionsHWD())
                            + "\",\"\",\"\",\"\",\"\");");
                        generatedRows.add("cell [j++] = new Array(" 
                            + "\"In Stock\",\""
                            + EditText.encodeEntity(Long.toString(part.getAvailable(conn, partNum)))
                            + "\",\"\",\"\",\"\",\"\");");
                    } else {
                        generatedRows.add("cell [j++] ["
                            + (i+1) 
                            + "] = \""
                            + EditText.encodeEntity(partNum)
                            + "\";");
                        generatedRows.add("cell [j++] ["
                            + (i+1) 
                            + "] = \""
                            + EditText.encodeEntity(part.getSalesSubcat())
                            + "\";");
                        generatedRows.add("cell [j++] ["
                            + (i+1) 
                            + "] = \""
                            + EditText.encodeEntity(part.getDescription())
                            + "\";");
                        generatedRows.add("cell [j++] ["
                            + (i+1) 
                            + "] = \""
                            + EditText.encodeEntity(subfamilyCode)
                            + "\";");
                        generatedRows.add("cell [j++] ["
                            + (i+1) 
                            + "] = \""
                            + EditText.encodeEntity(part.getDimensionsHWD())
                            + "\";");
                        generatedRows.add("cell [j++] ["
                            + (i+1) 
                            + "] = \""
                            + EditText.encodeEntity(Long.toString(part.getAvailable(conn, partNum)))
                            + "\";");
                    }
                    parmSet = new GPSparmSet();
                    if (!parmSet.read(conn, partNum)) {
                        errorMessage = "Unexpected Error while reading parametric values for Part Number "
                                + partNum;
                        debug (errorMessage);
                        request.setAttribute("message", errorMessage);
                        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        return;
                    }
            
                    debug ("Raw parametric data has been read for PN " + partNum);
                    debug ("Now converting raw values to cooked.");
                    
                    cvt = new GPScvt();  // We need this class to cook raw values
                    
                    // plow through the parametric fields and convert cooked to raw values
                    
                    for (j = 0; j < fieldSet.count(); j++) {
                        seqNum = rules[j].getSeqNum();
                        raw = parmSet.getParmValue(seqNum); // this is the raw value
                        cooked = raw; // default cooked is raw
                        rules[j].setRawValue(raw);
                        // If a DE select box is applicable
                        // try to do a select box look up on the raw value
                        selectBox = rules[j].getDeSelectBox();
                        if (selectBox != null) { 
                            // Do this if there is a select box for this field
                            // Let's attempt a look up and replace the raw with the select box entry
                            debug ("Select Box found for field # " + seqNum);
                            
                            rc = selectBox.optionValue1IndexOf(raw);
                            debug ("SB Lookup Return code was " + rc);
                            if (rc > -1) {
                                cooked = selectBox.getOptionText(rc);
                            }
                        } else if ( rules[j].getDataType().equals("N") ) {
                            cooked = cvt.toCooked(raw, rules[j].getDisplayMultipliers(),
                                rules[j].getParmDelimiter(), rules[j].getDecShift(), 
                                rules[j].getAllowDuplicates(), rules[j].getAllowTilde(), true);
                        }
                        debug ("Cooked value = '" + cooked + "'");
                        rules[j].setCookedValue(cooked);
                        if (rules[j].getDataType().equals("N")) {
                            if (cooked.length() != 0) {
                                cooked += " " + rules[j].getDisplayUnits();
                            } else {
                                cooked = "&nbsp;";
                            }
                        }
                        if ( i == 0 ) {
                            generatedRows.add("cell [j++] = new Array(" 
                                + "\"" + EditText.encodeEntity(rules[j].getParmName()) 
                                + "\",\""
                                + EditText.encodeEntity(cooked)
                                + "\",\"\",\"\",\"\",\"\");");
                        } else {
                            generatedRows.add("cell [j++] ["
                                + (i+1) 
                                + "] = \""
                                + EditText.encodeEntity(cooked)
                                + "\";");                            
                        }
                    }
                } // END if (partNum != null && !partNum.equals(""))   
                
            } // END for (i = 0; i < 5; i++) 
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setAttribute("close", close);
        request.setAttribute("generatedRows", generatedRows);
        RequestDispatcher view = request.getRequestDispatcher("gpsdrf2.jsp");
        view.forward(request,response); 
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
