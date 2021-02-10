/*
 * GPSparmSearch.java
 *
 * Created on October 4, 2007, 5:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gps.util;

import OEdatabase.WDSconnect;
import java.sql.*;
import java.util.*;
import gps.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author Sauter
 */
public class GPSparmSearch {
    
    private boolean debugSw = false;
    private static final String version = "1.5.00";
        
    /** Creates a new instance of GPSparmSearch */
    public GPSparmSearch() {
    }
    
    public List <String> search(HttpServletRequest request, HttpSession session, 
            WDSconnect conn, String familyCode, String subfamilyCode, 
            String manufacturerCode, boolean inStockOnly, List seqNumMap) {
        
            // LOCAL variables
        
            long available = 0;
            String cookedFieldName = "";
            String cookedFieldValue = "";
            int countTotal = 0;
            int countMatch = 0;
            GPScvt cvt = null;
            String dataType = "";
            int error;
            float floatWork;
            List <String> generatedScript = new ArrayList <String> ();
            boolean goodSoFar;
            int i;
            int index;
            int j;
            int k;
            int m;
            GPSmatchItem matchItem = null;
            ArrayList<GPSmatchItem> matchItems = new ArrayList<GPSmatchItem>();
            int maxPreview = 10;
            float maxWork;
            float minWork;
            String parmLabel;
            float parmScore;
            GPSparmSet parmSet;
            String parmValueCooked;
            String parmValueRaw;
            String partNum;
            String previousValue;
            List <String> previousValues = new ArrayList <String>();
            GPSfieldSet qfs;
            float rankScore;
            String rawFieldName = "";
            String rawFieldValue = "";
            ResultSet rs = null;
            GPSrules[] rules;
            GPSselectBox sb = null;
            String sbName;
            String script;
            String searchValue = "";
            int seqNum = 0;
            String temp = "";
            int totalWeights = 0;

            session.setAttribute("previousValues", previousValues);
            
        try {
            debug ("Looking up Rules for Search Fields.");
            qfs = new GPSfieldSet();
            rules = qfs.getRules(conn, familyCode, subfamilyCode, GPSfieldSet.SEARCH_ORDER);
            debug("Search Field rules are loaded; found rules for " + qfs.count() + " search fields");
            
            // Pre-calc min and max values for each numeric search field
            
            for (i = 0; i < seqNumMap.size() ;  i++) {
                // for each search field in "search by value" GUI page
                seqNum = Integer.parseInt((String)seqNumMap.get(i));
                // seqNum holds the corresponding parm Seq Number (Field Number)
                // for each search field on the GUI page
                // seqNum values are in not necessarily in SEARCH order
                debug("GUI Search field " + i + " maps to Parm SeqNum: " + seqNum);
                cookedFieldName = "inputObject" + Integer.toString(i);
                rawFieldName = "raw" + Integer.toString(i);
                debug("Cooked Object name is " + cookedFieldName);
                debug("Raw Object name is " + rawFieldName);
                // work with rawFieldName value if present
                rawFieldValue = request.getParameter(rawFieldName);
                // otherwise work with cookedFieldName value
                if (rawFieldValue == null) {  
                    debug(rawFieldName + " contains a null value!");
                } else {
                    debug(rawFieldName + " contains: '" + rawFieldValue + "'");
                }
                cookedFieldValue = request.getParameter(cookedFieldName);
                if (cookedFieldValue == null) {
                    debug(cookedFieldName + " contains a null value!");
                } else {
                    debug(cookedFieldName + " contains: '" + cookedFieldValue + "'");
                }
                // work with rawFieldName value if present
                // otherwise work with cookedFieldName value
                searchValue = rawFieldValue;
                if (searchValue == null) {
                    searchValue = cookedFieldValue;
                }
                if (searchValue == null) {
                    searchValue = "";
                }
                searchValue = searchValue.trim();
                previousValue = "    aPreviousValue[f2++] = \"" + searchValue + "\";";
                previousValues.add(previousValue);
                debug("Using search value: '" + searchValue + "'");
                // if it is a non-blank entry we're gonna search on it
                if (searchValue.length() != 0) {
                    // find the parm field rule set that matches this search field
                    for (j = 0; j < qfs.count(); j++) { 
                        k = rules[j].getSeqNum();
                        debug("Field Rules " + j + " has SeqNum " + k);
                        if (k == seqNum) {
                            break;
                        }
                    }
                    // j is index of corresponding field rules
                    dataType = rules[j].getDataType();
                    seqNum = rules[j].getSeqNum();
                    if (dataType.equals("N")) {
                        debug("Numeric search field " + i + " contains '" + searchValue + "'; Field Rules Index is " + j);
                        debug("SeqNum is " + seqNum);
                        debug ("Parm Label is " + rules[j].getParmName());
                        rules[j].setSearchString(searchValue);
                        floatWork = Float.parseFloat(searchValue);
                        rules[j].setSearchValue(floatWork);
                        minWork = (float) rules[j].getSearchMin();
                        minWork = floatWork - (floatWork * minWork / (float) 100.0);
                        rules[j].setSearchMinValue(minWork);
                        maxWork = (float) rules[j].getSearchMax();
                        maxWork = floatWork + (floatWork * maxWork / (float) 100.0);
                        rules[j].setSearchMaxValue(maxWork);
                        debug("Numeric match window Min " + minWork + " and Max " + maxWork + " have been calculated.");
                        sbName = rules[j].getDeSelectBoxName();
                        if (sbName == null) { sbName = "";}
                        debug ("DE Select Box Name is '" + sbName + "'.");
                        if (sbName.length() != 0) {
                            sb = new GPSselectBox();
                            error = sb.open(conn, familyCode, subfamilyCode, sbName);
                            if (error > -1) {
                                rules[j].setDeSelectBox(sb);
                                debug ("I successfully loaded Select Box " + sbName );
                            } else {
                                debug ("Error No. " + error + " while attempting to load Select Box '" + sbName + "'." );
                            }
                            sb = null;
                        }
                    }
                    if (dataType.equals("S")) {
                        rules[j].setSearchString(searchValue);
                        debug("String search field " + i + " contains '" + searchValue + "'; Field Rules Index is " + j);
                        debug("SeqNum is " + seqNum);
                        debug ("Parm Label is " + rules[j].getParmName());
                        sbName = rules[j].getDeSelectBoxName();
                        if (sbName == null) { sbName = "";}
                        debug ("DE Select Box Name is '" + sbName + "'.");
                        if (sbName.length() != 0) {
                            debug ("Attempting to create select box.");
                            sb = new GPSselectBox();
                            error = sb.open(conn, familyCode, subfamilyCode, sbName);
                            if (error > -1) {
                                rules[j].setDeSelectBox(sb);
                                debug ("I successfully loaded Select Box " + sbName );
                            } else {
                                debug ("Error No. " + error + " while attempting to load Select Box '" + sbName + "'." );
                            }
                            sb = null;
                        }
                    }
                    if (dataType.equals("L")) {
                        rules[j].setSearchString(searchValue);
                    }
                    if (dataType.equals("D")) {
                    }
                }
            } // end for (int i = 0; i < seqNumMap.size() ;  i++) {  
            
            String queryString = "SELECT part_num ";
            queryString += " FROM pub.part";
            queryString += " WHERE family_code = '" + familyCode +"'";
            queryString += " AND has_ps_data = '1'";
            if (!subfamilyCode.equals("*")) {
                queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            }
            if (!manufacturerCode.equals("*")) {
                queryString += " AND sales_subcat = '" + manufacturerCode + "'";
            }
            debug("SELECT familyCode is '" + familyCode + "'");
            debug("SELECT subfamilyCode is '" + subfamilyCode + "'");
            debug("SELECT manufacturerCode is '" + manufacturerCode + "'");
            debug(queryString);
            
            rs = conn.runQuery(queryString);
            if (rs == null) {
                System.out.println("A fatal error occured while talking to the database in GPSparmSearch().");
                return null;
            }
            debug("Processing Selected Part Numbers...");
            
            cvt = new GPScvt();
            
            // loop through all the part numbers
            // checking for a match within the match window
            
            while (rs.next()) {
                if (countTotal == 0) {  // first time through?
                    // iterate through searchable items
                    // to calculate totalWeights
                    totalWeights = 0;
                    for (i = 0; i < qfs.count(); i++) { 
                        searchValue = rules[i].getSearchString();
                        if (searchValue != null && !searchValue.equals("")) { // if we're searching on this field
                            //seqNum = rules[i].getSeqNum(); // get Seq Num of field we're matching on
                            dataType = rules[i].getDataType();
                            if (dataType.equals("N")) {
                                totalWeights += rules[i].getSearchWeight();
                            }
                        }
                    }
                    debug("Calculated Total Relative Weights = " + totalWeights);
                }
                countTotal++;
                rankScore = 0;   // init rank for this part num parm set to 0
                goodSoFar = true;
                partNum = rs.getString("part_num");
                debug("Processing PN " + partNum);
                available = GPSpart.getAvailable(conn, partNum);
                debug("In stock and available = " + available);
                
                // Skip this part if instock only and there is no stock
                
                if (inStockOnly && available < 1) {
                    goodSoFar = false;
                }
                if (goodSoFar) {
                    // Get field parm values for this PN
                    parmSet = new GPSparmSet();
                    parmSet.read(conn, partNum);
                    debug("Parm values for " + partNum + " were read successfully.");
                    
                    // Check if this PN matches all search criteria
                
                    String head[] = {"","","","","","","","","",""}; // new String[10];
                    String value[] = {"","","","","","","","","",""}; //new String[10];
                    String rawValue[] = {"","","","","","","","","",""}; //new String[10];
                    
                    m = 0;
                    for (i = 0; i < qfs.count(); i++) { // iterate through searchable items
                        searchValue = rules[i].getSearchString();
                        // if we're searching on this field
                        if (searchValue != null && !searchValue.equals("")) { 
                            seqNum = rules[i].getSeqNum(); // get Seq Num of field we're matching on
                            dataType = rules[i].getDataType();
                            parmLabel = rules[i].getParmName();
                            parmValueRaw = parmSet.getParmValue(seqNum);
                            debug("Parm field " + seqNum + " contains raw value '" + parmValueRaw + "'");
                            if (dataType.equals("N")) {
                                debug("Search Numeric Value is " + rules[i].getSearchValue());
                                debug("Search Window is between " + rules[i].getSearchMinValue()
                                    + " and " + rules[i].getSearchMaxValue());
                                parmScore = GPSparmCheck.numericMatch(parmValueRaw,
                                rules[i].getSearchValue(), rules[i].getSearchMinValue(), 
                                rules[i].getSearchMaxValue(), rules[i].getParmDelimiter(),
                                rules[i].getAllowTilde());
                                debug("Parm field calculated score is " + parmScore);
                                if (parmScore < 0) {
                                    debug("BOO! - No Match within Search Window or Parse error.");
                                    goodSoFar = false;
                                    break;
                                } else {
                                    debug("YAY! - Numeric Value is inside the Search Window.");
                                                                       
                                    // fix rankscore if there was only one searchweight in totalweights  07/21/2007
                                   
                                    if (totalWeights == rules[i].getSearchWeight()) {
                                        rankScore = parmScore;
                                    } else {
                                        rankScore += parmScore * (totalWeights - rules[i].getSearchWeight()) / totalWeights;
                                    }
                                }
                            }
                            if (dataType.equals("S")) {
                                debug("Search Value for String field " + seqNum + " contains '" + searchValue + "'");
                                if (!GPSparmCheck.stringMatch(parmValueRaw,
                                    searchValue, rules[i].getParmDelimiter(), rules[i].getAllowTilde()) ) {
                                    goodSoFar = false;
                                    debug("BOO! - No String field Match or Parse error.");
                                    break;
                                } else {
                                    debug("YAY! - We got a String Match.");
                                }
                            }
                            if (dataType.equals("L")) {
                                debug("Search Value for Logical field " + seqNum + " contains '" + searchValue + "'");
                                if (!parmValueRaw.equals(searchValue)) {
                                    goodSoFar = false;
                                    debug("BOO! - No Logical field Match or Parse error.");
                                    break;
                                } else {
                                    debug("YAY! - We got a Logical Match.");
                                }
                            }
                            if (dataType.equals("D")) {
                            // Not supported yet !!!!!!!!!!!!!!!!!!!!!!!!!!!
                            }
                            // Now that we did whatever tests on this parm field
                            if (goodSoFar) {
                                // add parm data to match set if it has a preview order > -1
                                debug ("Check if I need to add parm data to Preview data set");
                                if (rules[i].getPreviewOrder() > -1) {
                                    if (m < maxPreview) {
                                        head[ m ] = parmLabel;
                                        debug ("heading for match preview item " + m + " is " + parmLabel);
                                        rawValue[ m ] = parmValueRaw;
                                        debug ("Raw Value for match preview item " + m + " is " + parmValueRaw);
                                        parmValueCooked = parmValueRaw;
                                        if (dataType.equals("N")) {
                                            sb = rules[i].getDeSelectBox();
                                            if (sb == null) {
                                                parmValueCooked = cvt.toCooked(parmValueRaw,
                                                    rules[i].getDisplayMultipliers(),
                                                    rules[i].getParmDelimiter(),
                                                    rules[i].getDecShift(),
                                                    rules[i].getAllowDuplicates(),
                                                    rules[i].getAllowTilde(),
                                                    true);
                                            } else {
                                                debug ("Looking up '" + parmValueRaw + "' in Select Box '" + rules[i].getDeSelectBoxName() +"'...");
                                                index = sb.optionValue1IndexOf(parmValueRaw);
                                                if (index > -1 ) {
                                                    parmValueCooked = sb.getOptionText(index);
                                                    debug ("Found '" + parmValueCooked + "'.");
                                                } else {
                                                    debug ("Found No Match!");
                                                }
                                                //sb = null;
                                            }
                                            parmValueCooked += " " +rules[i].getDisplayUnits();
                                        }
                                        if (dataType.equals("S")) {
                                            sb = rules[i].getDeSelectBox();
                                            if (sb != null) {
                                                debug ("Select Box " + rules[i].getDeSelectBoxName() + " exists");
                                                debug ("Looking up '" + parmValueRaw + "' in Select Box '" + rules[i].getDeSelectBoxName() +"'...");
                                                index = sb.optionValue1IndexOf(parmValueRaw);
                                                if (index > -1 ) {
                                                    parmValueCooked = sb.getOptionText(index);
                                                    debug ("Found '" + parmValueCooked + "'.");
                                                } else {
                                                    debug ("Found No Match!");
                                                }
                                                //sb = null;
                                            } else {
                                                debug ("Select Box does not exist.");
                                            }
                                        }
                                        
                                        value[ m ] = parmValueCooked;
                                        debug ("Cooked Value for match preview item " + m + " is " + parmValueCooked);
                                        m++;
                                    }
                                }
                            }
                        } // end if (searchValue != null... {
                    }  // end for (i = 0; i < qfs.count(); i++) {
                    // if goodSoFar is true, calculate final weight and add to sort List
                    if (goodSoFar) {
                        debug("Rank score for this PN parm set is " + rankScore);
                        debug("***************We got a MATCH*************");
                        matchItem = new GPSmatchItem(partNum, rankScore, available, 
                            head[0], value[0], head[1], value[1],
                            head[2], value[2], head[3], value[3]);
                        matchItems.add(matchItem);
                        countMatch++;
                    }   
                    parmSet = null;
                }
                if (!goodSoFar) {
                    debug("No match.");
                }
            }  // end while (rs.next()) {
            rs.close();
            rs = null;
            conn.closeStatement();
            debug("Found " + countMatch + " out of " + countTotal + " candidates.");
            if (countMatch > 1) {
                Collections.sort(matchItems);
                debug ("Items were sorted successfully.");
            }
            if (countMatch > 0) {
                for ( i = 0 ; i < matchItems.size(); i++) {
                    matchItem = matchItems.get(i);
                    if (i == 0) {
                        script = "aHead[0] = \"" + matchItem.getHead1() + "\";";
                        generatedScript.add(script);
                        script = "aHead[1] = \"" + matchItem.getHead2() + "\";";
                        generatedScript.add(script);
                        script = "aHead[2] = \"" + matchItem.getHead3() + "\";";
                        generatedScript.add(script);
                        script = "aHead[3] = \"" + matchItem.getHead4() + "\";";
                        generatedScript.add(script);
                    }
                    script = "aPartNum[f] = \"" + matchItem.getPartNum() + "\";";
                    generatedScript.add(script);
                    script = "aRank[f] = " + matchItem.getRank() + ";";
                    generatedScript.add(script);
                    script = "aQuantity[f] = " + matchItem.getAvailable() + ";";
                    generatedScript.add(script);
                    script = "aParm1[f] = \"" + matchItem.getParm1() + "\";";
                    generatedScript.add(script);
                    script = "aParm2[f] = \"" + matchItem.getParm2() + "\";";
                    generatedScript.add(script);
                    script = "aParm3[f] = \"" + matchItem.getParm3() + "\";";
                    generatedScript.add(script);
                    script = "aParm4[f] = \"" + matchItem.getParm4() + "\";";
                    generatedScript.add(script);
                    generatedScript.add("f++;");
                }
            }
            return generatedScript;
       
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("A fatal error occured while Searching the database.");
            return null;
        }
    }
    
    private void debug (String x) {
        if (debugSw) {
            System.out.println(x);
        }
    }
    
}
