/*
 * gpsfdf3.java
 *
 * Created on February 14, 2007, 4:22 PM
 */

package gpsFamily;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import java.sql.*;
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
 * Modified 9/01/2010 by DES to update the Index.
 * Modified 9/29/2010 by DES to support division CP
 * Modified 12/09/2011 by DES to ALWAYS update the Index.
 */
public class gpsfdf3 extends HttpServlet {
    
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.5.05";
  
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

        String allowParentAll = "";
        String auditUserID = "";
        String altFamilyCode = "";
        String altProductLineCode = "";        
        boolean completedOK = false;
        int displayOrder;
        String enableToolTips = "";
        String familyCode = "";
        String familyName = "";
        String index = "";
        int j = 0;
        String message = "";
        String message2 = "";
        String keywordsPlural = "";
        String keywordsSingular = "";
        int oldDisplayOrder;
        String oldAllowParentAll = "";
        String oldAltProductLineCode = "";
        String oldAltFamilyCode = "";
        String oldFamilyName = "";
        String oldKeywordsIndex = "";
        String oldKeywordsPlural = "";
        String oldKeywordsSingular = "";
        String oldParentFamilyCode = "";
        String oldProductLineCode = "";
        String parentFamilyCode = "";
        String productLineCode = "";
        String productLineName = "";
        String queryString = "";
        int rc = 0;
        ResultSet rs = null;
        String SQLCommand = "";
        String subfamilyCode = "*";
        String subfamilyName = "";
        String work = "";
    
        // Check Permissions !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        
        try {    
        
            /* Check for invalid Call  i.e., validation key must be set to "OK" */

            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }

            //  Get Initial set up and save in Session variables if we got xtrol from gpslmf2.jsp.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Delete")) {
                // Set our local variables from form vars
                //altFamilyCode = request.getParameter("altFamilyCode");
                //altProductLineCode = request.getParameter("altProductLine");
                auditUserID = request.getParameter("auditUserID");
                displayOrder = Integer.parseInt(request.getParameter("displayOrder"));
                enableToolTips = request.getParameter("enableToolTips");
                familyCode = request.getParameter("familyCode");
                familyName = request.getParameter("familyName");
                index = "1"; // request.getParameter("index");
                if (index == null) {
                    index = "0";
                }
                //keywordsPlural = request.getParameter("pKeywords");
                //keywordsSingular = request.getParameter("sKeywords");
                oldAllowParentAll = request.getParameter("allowParentAll");
                oldAltProductLineCode = request.getParameter("txtAltProductLineCode");
                oldAltFamilyCode = request.getParameter("txtAltFamilyCode");
                //oldFamilyName = request.getParameter("oldFamilyName");
                oldKeywordsIndex = ""; //request.getParameter("iKeywords");
                oldKeywordsPlural = request.getParameter("pKeywords");
                oldKeywordsSingular = request.getParameter("sKeywords");
                oldParentFamilyCode = request.getParameter("txtParentFamilyCode");
                //parentFamilyCode = request.getParameter("parentFamilyCode");
                productLineCode = request.getParameter("productLineCode");
                //productLineName = request.getParameter("productLineName");
                                
                //  Set/Update session vars
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);
                debug (debugLevel, 8, uStamp + " processed form variables.");
            } else {
                response.sendRedirect ("gpsnull.htm");
                return;
            }
        } catch (Exception e){
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
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
        
        // Make sure the Family Code we want to delete did not change somehow
    
        try {
            queryString = "SELECT *";
            queryString += " FROM pub.ps_family";
            queryString += " WHERE family_code = '" + familyCode + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (!rs.next()) {
                    sWork = uStamp + " Error! Family Code " + familyCode + " does not exist!";
                    debug (debugLevel, 0, sWork);
                    request.setAttribute("message", sWork);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    rs.close();
                    conn.closeStatement();
                    conn.close();
                    return;
                } else {
                    allowParentAll = rs.getBoolean("allow_parent_all") ? "Y" : "N";
                    if (!rs.getString("family_name").equals(familyName)
                            ||  rs.getInt("display_order") != displayOrder
                            || !allowParentAll.equals(oldAllowParentAll)
                            || !rs.getString("alt_product_line_code").equals(oldAltProductLineCode)
                            || !rs.getString("alt_family_code").equals(oldAltFamilyCode)
                            || !rs.getString("parent_family_code").equals(oldParentFamilyCode)
                            // || !rs.getString("index_keywords").equals(oldKeywordsIndex)
                            || !rs.getString("keywords_plural").equals(oldKeywordsPlural)
                            || !rs.getString("keywords_singular").equals(oldKeywordsSingular)
                            || !rs.getString("product_line_code").equals(productLineCode) ) {
                        debug (debugLevel, 4, uStamp + " Error! family Code " + familyCode + " original value(s) have changed!");
                        debug (debugLevel, 4, uStamp + " Old Name = " + familyName + "; new Name = " + rs.getString("family_name"));
                        debug (debugLevel, 4, uStamp + " Old Order = " + displayOrder + "; new Order = " + rs.getInt("display_order"));
                        debug (debugLevel, 4, uStamp + " Old Product Line Code = " + productLineCode + "; new Product Line Code = " + rs.getString("product_line_code"));
                        request.setAttribute("message", "Unusual Error - Family Code " + familyCode + " original value(s) have changed!");
                        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        rs.close();
                        conn.closeStatement();
                        conn.close();
                        return;
                    }
                }
                rs.close();
            }
        
            // If we get here, the family code we are deleting has not
            // been modified by anyone else in the meantime and still currently exists.
        
            // Ummmm Note that the Family Code record was NOT locked here.
        
            boolean cancel = false;
            
            // Check for sufamily references
            
            GPSsubfamilyCodes subfamCodes = new GPSsubfamilyCodes();
            rc = subfamCodes.open(conn, familyCode);
            if (rc == subfamCodes.SUBFAMILY_CODES_EMPTY) {
                cancel = false;
            }
            if (rc == subfamCodes.SUBFAMILY_CODES_OK) {
                message = "Cannot delete Family Code " + familyCode + "; One or more Subfamily Codes still exist.";
                cancel = true;
            }
            if (rc < 0 ) {
                message = "Database error " + rc + " when checking Subfamily Code table.";
                cancel = true;
            }
                    
            GPSfamilyCodes famCodes = new GPSfamilyCodes();;
            ArrayList <String> refs;
            
            // check for rules references
            
            if (!cancel) {
                refs = famCodes.getRuleReferences(conn, familyCode);
                if (refs == null) {
                    cancel = true;
                    message = "Cannot delete; Database error when accessing Rules table.";
                } else {
                    if (refs.size() != 0) {
                        cancel = true;
                        message = "Cannot delete when references still exist in Rules table.";
                    }
                }
                refs = null;                  
            }
            
            // check for SelectBox references
            
            if (!cancel) {
                refs = famCodes.getSelectBoxReferences(conn, familyCode);
                if (refs == null) {
                    cancel = true;
                    message = "Cannot delete; Database error when accessing Select Boxes table.";
                } else {
                    if (refs.size() != 0) {
                        cancel = true;
                        message = "Cannot delete when references still exist in Select Boxes table.";
                    }
                }
                refs = null;                  
            }
            
            // check for Part references
            
            if (!cancel) {
                refs = famCodes.getPartReferences(conn, familyCode);
                if (refs == null) {
                    cancel = true;
                    message = "Cannot delete; Database error when accessing Part table.";
                } else {
                    if (refs.size() != 0) {
                        cancel = true;
                        message = "Cannot delete when references still exist in Part table.";
                    }
                }
                refs = null;                  
            }

            if (!cancel) {
                // If there are no active references in database, continue:
                 
                debug (debugLevel, 2, uStamp + " Attempting to delete Family Code " + familyCode);
                SQLCommand = "DELETE FROM pub.ps_family";
                SQLCommand += " WHERE family_code = '" + familyCode + "'";
                debug (debugLevel, 2, uStamp + " SQL Command: " + SQLCommand);
                completedOK = conn.runUpdate(SQLCommand);
                message = "Family Code " + familyCode + " was";
                if (!completedOK) {
                    message += " NOT";
                }
                message += " deleted successfully by " + SERVLET_NAME;
                debug (debugLevel, 2, uStamp + " " + message);
            }
            
            if (!cancel) {
                if (completedOK) {
                    if (index.equals("1")) {
                        queryString = "SELECT *";
                        queryString += " FROM pub.ps_index_entry";
                        queryString += " WHERE family_alias = '" + familyName + "'";
                        queryString += " AND subfamily_alias = '" + subfamilyName + "'";
                        queryString += " AND family_code = '" + familyCode + "'";
                        queryString += " AND subfamily_code = '" + subfamilyCode + "'";
                        queryString += " AND division = 'CP'";
                        rs = conn.runQuery(queryString);
                        completedOK = false;
                        if (rs != null) {
                            if (rs.next()) {
                                completedOK = true;
                                message2 = " Found Index Alias for '" + familyName + " / " + subfamilyName + "' OK";
                                sWork = uStamp + message2;
                                debug (debugLevel, 5, sWork);
                            }
                            rs.close();
                            conn.closeStatement();
                        }
                        if (completedOK) {
                            debug (debugLevel, 4, uStamp + " Attempting to delete Index Alias " + familyName + " / " + subfamilyName
                                + " for family/subfamily " + familyCode + "/" + subfamilyCode);
                            SQLCommand = "DELETE FROM pub.ps_index_entry";
                            SQLCommand += " WHERE family_alias = '" + familyName + "'";
                            SQLCommand += " AND subfamily_alias = '" + subfamilyName + "'";
                            SQLCommand += " AND family_code = '" + familyCode + "'";
                            SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
                            SQLCommand += " AND division = 'CP'";
                            completedOK = conn.runUpdate(SQLCommand);
                            message += "<br />Index '" + familyName + "' was";
                            if (!completedOK) {
                                message += " NOT";
                            }
                            message += " deleted successfully for family Code" + familyCode;
                            debug (debugLevel, 4, uStamp + message);
                        } else {
                            message += "<br />Could not find Index for '" + familyName + "'";
                            sWork = uStamp + message;
                            debug (debugLevel, 0, sWork);
                        }
                    } else {
                        message += "<br />Family was NOT removed from the Index.";
                    }
                }
            }
        
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
            famCodes = new GPSfamilyCodes();
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
            request.setAttribute("lines", lines);
            request.setAttribute("familyCodes", familyCodes);
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsfdf1.jsp");
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
