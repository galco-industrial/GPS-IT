/*
 * GPSfieldSet.java
 *
 * Created on November 9, 2006, 5:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gps.util;


import OEdatabase.WDSconnect;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 */
public class GPSfieldSet {
    
    private boolean debugSw = false;
    private static final String version = "1.5.00";
    
    private final int ARRAY_SIZE = 100;
    private boolean isValid = false;
    private int [] order = new int[ARRAY_SIZE];
    private GPSrules rules[] = new GPSrules[ARRAY_SIZE];
    private GPSrules sortedRules[] = new GPSrules[ARRAY_SIZE];
    private int size = 0;
    // Constants
    public static final String DATA_ENTRY_ORDER = "DE";
    public static final String DISPLAY_ORDER = "D";
    public static final String MATCH_ORDER = "M";
    public static final String PREVIEW_ORDER = "P";
    public static final String SEARCH_ORDER = "S";
    public static final String SEQUENCE_NUMBER_ORDER = "SN";

    /**
     * Creates a new instance of GPSfieldSet
     */
    public GPSfieldSet() {
    }
    
    private void debug (String x) {
        if (debugSw) {
            System.out.println(x);
        }
    }
    
    public GPSrules[] getRules(WDSconnect conn, String aFamilyCode, String aSubfamilyCode, 
        boolean seqNumOrder) {
        
        // This method is deprecated and should go away
        
        String sortOrder;
        if (seqNumOrder) {
            sortOrder = "SN";
        } else {
            sortOrder = "DE";
        }
        return getRules(conn, aFamilyCode, aSubfamilyCode, sortOrder);    
    }
    
    /** I return an array of rules sets for each parametric field in a
     * family/subfamily. Local rules with the same seq number as a global rule
     * will override the global rule. 
     * For numeric fields, I look up and set the decimal shift value in the ruleset.
     * The rulesets are returned in the order requested.
     */
    public GPSrules[] getRules(WDSconnect conn, String aFamilyCode, String aSubfamilyCode, 
        String aSortOrder) {
        int currSeqNum = 0;
        boolean deOrder = false;
        boolean displayOrder = false;
        boolean matchOrder = false;
        boolean previewOrder = false;
        int prevSeqNum = -1;
        ResultSet rs = null;
        boolean searchOrder = false;
        boolean seqNumOrder = false;
        GPSunit units = null;
        
        seqNumOrder = true;  // Set this as the default
                
        if (aSortOrder.equals("SN")) {
            seqNumOrder = true;
        }
        if (aSortOrder.equals("DE")) {
            deOrder = true;
            seqNumOrder = false;
        }
        if (aSortOrder.equals("D")) {
            displayOrder = true;
            seqNumOrder = false;
        }
        if (aSortOrder.equals("M")) {
            matchOrder = true;
            seqNumOrder = false;
        }
        if (aSortOrder.equals("P")) {
            previewOrder = true;
            seqNumOrder = false;
        }
        if (aSortOrder.equals("S")) {
            searchOrder = true;
            seqNumOrder = false;
        }
        
        try {
            String queryString = "SELECT * "; 
            queryString += " FROM pub.ps_rules";
            queryString += " WHERE family_code = '" + aFamilyCode +"' ";
            queryString += " AND (subfamily_code = '" + aSubfamilyCode + "' OR subfamily_code = '*') ";
            if (displayOrder) {
                queryString += " AND display_order <> 0";
            }
            if (searchOrder) {
                queryString += " AND search_order <> 0";
            }
            if (matchOrder) {
                queryString += " AND match_order <> 0";
            }
            if (previewOrder) {
                queryString += " AND preview_order <> 0";
            }
            queryString += " ORDER BY seq_num, rule_scope DESC";
            rs = conn.runQuery(queryString);
            size = 0;            
            if (rs != null) {
                while(rs.next()) {
                    currSeqNum = rs.getInt("seq_num");
                    if (currSeqNum != prevSeqNum) {
                        rules[size] = new GPSrules(); // create a brand new rules object for this field
                        if (!rules[size].setProperties(rs) ) {
                            System.out.println("Rules Object creation failed for " + aFamilyCode + " - " + aSubfamilyCode);
                            if (rs != null) {
                                rs.close();
                                rs = null;
                                conn.closeStatement();
                            }
                            return null;
                        }
                        // Set Decimal Shift if a numeric field
                        if (rules[size].getDataType().equals("N") ) {
                            units = new GPSunit();
                            units.open(conn, rules[size].getDisplayUnits());
                            rules[size].setDecShift(units.getMultiplierExp());
                            units = null;  // release units object
                        }
                        size++;
                        prevSeqNum = currSeqNum;
                    }
                }
                isValid = size > 0;
                rs.close();
                rs = null;
                conn.closeStatement();
            }
            
            if (seqNumOrder) {
                return rules;
            }
                        
            // If sorting was required I continue here
            
            if (deOrder) {
                for (int ruleNo = 0 ; ruleNo < size ; ruleNo ++) {
                    order [ruleNo] = rules[ruleNo].getDeOrder();
                }
            }
            
            if (displayOrder) {
                for (int ruleNo = 0 ; ruleNo < size ; ruleNo ++) {
                    order [ruleNo] = rules[ruleNo].getDisplayOrder();
                }
            }
            
            if (previewOrder) {
                for (int ruleNo = 0 ; ruleNo < size ; ruleNo ++) {
                    order [ruleNo] = rules[ruleNo].getPreviewOrder();
                }
            }
            
            if (searchOrder) {
            debug ("extracting search order values...");
                for (int ruleNo = 0 ; ruleNo < size ; ruleNo ++) {
                    order [ruleNo] = rules[ruleNo].getSearchOrder();
                     debug ("Rule at index " + ruleNo + " has Search order " + order [ruleNo]); 
                }
            debug ("extract complete.");
            }
            
            if (matchOrder) {
                for (int ruleNo = 0 ; ruleNo < size ; ruleNo ++) {
                    order [ruleNo] = rules[ruleNo].getMatchOrder();
                }
            }
            
            // Now I do the sort
   
            int lowestOrderSoFar;
            int lowestIndexFound = -1;
            int ruleIndex = 0;
            int count = 0;
            // Outer loop "count" iterates once for each rule in the array.
            // after each iteration, lowestIndex is the sorted Index of the next lowest rule
            // if two or more rules have the same order number, 
            // they will be removed in the order they were found (i.e., by field seqnum). 
            debug ("*** Sorting... ***");    
            for (count = 0; count < size; count++) {
                lowestOrderSoFar = 9999;  // Initially set to a super high value
                for (ruleIndex = 0; ruleIndex < size; ruleIndex++) {
                    if (order [ ruleIndex ] < lowestOrderSoFar) {
                        lowestOrderSoFar = order [ ruleIndex];
                        lowestIndexFound = ruleIndex;
                    }
                } // after loop "index" completes, lowestIndexFound has been determined
                order [lowestIndexFound ] = 9999; // this rule will not be selected again
                sortedRules [count] = rules [lowestIndexFound];
                debug ("*******lowest Order found was " + lowestOrderSoFar);
                debug ("*******from Rule at index " + lowestIndexFound);
            }
            debug ("*** Sort Complete ***");
            return sortedRules;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }    
    }
    
    public GPSrules[] getRulesInSearchOrder(WDSconnect conn, String aFamilyCode, String aSubfamilyCode) {
                        
        // This method is deprecated and should go away

        return getRules(conn, aFamilyCode, aSubfamilyCode, "S");    
    }    
    
    public int count() {
        return size;
    }
    
    public int size() {
        return size;
    }

}
