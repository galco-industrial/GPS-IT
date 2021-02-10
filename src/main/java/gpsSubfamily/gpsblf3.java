/*
 * gpsblf3.java
 *
 * Created on June 12, 2009, 3:55 PM
 */

package gpsSubfamily;

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
 * Modified 6/09/2009 by DES to support alt/keywords/buys and index fields
 * Modified 4/05/2010 by DES to support Index Keywords.
 * Modified 7/14/2010 by DES to remove Index Keywords.
 *
 * I obtain the Subfamily Code to be Displayed and pass the info to
 * gpsblf2.jsp to display the Data.
 */
public class gpsblf3 extends HttpServlet {
        
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
        
        ArrayList <String> aliasList = new ArrayList <String> ();                
        String altFamilyCode = "";
        String altProductLine = "";
        String altSubfamilyCode = "";
        String custBuys = "None";
        String familyCode = "";
        String familyName = "";
        int i = 0;
        GPSindex index = null;
        String indexLevel = "";
        String keywordsIndex = "";
        String keywordsPlural = "";
        String keywordsSingular = "";
        String productLineCode = "";
        String productLineName = "";
        int rc = 0;
        String subfamilyCode = "";
        int subfamilyCodeIndex = -1;
        String totalBuys = "None";
        String work = "";
        String work2 = "";
        
        // Set our local variables from form vars
        
        familyCode = request.getParameter("familyCode");
        subfamilyCode = request.getParameter("subfamilyCode");
        debug (debugLevel, 8, uStamp + " processed form variables.");
    
        // Isolate Product Line Name and Family Code & Name and 
        // all Subfamily Codes for this Family from the Subfamily Codes Class
        // in prep for a call to the Delete JSP
    
        try {
            GPSproductLines productLines = new GPSproductLines();
            rc = productLines.open(conn);
            if (rc != 0) {
                productLines = null;
                conn.close();
                sWork = uStamp + " failed to obtain Product Line Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }   
            ArrayList lines = productLines.getArrayList("CP");
            
            GPSfamilyCodes famCodes = new GPSfamilyCodes();
            rc = famCodes.open(conn);
            if (rc < 0) {
                productLines = null;
                famCodes = null;
                conn.close();
                sWork = uStamp + " failed to obtain Family Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;                
            }
            ArrayList familyCodes = famCodes.getArrayList();
                        
            GPSsubfamilyCodes subfamCodes = new GPSsubfamilyCodes();
            rc = subfamCodes.open(conn);
            if (rc < 0) {
                productLines = null;
                famCodes = null;
                subfamCodes = null;
                conn.close();
                sWork = uStamp + " failed to obtain Subfamily Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;                
            }
            ArrayList subfamilyCodes = subfamCodes.getArrayList();
            
            //ArrayList subfamilyCodesList = subfamCodes.getArrayList(familyCode);
            productLineCode = famCodes.getFamilyProductLineCode(familyCode);
            productLineName = productLines.getProductLineName(productLineCode);
            familyName = famCodes.getFamilyName(familyCode);
            subfamilyCodeIndex = subfamCodes.getSubfamilyCodeIndex(familyCode, subfamilyCode);
            indexLevel = Integer.toString(subfamCodes.getIndexLevel(subfamilyCodeIndex));
            keywordsIndex = ""; //subfamCodes.getKeywordsIndex(subfamilyCodeIndex);
            keywordsPlural = subfamCodes.getKeywordsPlural(subfamilyCodeIndex);
            keywordsSingular = subfamCodes.getKeywordsSingular(subfamilyCodeIndex);
            altFamilyCode = subfamCodes.getAltFamilyCode(subfamilyCodeIndex);
            altSubfamilyCode = subfamCodes.getAltSubfamilyCode(subfamilyCodeIndex);
            if (!altFamilyCode.equals("")) {
                altProductLine = famCodes.getFamilyProductLineCode(altFamilyCode);
            }
            custBuys = fmt(subfamCodes.getCustBuys(subfamilyCodeIndex));
            totalBuys = fmt(subfamCodes.getTotalBuys(subfamilyCodeIndex));
            index = new GPSindex(conn, familyCode, subfamilyCode, false);
            for (i = 0; i < index.getSize(); i++) {
                work = index.getFamilyAlias(i) + "/";
                work2 = index.getSubfamilyAlias(i);
                if (work2.trim().equals("")) {
                    work2 = "&lt;none&gt;";
                }
                work += work2;
                if (!index.getActiveAlias(i).equals("Y")) {
                    work += " (Inactive)";
                }
                aliasList.add(work);
            }
            if (index.getSize() == 0) {
                aliasList.add("No aliases exist for this Subfamily.");
            }
            request.setAttribute("altFamilyCode", altFamilyCode);
            request.setAttribute("altProductLine", altProductLine);
            request.setAttribute("altSubfamilyCode", altSubfamilyCode);
            request.setAttribute("familyCode", familyCode);
            request.setAttribute("familyCodesList", familyCodes);
            request.setAttribute("familyName", familyName);
            request.setAttribute("indexLevel", indexLevel);
            // request.setAttribute("keywordsIndex", keywordsIndex);
            request.setAttribute("keywordsPlural", keywordsPlural);
            request.setAttribute("keywordsSingular", keywordsSingular);
            request.setAttribute("productLineName", productLineName);
            request.setAttribute("productLinesList", lines);
            request.setAttribute("statusMessage", "");
            request.setAttribute("subfamilyCode", subfamilyCode);
            request.setAttribute("subfamilyCodesList", subfamilyCodes);
            request.setAttribute("custBuys", custBuys);
            request.setAttribute("aliasList", aliasList);
            request.setAttribute("totalBuys", totalBuys);
            RequestDispatcher view = request.getRequestDispatcher("gpsblf3.jsp");
            view.forward(request,response);
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
    
    private String fmt(int junk) {
        String work = "";
        int k = 0;
        String src = Integer.toString(junk);
        while (src.length() > 0) {
             if (++k == 4) {
                 work = "," + work;
                 k = 1;
             }
             work = src.substring(src.length() - 1) + work;
             src = src.substring(0, src.length() - 1);
        }
        return work;
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
