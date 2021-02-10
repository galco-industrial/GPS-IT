/*
 * gpsrdf3.java
 *
 * Created on May 7, 2007, 2:36 PM
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
 *
 * I look up the selected global or local rule selected for deletion and I display it
 * first.
 *
 *
 *  Modification History
 *
 *
 * 5/18/2007 DES Fixed Char Set Groups Codes to display correctly
 * 5/30/2007 DES    Added "Data Entry Required Y/N" Rule
 * 07/07/2007 DES   Add support for Match Order/Preview Order/Series Implicit
 *
 */
public class gpsrdf3 extends HttpServlet {
            
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
        String familyCode = "";
        String familyName = "";
        String familyCodeString = "";
        int hour = 0;
        String message = "";
         int minute = 0;
        String partNum = "";
        String productLine = "";
        String productLineName = "";
        String queryString = "";
        int references = 0;
        String scopeString = "";
        int second = 0;
        int secondsSinceMidnight = 0;
        String subfamilyCode = "";
        String subfamilyCodeName = "";
        String subfamilyCodeString = "";
        ResultSet rs = null;
        ResultSet rs2 = null;
        ArrayList <String> ruleData;
        String ruleScope = "";
        GPSrules ruleSet = null;  // a convenient rules object to point to a fieldRules[] item
        GPSunit units = null;
        String seqNum = "";
        Statement statement;
        String work = "";
        String work2 = "";

        familyCode = request.getParameter("familyCode");
        familyName = ""; // request.getParameter("familyName");
        productLineName = request.getParameter("productLineName");
        ruleScope = request.getParameter("ruleScope");
        seqNum = request.getParameter("seqNum");
        subfamilyCode = request.getParameter("subfamilyCode");

        // Build query to extract existing Product Line codes 
        // and Family Codes from the database
    
        try {
            debug(debugLevel, 4, uStamp + " Looking up data for the selected rule set for family/subfamily...");
            queryString = "Select *";
            queryString += " FROM pub.ps_rules";
            queryString += " WHERE family_code = '" + familyCode +"' ";
            queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            queryString += " AND rule_scope = '" + ruleScope + "'";
            queryString += " AND seq_num = " + seqNum;
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    ruleSet = new GPSrules(); // create a brand new rules object for this field
                    if (!ruleSet.setProperties(rs) ) {
                        statement = rs.getStatement();
                        rs.close();
                        statement.close();
                        conn.close();
                        message = "Failed to read rules for this Family/Subfamily/Scope/SeqNum " + familyCode + " - " + subfamilyCode;
                        request.setAttribute("message", message);
                        view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        return;
                    }
                }
                statement = rs.getStatement();
                rs.close();
                statement.close();
            } else {
                conn.close();
                message = "Failed to read rules for this Family/Subfamily/Scope/SeqNum " + familyCode + " - " + subfamilyCode;
                request.setAttribute("message", message);
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }

            // save the rule set in a session variable here
            
            session.setAttribute("sessionRuleSet", ruleSet);
            
            // Now serve up the rules for the initial rules screen here

            ruleData = new ArrayList <String> ();
            ruleData.add( arrayString("Product Line", productLineName ) );
            ruleData.add( arrayString("Family Code", ruleSet.getFamilyCode() ) );
            work = ruleSet.getSubfamilyCode();
            if (work.equals("*")) {
                work = "* - All";
            }
            ruleData.add( arrayString("Subfamily Code", work ) );
            ruleData.add( arrayString("Field Number", Integer.toString(ruleSet.getSeqNum()) ) );
            work = ruleSet.getRuleScope();
            if (work.equals("G")) {
                work = "Global";
            }
            if (work.equals("L")) {
                work = "Local";
            }
            ruleData.add( arrayString("Rule Scope", work ) );
            dataType = ruleSet.getDataType();
            if (dataType.equals("N")) {
                work = "Numeric";
            }
            if (dataType.equals("S")) {
                work = "String";
            }
            if (dataType.equals("L")) {
                work = "Logical";
            }
            if (dataType.equals("D")) {
                work = "Date/Time";
            }
            ruleData.add( arrayString("Data Type", work) );
            ruleData.add( arrayString("Field Name", ruleSet.getParmName() ) );
            ruleData.add( arrayString("Description", ruleSet.getDescription() ) );
            ruleData.add( arrayString("Data Entry Tool Tip", ruleSet.getDeToolTip() ) );
            ruleData.add( arrayString("Search Tool Tip", ruleSet.getSearchToolTip() ) );
            work = ruleSet.getParmStatus();
            if (work.equals("A")) {
                work = "Active";
            } else {
                work = "Inactive";
            }
            ruleData.add( arrayString("Status", work) );
            ruleData.add( arrayString("Mandatory for Search", (ruleSet.getSearchRequired()? "Yes" : "No") ) );
            ruleData.add( arrayString("Required for Data Entry", (ruleSet.getDeRequired()? "Yes" : "No") ) ); // fixed 5/30/07
            ruleData.add( arrayString("Mfgr Series Implicit", (ruleSet.getSeriesImplicit()? "Yes" : "No") ) ); 
            ruleData.add( arrayString("Data Entry Order", Integer.toString(ruleSet.getDeOrder() ) ) );
            ruleData.add( arrayString("Display Order", Integer.toString(ruleSet.getDisplayOrder() ) ) );
            ruleData.add( arrayString("Search Order", Integer.toString(ruleSet.getSearchOrder() ) ) );
            ruleData.add( arrayString("Match Order", Integer.toString(ruleSet.getMatchOrder() ) ) );
            ruleData.add( arrayString("Preview Order", Integer.toString(ruleSet.getPreviewOrder() ) ) );
            work = ruleSet.getDisplayJust();
            if (work.equals("L")) {
                work = "Left";
            }
            if (work.equals("R")) {
                work = "Right";
            }
            if (work.equals("C")) {
                work = "Center";
            }
            ruleData.add( arrayString("Justification", work ) );
            if (dataType.equals("N") ) {
                work = ruleSet.getDeObject();
                if (work.equals("T")) {
                    work = "Text Box";
                }
                if (work.equals("S")) {
                    work = "Select Box";
                }
                ruleData.add( arrayString("Data Entry Object", work ) );
                ruleData.add( arrayString("DE Text Box Size", Integer.toString(ruleSet.getDeTextBoxSize() ) ) );
                ruleData.add( arrayString("DE Select Box Name", ruleSet.getDeSelectBoxName() ) );
                work = ruleSet.getQobject();
                if (work.equals("T")) {
                    work = "Text Box";
                }
                if (work.equals("S")) {
                    work = "Select Box";
                }
                ruleData.add( arrayString("Search Object", work ) );
                ruleData.add( arrayString("Search Text Box Size", Integer.toString(ruleSet.getQtextBoxSize() ) ) );
                ruleData.add( arrayString("Search Select Box Name", ruleSet.getQselectBoxName() ) );
                ruleData.add( arrayString("Display Units", ruleSet.getDisplayUnits() ) );
                work = ruleSet.getParmDelimiter();
                if (work.equals("")) {
                    work = "(none)";
                }
                ruleData.add( arrayString("Delimiter", work ) );
                ruleData.add( arrayString("Allow Duplicates", (ruleSet.getAllowDuplicates()? "Yes" : "No") ) );
                ruleData.add( arrayString("Allow Zero", (ruleSet.getAllowZero()? "Yes" : "No") ) );
                ruleData.add( arrayString("Allow Sign", (ruleSet.getAllowSign()? "Yes" : "No") ) );
                ruleData.add( arrayString("Allow Tilde", (ruleSet.getAllowTilde()? "Yes" : "No") ) );     
                ruleData.add( arrayString("Allow Fractions", (ruleSet.getAllowFractions()? "Yes" : "No") ) );
                ruleData.add( arrayString("Decimal Places", Integer.toString(ruleSet.getMaxDecimalDigits() ) ) ) ;
                ruleData.add( arrayString("Data Entry Multipliers", ruleSet.getDeMultipliers() ) );
                ruleData.add( arrayString("Display Multipliers", ruleSet.getDisplayMultipliers() ) );
                ruleData.add( arrayString("Minimum Value", ruleSet.getMinValueRaw() ) );
                ruleData.add( arrayString("Maximum Value", ruleSet.getMaxValueRaw() ) );
                work = ruleSet.getDefaultValueRaw();
                if (work.equals("")) {
                    work = "(none)";
                }
                ruleData.add( arrayString("Default Value", work ) );
                ruleData.add( arrayString("Search Min Pct", "-" + Integer.toString(ruleSet.getSearchMin()  ) + "%") );
                ruleData.add( arrayString("Search Max Pct", "+" + Integer.toString(ruleSet.getSearchMax()  ) + "%") );
                ruleData.add( arrayString("Search Rel. Weight", Integer.toString(ruleSet.getSearchWeight() ) ) );
            }
            if (dataType.equals("S") ) {
                work = ruleSet.getDeObject();
                if (work.equals("T")) {
                    work = "Text Box";
                }
                if (work.equals("S")) {
                    work = "Select Box";
                }
                ruleData.add( arrayString("Data Entry Object", work ) );
                ruleData.add( arrayString("DE Text Box Size", Integer.toString(ruleSet.getDeTextBoxSize() ) ) );
                ruleData.add( arrayString("DE Select Box Name", ruleSet.getDeSelectBoxName() ) );
                work = ruleSet.getQobject();
                if (work.equals("T")) {
                    work = "Text Box";
                }
                if (work.equals("S")) {
                    work = "Select Box";
                }
                ruleData.add( arrayString("Search Object", work ) );
                ruleData.add( arrayString("Search Text Box Size", Integer.toString(ruleSet.getQtextBoxSize() ) ) );
                ruleData.add( arrayString("Search Select Box Name", ruleSet.getQselectBoxName() ) );
                ruleData.add( arrayString("Image Type Code", ruleSet.getImageType() ) );
                ruleData.add( arrayString("Minimum Length", Integer.toString(ruleSet.getMinLength() ) ) ) ;
                ruleData.add( arrayString("Maximum Length", Integer.toString(ruleSet.getMaxLength() ) ) ) ;
                ruleData.add( arrayString("Delete Non-printing Chars", (ruleSet.getDeleteNPC()? "Yes" : "No") ) );                
                ruleData.add( arrayString("Delete All Spaces", (ruleSet.getDeleteSP()? "Yes" : "No") ) );                
                ruleData.add( arrayString("Delete Leading Spaces", (ruleSet.getDeleteLS()? "Yes" : "No") ) );                
                ruleData.add( arrayString("Delete Trailing Spaces", (ruleSet.getDeleteTS()? "Yes" : "No") ) );                
                ruleData.add( arrayString("Reduce Spaces", (ruleSet.getReduceSP()? "Yes" : "No") ) );                
                work = "No";
                if (ruleSet.getForceLC() ) {
                    work = "Lower Case";
                }
                if (ruleSet.getForceUC() ) {
                    work = "Upper Case";
                }
                ruleData.add( arrayString("Force Case", work) );                
                work = ruleSet.getParmDelimiter();
                if (work.equals("")) {
                    work = "(none)";
                }
                ruleData.add( arrayString("Delimiter", work ) );
                ruleData.add( arrayString("Allow Tilde", (ruleSet.getAllowTilde()? "Yes" : "No") ) );     
                ruleData.add( arrayString("Regular Expression", (ruleSet.getRegExpr()? "Yes" : "No") ) );
                ruleData.add( arrayString("Other Characters", ruleSet.getOtherCharSet() ) );
                work = ruleSet.getCharSetGroups();
                work2 = "";
                // Fix 5/29/2007 DES
                // changed comparands from 0 to -1
                if (work.indexOf("U") != -1) {
                    work2 += "UC ";
                }
                if (work.indexOf("L") != -1) {
                    work2 += "LC ";
                }
                if (work.indexOf("S") != -1) {
                    work2 += "SP ";
                }
                if (work.indexOf("N") != -1) {
                    work2 += "NU ";
                }
                if (work.indexOf("A") != -1) {
                    work2 += "AP ";
                }
                if (work.indexOf("Q") != -1) {
                    work2 += "QU ";
                }
                ruleData.add( arrayString("Char Set Groups", work2 ) );
                work = ruleSet.getDefaultValueRaw();
                if (work.equals("")) {
                    work = "(none)";
                }
                ruleData.add( arrayString("Default Value", work ) );
            }
            if (dataType.equals("L") ) {
                work = ruleSet.getDefaultValueRaw();
                if (work.equals("U")) {
                    work = "(none)";
                }
                ruleData.add( arrayString("Default Value", work ) );
                work = ruleSet.getSearchLogicalDefault();
                if (work.equals("U")) {
                    work = "(none)";
                }
                ruleData.add( arrayString("Search Logical Default", work ) );
            }
            if (dataType.equals("D") ) {
                work = ruleSet.getDefaultValueRaw();
                if (work.equals("")) {
                    work = "(none)";
                }
                ruleData.add( arrayString("Default Value", work ) );
            }
            ruleData.add( arrayString("Audit User ID", ruleSet.getAuditUserID() ) );
            ruleData.add( arrayString("Audit Date", ruleSet.getAuditDate() ) );
            secondsSinceMidnight = ruleSet.getAuditTimeRaw();
            hour = secondsSinceMidnight / 3600;
            minute = (secondsSinceMidnight - (hour*3600)) / 60;
            second = secondsSinceMidnight % 60;
            
            work = Integer.toString(hour) + ":" + Integer.toString(minute) + ":" + Integer.toString(second);
            ruleData.add( arrayString("Audit Time", work ) );

            debug(debugLevel, 4, uStamp + " Checking to make sure no parametric data exists for this parm field.");
            queryString = "SELECT part_num";
            queryString += " FROM pub.part";
            queryString += " WHERE family_code = '" + familyCode +"' ";
            queryString += " AND has_ps_data = '1' ";
            if (ruleScope.equals("L")) {
                queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            }
            debug (debugLevel, 4, uStamp + " Query string is " + queryString);
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    partNum = rs.getString("part_num");
                    queryString = "SELECT part_num, parm_value";
                    queryString += " FROM pub.ps_parm_data";
                    queryString += " WHERE part_num = '" + partNum + "'";
                    queryString += " AND seq_num = " + seqNum;
                    debug (debugLevel, 6, uStamp + " Query string is " + queryString);
                    rs2 = conn.runQuery(queryString);
                    if (rs2 != null) {
                        work = "";
                        if (rs2.next()) {
                            work = rs2.getString("parm_value");
                            if (work == null) {
                                work = "";
                            }
                        }
                        work = work.trim();
                        if (!work.equals("")) {
                            references++;
                        }
                        statement = rs2.getStatement();
                        rs2.close();
                        statement.close();
                    } else {
                        statement = rs.getStatement();
                        rs.close();
                        statement.close();
                        conn.close();
                        message = "Error reading parm data for this Family/Subfamily/Scope/SeqNum " + familyCode + " - " + subfamilyCode;
                        request.setAttribute("message", message);
                        view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        return;
                    }
                    if (references != 0) {
                        break;
                    }
                }
                statement = rs.getStatement();
                rs.close();
                statement.close();
            } else {
                conn.close();
                message = "Error while looking up part number references for this family: " + familyCode + " - " + subfamilyCode;
                request.setAttribute("message", message);
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
           
            request.setAttribute("ruleData", ruleData);
            request.setAttribute("familyCode", familyCode);
            request.setAttribute("familyName", familyName);
            request.setAttribute("subfamilyCode", subfamilyCode);
            request.setAttribute("productLineName", productLineName);
            request.setAttribute("ruleScope", ruleScope);
            request.setAttribute("seqNum", seqNum);
            request.setAttribute("references", references);
            
            view = request.getRequestDispatcher("gpsrdf3.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + " <br />" + e);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } finally {
            conn.close();
        }
    }
    
    private String arrayString(String label, String data) {
        if (label.equals("")) {
            label = "&nbsp;";
        }
        if (data == null) {
            data = "&nbsp;";
        }
        if (data.equals("")) {
            data = "&nbsp;";
        }
        return "\"" + label + "\",\"" + data + "\"";
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
