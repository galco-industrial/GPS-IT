/*
 * gpsdvf3.java
 *
 * Created on October 4, 2007, 2:20 PM
 */

package gpsParmData;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import gps.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * 
 * 
 * @author Sauter
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I get the search values from gpsdwf2.jsp and
 * save the list in a previous values array in case the user
 * wants to do another search with similar values.
 * Then I actually perform the search with these current values.
 * I then pass a list of part numbers and selected key values
 * that resulted from this search to gpsdvf3.jsp which allows the
 * user to view one or more of the found items and compare 
 * their complete parametric value set side by side.
 *  
 * Modification History
 * 
 * 07/21/2007 DES   fixed rankscore calculation if there was only one searchweight in totalweights
 * 10/04/2007 DES   updated code to use AJAX and embed business logic in Class modules
 * 10/08/2007 DES   moved business logic from thi8s servlet into a class module called GPSparmSearch
 */
public class gpsdvf3 extends HttpServlet {
                
    private boolean debugSw = false;
    private static final String SERVLET_NAME = "gpsdvf3.java";
    private static final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        String work = request.getParameter("validation"); // Check for invalid Call
        if (work == null || !work.equals("OK")) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6499");
            return;
        }
        
        //work = request.getParameter("B1");
        //if (work == null || !work.equals("Search")) {
        //    response.sendRedirect ("gpsabend.jsp?rc=gps6500");
        //    return;
        //}
        
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
        
        // Declare some local variables here
                       
        String auditUserID;
        String enableToolTips;
        String familyCode;
        String familyName;
        boolean inStockOnly;
        String manufacturerCode;
        String manufacturerName;        
        String subfamilyCode;
        String subfamilyName;
        
        GPSparmSearch parmSearch = new GPSparmSearch();
        List <String> generatedScript = null;

        // Check request values and set Session Variables
        // Make sure we have no null object references.
        
        auditUserID = setDefault(request.getParameter("auditUserID"),"");
        if (auditUserID.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6501");
            return;
        }
        session.setAttribute("auditUserID", auditUserID);
        
        enableToolTips = setDefault(request.getParameter("enableToolTips"),"");
        //if (enableToolTips.equals("") ) {
        //    response.sendRedirect ("gpsabend.jsp?rc=gps6502");
        //    return;
        //}
        session.setAttribute("enableToolTips", enableToolTips);
       
        familyCode = setDefault(request.getParameter("familyCode"),"");
        if (familyCode.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6503");
            return;
        }
        session.setAttribute("sbFamilyCode", familyCode);
        
        familyName = setDefault(request.getParameter("familyName"),"");
        if (familyName.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6504");
            return;
        }
        session.setAttribute("sbFamilyName", familyName);
               
        manufacturerCode = setDefault(request.getParameter("manufacturerCode"),"");
        if (manufacturerCode.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6505");
            return;
        }
        session.setAttribute("sbManufacturerCode", manufacturerCode);
        
        work = (String) session.getAttribute("inStockOnly");
        inStockOnly = work.equals("Y") ? true : false;
        // session.setAttribute("inStockOnly", inStockOnly ? "Y" : "N");
        
        subfamilyCode = setDefault(request.getParameter("subfamilyCode"),"");
        if (subfamilyCode.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6507");
            return;
        }
        session.setAttribute("sbSubfamilyCode", subfamilyCode);
        
        subfamilyName = setDefault(request.getParameter("subfamilyName"),"");
        if (subfamilyName.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6508");
            return;
        }
        session.setAttribute("sbSubfamilyName", subfamilyName);
            
        // The Sequence Number Map contains the parm sequence numbers 
        // that correspond to the form Search objects order
        
        List seqNumMap = (List)session.getAttribute("seqNumMap");
        if (seqNumMap == null ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6521");
            return;
        }
        
        debug("Request parms have been validated.");
        WDSconnect conn = new WDSconnect();  // Connect to WDS database    
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        debug("Database is open.");
        
        generatedScript = parmSearch.search(request, session, conn, familyCode,
                subfamilyCode, manufacturerCode, inStockOnly, seqNumMap);
        
        request.setAttribute("generatedScript", generatedScript);
        RequestDispatcher view = request.getRequestDispatcher("gpsdvf3.jsp");
        view.forward(request,response);
        conn.close();
        conn = null;
        return;
    }
   
private void debug (String x) {
    if (debugSw) {
        System.out.println(x);
    }
}

private String setDefault(String item, String itemDefault) {
    if (item == null) {
        item = "";
    }
    if (item.equals("") ) {
        item = itemDefault;
    }
    return item;
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
