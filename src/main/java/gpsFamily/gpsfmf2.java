/*
 * gpsfmf2.java
 *
 * Created on February 12, 2007, 3:44 PM
 */

package gpsFamily;

import OEdatabase.WDSconnect;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 * Modified 03/25/2010 by DES to support allow_parent_all field
 * Modified 4/05/2010 by DES to support Index Keywords.
 * Modified 7/14/2010 by DES to remove Index Keywords.
 *
 * I obtain the Family Code to be modified and pass the info to
 * gpsfmf2.jsp to display the modification form.
 *
 * Modified 9/29/2010 by DES to support division CP
 *
 */
public class gpsfmf2 extends HttpServlet {
    
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.5.04";
      
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

        String allowParentAll = "*undefined*";
        String altFamilyCode = "*undefined*";
        String altProductLineCode = "*undefined*";
        String familyCode = "*undefined*";
        int familyCodeIndex = -1;
        String keywordsIndex = "*undefined*";
        String keywordsPlural = "*undefined*";
        String keywordsSingular = "*undefined*";
        String parentFamilyCode = "*undefined*";
        int rc = 0;
        
        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {        
            sWork = uStamp + " failed to connect to WDS database; aborting.";
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        // Build query to extract existing Product Line codes 
        // and Family Codes from the database in prep for an update
    
        try {
            GPSproductLines productLines = new GPSproductLines();
            rc = productLines.open(conn);
            if (rc != 0) {
                sWork = uStamp + " failed to obtain Product Line Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                conn.close();
                return;
            }   
            ArrayList lines = productLines.getArrayList("CP");
            GPSfamilyCodes famCodes = new GPSfamilyCodes();
            rc = famCodes.open(conn);
            if (rc < 0) {
                sWork = uStamp + " failed to obtain Family Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                famCodes = null;
                conn.close();
                return;                
            }
            familyCode = request.getParameter("code");
            familyCodeIndex = famCodes.getFamilyCodeIndex(familyCode);
            if (familyCodeIndex > -1) {
                allowParentAll = famCodes.getAllowParentAll(familyCodeIndex);
                altFamilyCode = famCodes.getAltFamilyCode(familyCodeIndex);
                altProductLineCode = famCodes.getAltProductLineCode(familyCodeIndex);
                keywordsIndex = ""; //famCodes.getKeywordsIndex(familyCodeIndex);
                keywordsPlural = famCodes.getKeywordsPlural(familyCodeIndex);
                keywordsSingular = famCodes.getKeywordsSingular(familyCodeIndex);
                parentFamilyCode = famCodes.getParentFamilyCode(familyCodeIndex);
            }
            ArrayList familyCodes = famCodes.getArrayList();
            request.setAttribute("lines", lines);
            request.setAttribute("familyCodes", familyCodes);
            request.setAttribute("parentFamilyCode", parentFamilyCode);
            request.setAttribute("allowParentAll", allowParentAll);
            request.setAttribute("altFamilyCode", altFamilyCode);
            request.setAttribute("altProductLine", altProductLineCode);
            //request.setAttribute("keywordsIndex", keywordsIndex);
            request.setAttribute("keywordsPlural", keywordsPlural);
            request.setAttribute("keywordsSingular", keywordsSingular);
            request.setAttribute("statusMessage", "");
            RequestDispatcher view = request.getRequestDispatcher("gpsfmf2.jsp");
            view.forward(request,response);
            productLines = null;
            famCodes = null;
        } catch (Exception e) {
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } finally {
            conn.close();
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
