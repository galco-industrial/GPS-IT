/*
 * gpsoaf1.java
 *
 * Created on October 14, 2010, 3:35 PM
 */

package gpsSelectBoxOptions;

import OEdatabase.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import sauter.util.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * 
 * I walk through the image files associated with select boxes and check for irregularities
 * producing an audit report of exceptions to be investigated.
 *
 */
public class gpsoaf1 extends HttpServlet {
                
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
     
        // Connect to WWW database    
    
        WWWconnect conn3 = new WWWconnect();
        if (!conn3.connect()) {
            sWork = uStamp + " failed to connect to WWW database; " + conn3.getError();
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            conn.close();
            return;
        }
    
        String auditDate = DateTime.getDateYYMMDD(); 
        String auditTimeRaw = Integer.toString(DateTime.getSecondsSinceMidnight());
        String auditUserID = "";
        String[] baseContents = null;
        File baseDir = null;
        //String elementName = "";
        String errorMsg = "";
        String[] familyContents = null;
        File familyDir = null;
        File familyFile = null;
        String fExt = "";
        String fileName = "";
        String fsep = File.separator;
        String gpsExportPath = getServletContext().getInitParameter("exportPath");
        String hhmmss = DateTime.getTimeHHMMSS("");
        int i0 = 0;
        int i1 = 0;
        int i2 = 0;
        int j = 0;
        String imagePath = fsep + fsep + "webdev" + fsep + "webcontent" + fsep + "images" + fsep;
        String logItem = "";
        int logLevel = 3;
        String[] mCodes = null;
        File mfgrImage = null;
        String mfgrImageName = "";
        PrintWriter out = null;
        String outFileName = "";
        String printHeader = "";
        String sbImageBase = imagePath + "catalog" + fsep + "sb" + fsep;
        String selectBoxName = "";
        String[] selectBoxNames = null;
        String[] subfamilyContents = null;
        File subfamilyDir = null;
        File subfamilyFile = null;
        File workElement = null;
        
        
        try {
            auditUserID = request.getParameter("auditUserID");
            session.setAttribute("auditUserID", auditUserID); // Update session User ID
            
            // Build path & file name and Open the output file
        
            fileName = "imageaudit." + removeSlashes(auditDate) + "." + hhmmss;
            fExt = ".txt";
            outFileName = gpsExportPath + fileName + fExt;
            out = new PrintWriter( new BufferedWriter ( new FileWriter(outFileName))); 
            logItem = "Image Audit - Run Date/Time: " + auditDate + " " + hhmmss;
            out.println(logItem);
            logItem = "Output Log File name is :  " + outFileName;
            out.println(logItem);
            debug (debugLevel, 0, logItem); 
            logItem = "Using image directory:  " + sbImageBase;
            out.println(logItem);
            debug (debugLevel, 0, logItem); 
            logItem = "Logging level is " + logLevel;
            out.println(logItem);
            debug (debugLevel, 0, logItem); 
                        
            // Begin at the base directory path
            
            baseDir = new File(sbImageBase);
            if (baseDir.exists() && baseDir.isDirectory()) {
                logItem = "Base Directory '" + baseDir.getName() + "' was found.";
                if (logLevel > 2) {out.println(logItem);}
                debug (debugLevel, 0, logItem); 
                logItem = "Processing contents of Base Directory...";
                if (logLevel > 2) {out.println(logItem);}
                debug (debugLevel, 0, logItem); 
                baseContents = baseDir.list();  // Get String[] array of element names in Base Directory
                if (baseContents == null || (baseContents != null && baseContents.length == 0)) {
                    logItem = "ERROR! - Base Directory '" + baseDir.getName() + "' is empty."; 
                    if (logLevel > 0) {out.println(logItem);}
                    debug (debugLevel, 0, logItem);
                } else {
                    for (i0 = 0; i0 < baseContents.length; i0++) {  // iterate through all the elements in the base directory
                        workElement = new File(baseDir, baseContents[i0]);
                        if (workElement.isDirectory()) {
                            familyDir = workElement;
                            logItem = "Base element " + (i0 + 1) + " is Family Code directory '" + familyDir.getName() + "'.";
                            if (logLevel > 2) {out.println(logItem);}
                            debug (debugLevel, 0, logItem); 
                            logItem = "  Processing contents of this Family Code Directory...";
                            if (logLevel > 2) {out.println(logItem);}
                            debug (debugLevel, 0, logItem); 
                            familyContents = familyDir.list();
                            if (familyContents == null|| (familyContents != null && familyContents.length == 0)) {
                                logItem = "    Warning! - Family Code Directory '" + familyDir.getName() + "' is empty.";
                                if (logLevel > 1) {out.println(logItem);}
                                debug (debugLevel, 0, logItem); 
                            } else {
                                for (i1 = 0; i1 < familyContents.length; i1++) {  // iterate through elements in the family dir
                                    workElement = new File(familyDir, familyContents[i1]);
                                    if (workElement.isDirectory()) {
                                        subfamilyDir = workElement;
                                        logItem = "    Family directory element " + (i1 + 1)
                                                + " is Subfamily Code directory '" + subfamilyDir.getName() + "'.";
                                        if (logLevel > 2) {out.println(logItem);}
                                        debug (debugLevel, 0, logItem); 
                                        logItem = "      Processing contents of this Subfamily Code Directory...";
                                        if (logLevel > 2) {out.println(logItem);}
                                        debug (debugLevel, 0, logItem); 
                                        subfamilyContents = subfamilyDir.list();
                                        if (subfamilyContents == null || (subfamilyContents != null && subfamilyContents.length == 0)) {
                                            logItem = "      Warning! - Subfamily Code Directory '" + familyDir.getName() + "/" + subfamilyDir.getName() + "' is empty.";
                                            if (logLevel > 1) {out.println(logItem);}
                                            debug (debugLevel, 0, logItem); 
                                        } else {
                                            for (i2 = 0; i2 < subfamilyContents.length; i2++) {  // iterate through elements in the subfamily dir
                                                workElement = new File(subfamilyDir, subfamilyContents[i2]);
                                                if (workElement.isDirectory()) {
                                                    logItem = "      ERROR! Subfamily element " + (i2 + 1) 
                                                            + " " + familyDir.getName() + "/" + subfamilyDir.getName()
                                                            + " contains an unexpected directory named '" +workElement.getName() + "'";
                                                    if (logLevel > 0) {out.println(logItem);}
                                                    debug (debugLevel, 0, logItem); 
                                                } else {
                                                    subfamilyFile = workElement;
                                                    logItem = "      Subfamily element " + (i2 + 1) 
                                                            + " " + familyDir.getName() + "/" + subfamilyDir.getName()
                                                            + " is an image file named '" + subfamilyFile.getName() + "', length: "
                                                            + subfamilyFile.length();
                                                    if (logLevel > 2) {out.println(logItem);}
                                                    debug (debugLevel, 0, logItem); 
                                                    selectBoxName = getSelectBoxName(conn, familyDir.getName(), subfamilyDir.getName(), subfamilyFile.getName());
                                                    if (selectBoxName.equals("")) {
                                                        logItem = "      Warning! "
                                                                + familyDir.getName() + "/" + subfamilyDir.getName()
                                                                + "/" + subfamilyFile.getName() + " is not referenced in an image select box.";
                                                        if (logLevel > 1) {out.println(logItem);}
                                                        debug (debugLevel, 0, logItem); 
                                                    } else {
                                                        mCodes = getMfgrCodes(conn, conn3, familyDir.getName(), subfamilyDir.getName(), selectBoxName);
                                                        if (mCodes == null) {
                                                            logItem = "      An ERROR occurred when trying to find the mfgr codes for image file "
                                                                    + familyDir.getName() + "/" + subfamilyDir.getName()
                                                                    + "/" +  subfamilyFile.getName();
                                                            if (logLevel > 0) {out.println(logItem);}
                                                            debug (debugLevel, 0, logItem); 
                                                        } else {
                                                            if (mCodes[0] != null && mCodes[0].equals("No Rule")) {
                                                                logItem = "    WARNING! No RuleSet was found using select box '"
                                                                    + selectBoxName + "' containing image file named "    
                                                                    + familyDir.getName() + "/" +  familyFile.getName();
                                                                if (logLevel > 1) {out.println(logItem);}
                                                                debug (debugLevel, 0, logItem); 
                                                            } else {
                                                                for (j = 0; j < mCodes.length; j++) {
                                                                    if (mCodes[j] == null || mCodes[j].equals("")) break;
                                                                    logItem = "        Found mfgr code '" + mCodes[j] + "'";
                                                                    if (logLevel > 2) {out.println(logItem);}
                                                                    debug (debugLevel, 0, logItem); 
                                                                    mfgrImageName = imagePath + mCodes[j].toLowerCase() + fsep + subfamilyFile.getName();
                                                                    mfgrImage = new File(mfgrImageName);
                                                                    if (mfgrImage.exists() && mfgrImage.isFile()) {
                                                                        logItem = "        ERROR! File '" 
                                                                                + familyDir.getName() + "/" + subfamilyDir.getName() + "/"
                                                                                + subfamilyFile.getName()
                                                                                + "' also exists in manufacturer directory '" + mCodes[j] + "'";
                                                                        if (logLevel > 0) {out.println(logItem);}
                                                                        debug (debugLevel, 0, logItem);
                                                                    }
                                                                    mfgrImage = null;
                                                                }
                                                            }    
                                                        }
                                                    }
                                                    subfamilyFile = null;
                                                }
                                            }
                                        }
                                    } else { // it's a file
                                        familyFile = workElement;
                                        logItem = "  Family directory element " + (i1 + 1) 
                                                + " is Family level image file '" 
                                                + familyDir.getName() + "/" + familyFile.getName() + "', length: "
                                                + familyFile.length();
                                        if (logLevel > 2) {out.println(logItem);}
                                        debug (debugLevel, 0, logItem); 
                                        selectBoxName = getSelectBoxName(conn, familyDir.getName(), "*", familyFile.getName());
                                        if (selectBoxName.equals("")) {
                                            logItem = "    Warning! " + familyDir.getName() + "/" +  familyFile.getName() 
                                            + " is not referenced in a global image select box.";
                                            if (logLevel > 1) {out.println(logItem);}
                                            debug (debugLevel, 0, logItem); 
                                        } else {
                                            mCodes = getMfgrCodes(conn, conn3, familyDir.getName(), "*", selectBoxName);
                                            if (mCodes == null) {
                                                logItem = "    An ERROR occurred when trying to find the mfgr codes for image file " 
                                                        + familyDir.getName() + "/" +  familyFile.getName();
                                                if (logLevel > 0) {out.println(logItem);}
                                                debug (debugLevel, 0, logItem); 
                                            } else {
                                                if (mCodes[0] != null && mCodes[0].equals("No Rule")) {
                                                    logItem = "    WARNING! No RuleSet was found using select box '"
                                                        + selectBoxName + "' containing image file named "    
                                                        + familyDir.getName() + "/" +  familyFile.getName();
                                                    if (logLevel > 1) {out.println(logItem);}
                                                    debug (debugLevel, 0, logItem); 
                                                } else {
                                                    for (j = 0; j < mCodes.length; j++) {
                                                        if (mCodes[j] == null || mCodes[j].equals("")) break;
                                                        logItem = "      Found mfgr code '" + mCodes[j] + "'";
                                                        if (logLevel > 2) {out.println(logItem);}
                                                        debug (debugLevel, 0, logItem);
                                                        mfgrImageName = imagePath + mCodes[j].toLowerCase() + fsep + familyFile.getName();
                                                        mfgrImage = new File(mfgrImageName);
                                                        if (mfgrImage.exists() && mfgrImage.isFile()) {
                                                            logItem = "        ERROR! File '" 
                                                                    + familyDir.getName() + "/" 
                                                                    + familyFile.getName()
                                                                    + "' also exists in manufacturer directory '" + mCodes[j] + "'";
                                                            if (logLevel > 0) {out.println(logItem);}
                                                            debug (debugLevel, 0, logItem);
                                                        }
                                                        mfgrImage = null;
                                                    }
                                                }
                                            }
                                        }
                                        familyFile = null;
                                    }
                                }
                            }
                            familyDir = null;
                        } else {
                            logItem = "  ERROR! Base Element " + (i0 + 1) + " is an unexpected file named '" 
                                    + workElement.getName() + "'.";
                            if (logLevel > 0) {out.println(logItem);}
                            debug (debugLevel, 0, logItem); 
                        }
                        workElement = null;
                    }
                }    
            } else {
                logItem = "ERROR! Base directory for images\\catalog\\sb\\ was not found.";
                if (logLevel > 0) {out.println(logItem);}
                debug (debugLevel, 0, logItem); 
            }
            
            
            
            logItem = "Execution complete.";
            if (logLevel > 0) {out.println(logItem);}
            debug (debugLevel, 0, logItem); 
            out.close();
            request.setAttribute("message", "Execution complete.");
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            conn.closeStatement();
            conn.close();
            conn3.closeStatement();
            conn3.close();
            return;
            
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = "An unexpected error occurred in " + SERVLET_NAME + " during initialization." + e ;
            request.setAttribute("message", errorMsg);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            conn.close();
            conn3.close();
            return;
        }
    }
    
    private void debug (int limit, int level, String x) {
        if (limit >= level) {
            System.out.println(x);
        }
    }
    
    private String getSelectBoxName (WDSconnect con, String famCode, String subfamCode, String option) {
        ResultSet rs = null;
        String result = "";
        try {
            String queryString = "SELECT select_box_name ";
            queryString += "FROM pub.ps_select_boxes ";
            queryString += "WHERE family_code = '" + famCode.toUpperCase() + "'" ;
            queryString += "AND subfamily_code = '" + subfamCode.toUpperCase() + "'" ;
            queryString += "AND option_image = '" + option + "'" ;
            rs = con.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    result = rs.getString("select_box_name");
                }
                rs.close();
                rs = null;
            }
            return result;
        } catch (Exception e) {
            return "";
        }
    }
          
    private String[] getSelectBoxNames (WDSconnect con, String famCode, String subfamCode, String option) {
        ResultSet rs = null;
        String[] results = new String[100];
        int count = 0;
        
        try {
            String queryString = "SELECT select_box_name ";
            queryString += "FROM pub.ps_select_boxes ";
            queryString += "WHERE family_code = '" + famCode.toUpperCase() + "'" ;
            queryString += "AND subfamily_code = '" + subfamCode.toUpperCase() + "'" ;
            queryString += "AND option_image = '" + option + "'" ;
            rs = con.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    results[count++] = rs.getString("select_box_name");
                }
                rs.close();
                rs = null;
            }
            return results;
        } catch (Exception e) {
            results = null;
            return null;
        }
    }
    
    private String[] getMfgrCodes(WDSconnect con, WWWconnect con3, String famCode, String subfamCode, String sbName) {
        
        // Return a list of mfgr codes that have parts in this fam/subfam
        
        String[] error1 = {"No Rule"};
        int i = 0;
        int j = 0;
        String queryString = "";
        ResultSet rs = null;
        ResultSet rs2 = null;
        String[] results = new String[100];
        boolean ruleSetFound = false;
        int seqNum = 0;
        String[] sfcs = new String[100];
        int sfcSize = 0;
        String ucFamCode = famCode.toUpperCase();
        String ucSubfamCode = subfamCode.toUpperCase();
        String work = "";
        
        // First find the Global or Local Ruleset for the parm field that references this select box
        // if the subfamily code is an asterisk, it will be a global ruleset for this parm field.
        //debug (0, 0, "    Get MFGR Codes STEP 1");
        try {
            queryString = "SELECT seq_num, rule_scope ";
            queryString += "FROM pub.ps_rules ";
            queryString += "WHERE family_code = '" + ucFamCode + "'" ;
            queryString += "AND subfamily_code = '" + ucSubfamCode + "'" ;
            queryString += "AND q_object = 'S' " ;
            queryString += "AND q_select_box_name = '" + sbName + "'";
            rs = con.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    seqNum = rs.getInt("seq_num"); 
                    ruleSetFound = true;
                }
                rs.close();
                rs = null;
            }
            if (!ruleSetFound) {
                return error1;
            }
        } catch (Exception e) {
            return null;
        }
        
        // If a local ruleset, set the subfamily code accordingly
        // if a global ruleset, find all the subfamily codes that are affected by the global ruleset
        //debug (0, 0, "    Get MFGR Codes STEP 2");
        if (!subfamCode.equals("*")) {
            sfcs[sfcSize++] = ucSubfamCode;
        } else {
            try {
                queryString = "SELECT subfamily_code ";
                queryString += "FROM pub.ps_subfamily ";
                queryString += "WHERE family_code = '" + ucFamCode + "'" ;
                rs = con.runQuery(queryString);
                if (rs != null) {
                    while (rs.next()) {
                        work = rs.getString("subfamily_code");
                        queryString = "SELECT rule_scope ";
                        queryString += "FROM pub.ps_rules ";
                        queryString += "WHERE family_code = '" + ucFamCode + "'" ;
                        queryString += "AND subfamily_code = '" + work + "'" ;
                        queryString += "AND seq_num = " + seqNum;
                        rs2 = con.runQuery(queryString);
                        if (rs2 != null) {
                            if (!rs2.next()) {
                                sfcs[sfcSize++] = work;
                            }
                            rs2.close();
                            rs2 = null;
                        }
                    }
                    rs.close();
                    rs = null;
                }
            } catch (Exception e) {
                return null;            
            }    
        }
        
        // if no subfamily codes were found, return with an empty list of mfgr codes
        //debug (0, 0, "    Get MFGR Codes STEP 3");
        if (sfcSize == 0) {
            return results;
        }
                        
        // Otherwise, find all affected mfgr codes
        //debug (0, 0, "    Get MFGR Codes STEP 4");
        try {
            queryString = "SELECT DISTINCT sales_subcat ";
            queryString += "FROM pub.catalogitem ";
            queryString += "WHERE family_code = '" + ucFamCode + "'" ;
            queryString += "AND (";
            for (i = 0; i < sfcSize; i++) {
                if (i > 0) {
                    queryString += " OR ";
                }
                queryString += " subfamily_code = '" + sfcs[i] + "'" ;    
            }
            queryString += ")";
            //debug (0, 0, queryString);
            rs = con3.runQuery(queryString);
            if (rs != null) {
                j = 0;
                while (rs.next()) {
                    results[j++] = rs.getString("sales_subcat");
                }
                rs.close();
                rs = null;
                //debug (0, 0, "    Get MFGR Codes STEP 5");
                return results;
            }
            //debug (0, 0, "    Get MFGR Codes STEP 5A");
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private boolean optionExists (WDSconnect con, String famCode, String subfamCode, String option) {
        ResultSet rs = null;
        try {
            String queryString = "SELECT * ";
            queryString += "FROM pub.ps_select_boxes ";
            queryString += "WHERE family_code = '" + famCode.toUpperCase() + "'" ;
            queryString += "AND subfamily_code = '" + subfamCode.toUpperCase() + "'" ;
            queryString += "AND option_image = '" + option + "'" ;
            rs = con.runQuery(queryString);
            if (rs != null) {
                if (!rs.next()) {
                    rs.close();
                    //con.closeStatement();
                    return true;
                }
                rs.close();
            }
            //con.closeStatement();
            return false;
        } catch (Exception e) {
            //con.closeStatement();
            return false;
        }
    }
    
    private String removeSlashes(String x) {
        int i = x.indexOf("/");
        while (i != -1) {
            x = x.substring(0, i) + x.substring(i+1);
            i = x.indexOf("/");
        }
        return x;
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
