/*
 * gpsdaf3.java
 *
 * Created on September 25, 2006, 2:01 PM
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
import sauter.util.*;

/**
 *
 * @author Sauter
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * updated to Version 1.2.00  9/11/2007 DES
 *
 */
public class gpsdaf3 extends HttpServlet {
    
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsdaf3.java";
    private final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        // Check for invalid Call  i.e., validation key must be set to "OK" */

	String yadda = request.getParameter("validation");
        if (!yadda.equals("OK")) {
            response.sendRedirect ("gpsabend.jsp?rc=gps4000");
            return;
        }
         
        // Get a handle on our session and check for timeout

        HttpSession session = request.getSession();
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
        //debugSw = true;
        
        if (session.isNew()) {          // check for session timeout
           response.sendRedirect ("gpstimeout.htm");
           return;
        }
        
        // Declare some variables here

        boolean autoClear = false;
        boolean completionCode = false;
        boolean replaceExistingData = false;
                
        String traxDate = DateTime.getDateMMDDYY();
        String auditDate = traxDate; 
        String auditTimeRaw = Integer.toString(DateTime.getSecondsSinceMidnight());
                
        double dDefaultValue;
        double dMaxValue;
        double dMinValue;
        final String UC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String LC = "abcdefghijklmnopqrstuvwxyz";
        final String SP = " ";
        final String NU = "0123456789";
        final String QU = "\"";
        final String AP = "'";
        int workInt;
        int seqNum;
        ResultSet rs;
        String cooked = "";
        String eString = "";
        String keyString;
        String message = "";
        String queryString;
        String partNum;
        String raw = "";
        String tempWork;
        String valueString;
        String work;
          
        // Make sure this was a valid call from the web browser 
        // and if so, extract and set session attributes from Request Object
        // otherwise abort
        
        String junk = (String) request.getParameter("B1");
        if (junk != null && junk.equals("Add")) {
            autoClear = request.getParameter("autoClear").equals("Y");
            session.setAttribute("autoClear", autoClear ? "Y" : "N");
            session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
            replaceExistingData = request.getParameter("replaceExistingData").equals("Y");
            session.setAttribute("replaceExistingData", replaceExistingData ? "Y" : "N");
            partNum = request.getParameter("partNum").toUpperCase();
            session.setAttribute("previousPartNum", partNum);
            session.setAttribute("traxDate", traxDate);

        } else {
            response.sendRedirect ("gpsabend.jsp?rc=gps4001");
            return;
        }    

        // Get values from Session Variables
        // Make sure we have no null object references.

        String familyCode = (String)session.getAttribute("familyCode");
        familyCode = EditText.setDefault(familyCode,"");
        if (familyCode.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps4002");
            return;
        }    
        String subfamilyCode = (String)session.getAttribute("subfamilyCode");
        subfamilyCode = EditText.setDefault(subfamilyCode,"");
        if (subfamilyCode.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps4003");
            return;
        }
            
        String auditUserID = (String)session.getAttribute("auditUserID");
        auditUserID = EditText.setDefault(auditUserID,"");
        if (auditUserID.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps4006");
            return;
        }
                       
        // The Sequence Number Map contains the parm sequence numbers 
        // that correspond to the form DE objects
        
        List seqNumMap = (List)session.getAttribute("seqNumMap");
        if (seqNumMap == null ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps4007");
            return;
        }
        
        @SuppressWarnings("unchecked")

        List<String> previousValue = (List<String>) session.getAttribute("previousValue");
        previousValue.clear();
        
        for (int i = 0; i < seqNumMap.size() ;  i++) {
            cooked = "inputObject" + Integer.toString(i);
            raw = "raw" + Integer.toString(i);
            junk = request.getParameter(raw);
            if (junk == null) { junk = request.getParameter(cooked); }
            work = partNum + ", " + familyCode + ", " + subfamilyCode + ", " + junk;
            tempWork = "    aPreviousValue[f2++] = \"" + junk + "\";";
            previousValue.add(tempWork);
        }

        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        debug (SERVLET_NAME + " connected to database");
 
        // Okies, Now we do any server side validation on data vales here
        // before we blindly add them to the ps_parm table
 
        // see if the Part Number exists
        // if No we set Status Message accordingly and call gpsdaf2.jsp
        // if yes we continue below
        
        try {
            if (!GPSpart.exists(conn, partNum)) {
                message = "Error! Part number " + partNum + " does not exist.";
                debug (message);
                request.setAttribute("statusMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("gpsdaf2.jsp");
                view.forward(request,response);
                conn.close();
                conn = null;
                return;
            }
            debug ("Part number " + partNum + " was found.");   
            // Check to see if there is parm data already loaded in ps_parm table for this part num.
            // if data pre-exists and overwrite data is  NOT selected
            // set Error message and call gpsdaf2.jsp
            // else delete the existing data from the ps_parm table
// ** BEGIN TRANSACTION **            
            if (!conn.enableTransactions()) {
                message = "Fatal Error - Attempt to enable Transactions in WDS database failed.";
                debug (message);
                request.setAttribute("statusMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("gpsdaf2.jsp");
                view.forward(request,response);
                conn.close();
                conn = null;
                return;
            }
            debug ("Database Transaction Management has been enabled.");       
            if (GPSparmSet.exists(conn, partNum)) {
                debug ("Parametric data already exists for part number " + partNum);
                if (replaceExistingData == false) {
                    message = "Parametric data for Part Number " + partNum + " exists and will not be deleted.";
                    request.setAttribute("statusMessage", message);
                    RequestDispatcher view = request.getRequestDispatcher("gpsdaf2.jsp");
                    view.forward(request,response);
                    conn.close();
                    conn = null;
                    return;
                } else {
                    debug ("Attempting to delete existing parametric data for this part number.");
                    if (!GPSparmSet.delete(conn, partNum)) {
                        message = "An error occurred while attempting to delete existing Parametric Data for Part Number " + partNum;
                        debug (message);
                        conn.rollback();
                        request.setAttribute("statusMessage", message);
                        RequestDispatcher view = request.getRequestDispatcher("gpsdaf2.jsp");
                        view.forward(request,response);
                        conn.close();
                        conn = null;
                        return;
                    } 
                }
            }        

            //      loop through all the parm values, adding them one by one to the ps_parm table
            
            for (int i = 0; i < seqNumMap.size() ;  i++) {
                seqNum = Integer.parseInt((String)seqNumMap.get(i));
                cooked = "inputObject" + Integer.toString(i);
                raw = "raw" + Integer.toString(i);
                junk = request.getParameter(raw);
                if (junk == null) { junk = request.getParameter(cooked); }
                debug ("Attempting to add " + cooked + " value='" + junk + "'");
                if (!GPSparmSet.add(conn, partNum, seqNum, junk, auditDate, auditTimeRaw, auditUserID)) {
                    conn.rollback();
                    message = "An Error occurred while attempting to add data to the Parm Data table.";
                    debug (message);
                    request.setAttribute("statusMessage", message);
                    RequestDispatcher view = request.getRequestDispatcher("gpsdaf2.jsp");
                    view.forward(request,response);
                    conn.close();
                    conn = null;
                    return;
                }
                debug ("Add was successful");
            }                

            //      update the family and subfamily name in the part number table
            
            if (!GPSpart.updatePartRec(conn, partNum, familyCode, subfamilyCode)) {
                message += "An error occurred while attempting to update the part table.";
                debug (message);
                conn.rollback();
                request.setAttribute("statusMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("gpsdaf2.jsp");
                view.forward(request,response);
                conn.close();
                conn = null;
                return;                
            }
            
            debug ("Part record has been successfully updated.");
            
            // ** End Transaction **
        
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occured while talking to the database in " + SERVLET_NAME);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            conn.close();
            conn = null;
            return;
        }
        conn.commit();
        // Disable transaction management
        if (!conn.disableTransactions()) {
            message = "Fatal Error - Attempt to disable Transactions in WDS database failed.";
            debug (message);
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsdaf2.jsp");
            view.forward(request,response);
            conn.close();
            conn = null;
            return;
        } else {
            debug ("WDS Transaction Management is now disabled.");
        }
        message = "Operation completed successfully.";
        debug (message);
        request.setAttribute("statusMessage", message);
        RequestDispatcher view = request.getRequestDispatcher("gpsdaf2.jsp");
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
