/*
 * gpsdvf2.java
 *
 * Created on October 2, 2007, 2:36 PM
 */

package gpsParmData;

import OEdatabase.WDSconnect;
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
 * 
 * @author Sauter
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I get product line, family, subfamily, manufacturer code,
 * and instockonly switch from
 * gpsdwf1.jsp and update session variables.
 * Then I extract the rules for the search fields
 * I need to send to gpsdvf2.jsp which builds the
 * web page that gets the query info for the search by value.
 */
public class gpsdvf2 extends HttpServlet {
        
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsdvf2.java";
    private final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    
    // now we have family code, subfamily code, manufacturer(s), instock, ID, and part number (maybe)    
    // next build a set of qrules for each search field
    // build arguments for a search page like we did when we built a DE page
    // pass these dudettes off to the jsp page
        
        String work = request.getParameter("validation");          // check for invalid call
        if (!work.equals("OK")) {
            response.sendRedirect ("gpsnull.htm");
            return;
        }

        HttpSession session = request.getSession();         // Get a handle on our session 
        if (session.isNew()) {                              // check for timeout
            response.sendRedirect ("gpstimeout.htm");
            return;
        }
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
        debugSw = false;
        
        
        String auditTimeRaw = DateTime.getTimeRawStr();
        String auditUserID = "";
        String dataType = "";
        String displayUnits = "";
        String enableToolTips = "";
        String errMsg = "";
        String familyCode = "";
        String familyName = "";
        int fieldNum;
        boolean inStockOnly;
        int iwork;
        int iwork2;
        String manufacturerCode = "";
        String manufacturerName = "";
        String productLineCode = "";
        String productLineName = "";
        ResultSet rs = null;
        String subfamilyCode = "";
        String subfamilyName = "";
        String traxDate = DateTime.getDateMMDDYY();
        String traxTime = DateTime.getTimeHHMMSS(":");
        GPSunit unit = new GPSunit();
        String work2 = "";
        String work3 = "";
        
        GPSrules qRules;
        GPSfieldSet fieldSet;
        GPSrules[] rules;
                
        try {    

            //  Get Initial set up and save in Session variables if we got xtrol from gpsdvf1.jsp.        
        
            work = request.getParameter("B1");
            if (work.equals("Search")) {
                auditUserID = request.getParameter("auditUserID");
                enableToolTips = request.getParameter("enableToolTips");
                familyCode = request.getParameter("familyCode");
                familyName = request.getParameter("familyName");
                inStockOnly = request.getParameter("inStockOnly").equals("Y") ? true : false;
                productLineCode = request.getParameter("productLineCode");
                productLineName = request.getParameter("productLineName");
                manufacturerCode = request.getParameter("manufacturerCode");
                manufacturerName = request.getParameter("manufacturerName");
                subfamilyCode = request.getParameter("subfamilyCode");
                subfamilyName = request.getParameter("subfamilyName");
            
                // Set session variables
            
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);
                session.setAttribute("sbProductLineCode", productLineCode);
                session.setAttribute("sbProductLineName", productLineName);
                session.setAttribute("sbFamilyCode", familyCode);
                session.setAttribute("sbFamilyName", familyName);
                session.setAttribute("sbSubfamilyCode", subfamilyCode);
                session.setAttribute("sbSubfamilyName", subfamilyName);
                session.setAttribute("sbManufacturerCode", manufacturerCode);
                session.setAttribute("sbManufacturerName", manufacturerName);
                session.setAttribute("inStockOnly", inStockOnly ? "Y" : "N");
            } else {
                request.setAttribute("message", "Module " + SERVLET_NAME + " was not properly invoked");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }  
        } catch (Exception e){
            e.printStackTrace();
            errMsg = "An error occurred in " + SERVLET_NAME  + "<br />" + e ;
        }
    
        WDSconnect conn = new WDSconnect();         // Connect to WDS database 
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        // Extract Rules for this Family and Subfamily from the rules database

        try {
            List <String> seqNumMap = new ArrayList <String> ();
            ArrayList <String> generatedScript = new ArrayList <String> ();
            fieldSet = new GPSfieldSet();
            rules = fieldSet.getRules(conn, familyCode, subfamilyCode, GPSfieldSet.DISPLAY_ORDER);
            debug("Rules Object successfully created.");
            int count;
            fieldNum = 0;
            for (count = 0; count < fieldSet.count(); count++) {
                if (rules[count].getSearchOrder() != 0) {
                    debug("Generating script for field " + rules[count].getSeqNum() + " - " 
                            + rules[count].getParmName());
                    ArrayList <String> fList = rules[count].getSearchGeneratedScript(conn, fieldNum);
                    if (fList == null) {
                        request.setAttribute("message", "Module " + SERVLET_NAME 
                            + " encountered a fatal error when processing parm field '" 
                                + rules[count].getParmName() + "'");
                        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        return;
                    }
                    for (int k = 0; k < fList.size(); k++) {
                        work = fList.get(k);
                        generatedScript.add(work);
                    }
                    seqNumMap.add(Integer.toString(rules[count].getSeqNum()));
                    fieldNum++;
                }        
            }
            conn.close();
            if (fieldNum == 0) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " could not find any Rules for this Family/Subfamily code.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
                
            session.setAttribute("generatedScript", generatedScript);
            session.setAttribute("seqNumMap", seqNumMap);
         
        } catch (Exception e){
            request.setAttribute("message", "An error occurred in " + SERVLET_NAME + "<br />" + e);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            conn.close();
            return;
        }
        // Now that we have the fields, we extract additional data to build the JavaScript code
        // that will build the XHTML inside the browser
        if (!errMsg.equals("")) {
            request.setAttribute("message", errMsg);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } else {
            RequestDispatcher view = request.getRequestDispatcher("gpsdvf2.jsp");
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
