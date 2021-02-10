/*
 * GPSoptionsArray.java
 *
 * Created on July 14, 2008, 2:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gps.util;

import java.util.*;
import OEdatabase.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.0
 *
 */
public class GPSoptionsArray {
    
    private int debugLevel = 0;
    private static final String version = "1.5.00";
     
    private boolean cooked = false;
    private GPScvt cvt = null;
    private String dataType = "";
    private String delimiter = "";
    private boolean delimiterAllowed = false;
    private String displayUnits = "";
    private String familyCode = "";
    private int index = 0;
    private boolean logical = false;
    private boolean numeric = false;
    private ArrayList<String> optionListCooked = new ArrayList<String>();
    private ArrayList<String> optionListRaw = new ArrayList<String>();
    private ArrayList<String> optionList = null;
    private ArrayList<String> optionsList = null;
    private String optionText = "";
    private String optionValue1 = "";
    //private boolean parent = false; // set to true if parent select boxes are allowable
    private String parmName = "*undefined*";
    private GPSrules ruleSet = null;
    private GPSselectBox selectBox = null;
    private String selectBoxName = "";
    private boolean selectBoxExists = false;
    private int seqNum = 0;
    private boolean string = false;
    private String subfamilyCode = "";
    private boolean tildeAllowed = false; // set to true if ranges are allowed
    private boolean tildeFound = false; // set to true if ranges are allowed and at least one tilde was found
    private int tildePosition  = -1;
     
    /**
     * Creates a new instance of GPSoptionsArray
     */
    public GPSoptionsArray() {
    }
    
    /**
     * Creates a new instance of GPSoptionsArray for a given Seq Num
     */
   
    public GPSoptionsArray(GPSrules aRuleSet) {
        ruleSet = aRuleSet;
        dataType = ruleSet.getDataType().toUpperCase();
        logical = dataType.equals("L");
        numeric = dataType.equals("N");
        string = dataType.equals("S");
        seqNum = ruleSet.getSeqNum();
        parmName = ruleSet.getParmName();
        familyCode = ruleSet.getFamilyCode();
        subfamilyCode = ruleSet.getSubfamilyCode();
        if ("NS".contains(dataType) ) {
            tildeAllowed = ruleSet.getAllowTilde();
            delimiter = ruleSet.getParmDelimiter();
            delimiterAllowed = !delimiter.equals("");
            if (ruleSet.getQobject().equals("S") 
                    && ruleSet.getQtextBoxSize() == 0) {
                selectBoxName = ruleSet.getQselectBoxName();
                selectBoxExists = true;
            }
        }
        if (numeric) {
            displayUnits = ruleSet.getDisplayUnits();
        }              
    }
    
    public boolean add(String rawValue) {
        if (!selectBoxExists) {
            if (delimiterAllowed) {
                boolean flag = false;
                String[] rawValues = rawValue.split(delimiter);
                for (int i = 0; i < rawValues.length; i++) {
                    if (tildeAllowed) {
                        tildeFound = rawValues[i].contains("~") | tildeFound;
                    }
                    flag = addElement(rawValues[i]) | flag;
                }
                return flag;
            }
            if (tildeAllowed) {
                tildeFound = rawValue.contains("~") | tildeFound;
            }
            return addElement(rawValue);
        }
        return addElement(rawValue);
    }
    
    public boolean addElement(String rawValue) {
        if (!optionListRaw.contains(rawValue)) {
            optionListRaw.add(rawValue);
            return true;
        }
        return false;
    }
    
    public ArrayList<String> cookedOptionList(WDSconnect conn) {
        String work;
        optionsList = new ArrayList<String>();
        
        try {
                 
            ////////////////////////////////////////////////////////
            // Handle Logical parm fields here                    //
            ////////////////////////////////////////////////////////
            
            if (logical) {
                work = "\"" + seqNum + "\",\"" + parmName + "\",\"\"";
                optionsList.add(work);
                // See if a false value was found
                if (optionListRaw.indexOf("N") >= 0) {
                    work = "\"N\",\"No\"";
                    optionsList.add(work);
                }
                if (optionListRaw.indexOf("Y") >= 0) {
                    work = "\"Y\",\"Yes\"";
                    optionsList.add(work);
                }
                return optionsList;
            }
            
            /////////////////////////////////////////////////////////////////////
            // Process pre-defined Select Boxes for Strings and Numerics here  //
            /////////////////////////////////////////////////////////////////////
            
            if (selectBoxExists) {
                
                // First copy raw values into cooked array as default placeholders
                for (int i = 0; i < optionListRaw.size(); i++) {
                    optionListCooked.add(optionListRaw.get(i));
                }
            
                // Now look up cooked values and also build a new list of
                // raw and cooked select box option strings in the same
                // relative order as the reference select box from the database
            
                selectBox = new GPSselectBox();
                if (selectBox.open(conn, familyCode, subfamilyCode, selectBoxName) > -1) {
                    optionsList = new ArrayList<String>();
                    work = "\"" + seqNum + "\",\"" + parmName + "\",\"" + displayUnits + "\"";
                    optionsList.add(work);
                    for (int i = 0; i < selectBox.size(); i++) {
                        // get each raw option value 1 in database select box order
                        // and if it exists in our optionListRaw
                        // set the corresponding cooked entry and
                        // add an option string to the new array list
                        optionValue1 = selectBox.getOptionValue1(i);
                        index = optionListRaw.indexOf(selectBox.getOptionValue1(i));
                        if (index > -1) {
                            optionText = selectBox.getOptionText(i);
                            optionListCooked.set(index, optionText);
                            work = "\"" + optionValue1 + "\",\"" + optionText + "\"";
                            optionsList.add(work);
                        }
                    }
                    return optionsList;
                }
            }
            
            ////////////////////////////////////////////////////////////
            // Process dynamic numeric text box data here             //
            ////////////////////////////////////////////////////////////
            
            if (numeric && !tildeFound) {
                optionList = new ArrayList<String>();
                debug (4, "Sorting Numeric values in ascending order...");
                int j = 0;
                double dWork = 0;
                while (optionListRaw.size() > 0) {
                    int lowestPointer = 0;
                    String raw = optionListRaw.get(0);
                    double lowestValue = Double.parseDouble(raw);
                    for (int i = 1; i < optionListRaw.size(); i++) {
                        raw = optionListRaw.get(i);
                        dWork = Double.parseDouble(raw);
                        if (dWork < lowestValue) {
                            lowestValue = dWork;
                            lowestPointer = i;
                        }
                    }
                    raw = optionListRaw.remove(lowestPointer);
                    optionList.add(raw);
                }
                optionListRaw = optionList;
                int size = optionList.size();
                debug (4, "Found " + size + " numeric options...");
                work = "\"" + seqNum + "\",\"" + parmName + "\",\"" + displayUnits + "\"";
                optionsList.add(work);
                cvt = new GPScvt();
                String raw1 = "";
                String cooked1 = "";
                               
                // Do we need to build a parent select box?
                
                if (size > 24) {
                    int skip = 1;
                    int k = size;
                    while (k > 24) {
                        k = (int) Math.pow(k, 0.5);
                        skip = skip * k;
                    }

                    String raw2 = "";
                    String cooked2 = "";
                    debug (4, "Parent Select Box will contain around " + k + " options.");
                    debug (4, "Skip value is " + skip);
                    skip--;
                    for (int m = 0; m < size; m++) {
                        raw1 = optionList.get(m);
                        m = m + skip;
                        if (m == size-2) {
                            m++;
                        }
                        if (m >= size) {
                            m = size - 1;
                        }
                        raw2 = optionList.get(m);
                        cooked1 = cvt.toCooked(raw1, ruleSet.getDeMultipliers(), 
                            delimiter, ruleSet.getDecShift(), 
                            ruleSet.getAllowDuplicates(), ruleSet.getAllowTilde(), true);
                        cooked2 = cvt.toCooked(raw2, ruleSet.getDeMultipliers(), 
                            delimiter, ruleSet.getDecShift(), 
                            ruleSet.getAllowDuplicates(), ruleSet.getAllowTilde(), true);
                        work = "\"{[" + raw1 + "]~[" + raw2 + "]}\" , \"" 
                            + cooked1 + " ~ " + cooked2 + "\"";
                        //m++;
                        optionsList.add(work);
                        debug (6, work);
                    }
                } else {
                    // handle the child boxes here
                    for (int m = 0; m < size; m++) {
                        raw1 = optionList.get(m);
                        cooked1 = cvt.toCooked(raw1, ruleSet.getDeMultipliers(), 
                            delimiter, ruleSet.getDecShift(), 
                            ruleSet.getAllowDuplicates(), ruleSet.getAllowTilde(), true);
                        work = "\"" + raw1 + "\",\"" + cooked1 +"\"";
                        optionsList.add(work);
                        debug (6, work);
                    }
                }
                optionList = null;
                return optionsList;
            }
    
            /////////////////////////////////////////////////////////////////////
            // Process everything else here                                    //
            /////////////////////////////////////////////////////////////////////
            
            if (numeric) {
                optionListRaw = sortNumericRanges(optionListRaw);
            }
            if (string) {
                optionListRaw = sortStringRaw(optionListRaw);
            }
            optionsList = new ArrayList<String>();
            work = "\"" + seqNum + "\",\"" + parmName + "\",\"" + displayUnits + "\"";
            optionsList.add(work);
            cvt = new GPScvt();
            for (int m = 0; m < optionListRaw.size(); m++) {
                        String raw1 = optionListRaw.get(m);
                        String cooked1 = raw1;
                        if (numeric) {
                            cooked1 = cvt.toCooked(raw1, ruleSet.getDeMultipliers(), 
                                delimiter, ruleSet.getDecShift(), 
                                ruleSet.getAllowDuplicates(), ruleSet.getAllowTilde(), true);
                        }
                        if (tildeFound) {
                            if (cooked1.contains("~")) {
                                cooked1 = "[" + cooked1 + "]";
                            }
                        }
                        work = "\"" + raw1 + "\",\"" + cooked1 +"\"";
                        optionsList.add(work);
                        debug (6, work);
            }
            return optionsList; // null; // Select Box error occurred
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void debug (int level, String x) {
        if (level <= debugLevel) {
            System.out.println(x);
        }
    }
    
    public String getDisplayUnits() {
        return displayUnits;
    }
        
    public String getParmName() {
        return parmName;
    }
    
    public int getSeqNum() {
        return seqNum;
    }
    
    private String parseRangeHigh(String work) {
        String result = work;
        if (work.startsWith("{[") && work.endsWith("]}") && work.contains("]~[")) {
            result = work.substring(work.indexOf("]~[") + 3, work.indexOf("]}"));
        }
        return result;
    }
    
    private String parseRangeLow(String work) {
        String result = work;
        if (work.startsWith("{[") && work.endsWith("]}") && work.contains("]~[")) {
            result = work.substring(2, work.indexOf("]~["));
        }
        return result;
    }
    
    public void setDisplayUnits(String aDisplayUnits) {
        displayUnits = aDisplayUnits;
    }
    
    public void setFamilyCode(String aFamilyCode) {
        familyCode = aFamilyCode;
    }
    
    public void setParmName(String aParmName) {
        parmName = aParmName;
    }

    public void setSelectBoxName(String aSelectBoxName) {
        selectBoxName = aSelectBoxName;
    }
    
    public void setSeqNum(int aSeqNum) {
        if (aSeqNum > 0 && aSeqNum < 100) {
            seqNum = aSeqNum;
        } else {
            seqNum = -1;
        }
    }
    
    public void setSubfamilyCode(String aSubfamilyCode) {
        subfamilyCode = aSubfamilyCode;
    }
    
    public int size() {
        return optionListRaw.size();
    }
    
    private ArrayList<String> sortNumericRanges(ArrayList<String> ranges) {
        String element = "";
        double itemValue = 0;
        int lowest = -1;
        String lowestElement = "";
        double lowestValue = 0;
                 
        // Normalize the data structure
        for (int i = 0; i < ranges.size(); i++) {
            String range = ranges.get(i);
            if (!range.contains("~")) {
                ranges.set(i, range + "~" + range);
            }
        }
        
        // first sort on high value
        
        ArrayList<String> sortedRanges = new ArrayList<String>();
        while (ranges.size() > 0) {
            element = ranges.get(0);
            lowest = 0;
            lowestElement = element;
            lowestValue = Double.parseDouble(element.substring(element.indexOf("~") + 1));
            for (int i = 1; i < ranges.size(); i++) {
                element = ranges.get(i);
                itemValue = Double.parseDouble(element.substring(element.indexOf("~") + 1));
                if (itemValue < lowestValue) {
                    lowestValue = itemValue;
                    lowest = i;
                    lowestElement = element;
                }
            }
            sortedRanges.add(lowestElement);
            ranges.remove(lowest);
        }
        
        // then sort on low value
        
        ranges = sortedRanges;
        sortedRanges = new ArrayList<String>();
        while (ranges.size() > 0) {
            element = ranges.get(0);
            lowest = 0;
            lowestElement = element;
            lowestValue = Double.parseDouble(element.substring(0, element.indexOf("~")));
            for (int i = 1; i < ranges.size(); i++) {
                element = ranges.get(i);
                itemValue = Double.parseDouble(element.substring(0, element.indexOf("~")));
                if (itemValue < lowestValue) {
                    lowestValue = itemValue;
                    lowest = i;
                    lowestElement = element;
                }
            }
            sortedRanges.add(lowestElement);
            ranges.remove(lowest);
        }
        
        // Unnormalize the data structure
        
        for (int i = 0; i < sortedRanges.size(); i++) {
            String range = sortedRanges.get(i);
            String[] values = range.split("~");
            if (values[0].equals(values[1])) {
                sortedRanges.set(i, values[0]);
            }
        }
        return sortedRanges;
    }
    
    private ArrayList<String> sortStringRaw(ArrayList<String> raw) {
        String element = "";
        int lowest = -1;
        String lowestElement = "";
                      
        ArrayList<String> sortedRaw = new ArrayList<String>();
        while (raw.size() > 0) {
            element = raw.get(0);
            lowest = 0;
            lowestElement = element;
            for (int i = 1; i < raw.size(); i++) {
                element = raw.get(i);
                if (element.compareToIgnoreCase(lowestElement) < 0  ) {
                    lowestElement = element;
                    lowest = i;
                }
            }
            sortedRaw.add(lowestElement);
            raw.remove(lowest);
        }
        return sortedRaw;
    }
}
