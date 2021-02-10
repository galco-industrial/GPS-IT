/*
 * GPSopts.java
 *
 * Created on April 3, 2009, 2:11 PM
 *
 * I am used to create parm values to be stored in a filter
 * option list select box.
 *
 * MODIFICATION History
 * 
 * 02/15/2013 DES Added code to support image select box filters for
 *                String Text boxes with 1 or more values separated
 *                by delimiters.
 *
 */

package gps.util;

import java.sql.*;
import java.util.*;
import OEdatabase.*;
import gps.util.*;

/**
 *
 * @author Sauter
 */
public class GPSopts {
    
    private static final String version = "1.7.01";
    
    private String beginPair = "{[";
    private ArrayList<String> childrenCooked = new ArrayList<String>();
    private ArrayList<String> childrenRaw = new ArrayList<String>();
    private int debugLevel = 0;
    private int decShift = 0;
    private boolean delimiterAllowed = false;
    private String displayMultipliers = "";
    private String displayUnits = "";
    private boolean duplicatesAllowed = false;
    private String endPair = "]}";
    private String familyCode = "";
    private boolean foundImage = false;
    private String imagePrefix = "";     /* 02/15/2013 DES */
    private String imageSuffix = ".jpg"; /* 02/15/2013 DES */
    private String imageType = "";       /* 02/15/2013 DES */
    private boolean isImageSelectBox = false;  /* Note this can be set to true for q select boxes
                                                * AND q text boxes as of 02/15/2013 DES */
    private String message = "";
    private String midPair = "]~[";
    private String optionImage = "";
    private ArrayList<String> optionListEntriesCooked = new ArrayList<String>();
    private ArrayList<String> optionListEntriesImages = new ArrayList<String>();
    private ArrayList<String> optionListEntriesRaw = new ArrayList<String>();
    private ArrayList<String> optionListRaw = new ArrayList<String>();
    private ArrayList<String> optionListSorted = null;
    private String parmDelimiter = "";
    private String parmDataType = "";
    private String parmName = "*Undefined*";
    private String ruleScope = "";
    private int seqNum = 0;
    private GPSselectBox selectBox = null;
    private boolean selectBoxExists = false;
    private String selectBoxName = "";
    private String subfamilyCode = "";
    private boolean tildeAllowed = false;
    private boolean tildeFound = false;
    
    /** Creates a new instance of GPSopts */
    public GPSopts() {
    }
    
    public boolean init(WDSconnect conn1, String aFamilyCode, String aSubfamilyCode, int aSeqNum) {
        String qObject = "";
        int qTextBoxSize = 0;
        String queryString = "";
        int rc = 0;
        ResultSet rs = null;
        
        familyCode    = aFamilyCode;
        subfamilyCode = aSubfamilyCode;
        seqNum        = aSeqNum;
        if (subfamilyCode.equals("")) {
            subfamilyCode = "*";
        }
        try {
            queryString = "SELECT allow_duplicates, allow_tilde, data_type, display_multipliers, image_type,"; /* 02/15/2013 DES */
            queryString += " display_units, parm_delimiter, parm_name, rule_scope, q_object, q_select_box_name, q_text_box_size";
            queryString += " FROM pub.ps_rules";
            queryString += " WHERE family_code  = '" + familyCode + "'";
            queryString += " AND seq_num        = " + seqNum;
            queryString += " AND (subfamily_code = '" + subfamilyCode + "' OR subfamily_code = '*')";
            queryString += " ORDER BY rule_scope "; 
            //if (!conn1.disableTransactions()) {
            //    debug (0, "(95) Attempt to disable transactions on conn1 in GPSopts.java failed.");
            //}
            rs = conn1.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    displayMultipliers = rs.getString("display_multipliers");
                    displayUnits       = rs.getString("display_units").trim();
                    duplicatesAllowed  = rs.getBoolean("allow_duplicates");
                    imageType          = rs.getString("image_type");  /* 02/15/2013 DES */
                    parmDataType       = rs.getString("data_type");
                    parmDelimiter      = rs.getString("parm_delimiter");
                    parmName           = rs.getString("parm_name");
                    qObject            = rs.getString("q_object");
                    selectBoxName      = rs.getString("q_select_box_name");
                    qTextBoxSize       = rs.getInt("q_text_box_size");
                    ruleScope          = rs.getString("rule_scope");
                    tildeAllowed       = rs.getBoolean("allow_tilde");
                    //break;
                }
                rs.close();
                rs = null;
            }
            conn1.closeStatement();
        } catch (Exception e) {
            message = " Database error in GPSopts reading ruleset for " 
                           + familyCode + "/" + subfamilyCode + "/" + ruleScope + seqNum;
            debug (0, message);
            e.printStackTrace();
            rs = null;
            conn1.closeStatement();
            //if (!conn1.commit()) {
            //    debug (0, "(126) Attempt to commit transaction on conn1 in GPSopts.java failed.");
            //}
            return false;
        }
        try {
            if (displayUnits.toLowerCase().equals("none")) {
                displayUnits = "";    
            }
            delimiterAllowed = (parmDelimiter.length() > 0 );
            if (displayUnits.length() > 0 ) {
                queryString = "SELECT numeric_base, multiplier_base, multiplier_exp";
                queryString += " FROM pub.ps_units";
                queryString += " WHERE display_units  = '" + displayUnits + "'";
            //if (!conn1.disableTransactions()) {
            //    debug (0, "(140) Attempt to disable transactions on conn1 in GPSopts.java failed.");
            //}
            rs = conn1.runQuery(queryString);
                if (rs != null) {
                    if (rs.next()) {
                        if (rs.getInt("numeric_base") == 10) {
                            if (rs.getInt("multiplier_base") == 10) {
                                decShift = rs.getInt("multiplier_exp");
                            }
                        }
                    }
                    rs.close();
                    rs = null;
                }
                conn1.closeStatement();
                //if (!conn1.commit()) {
                //    debug (0, "(156) Attempt to commit transaction on conn1 in GPSopts.java failed.");
                //}
            }
        } catch (Exception e) {
            message = " Database error in GPSopts finding ps_units " + displayUnits + " for " 
                          + familyCode + "/" + subfamilyCode + "/" + ruleScope + seqNum;
            debug (0, message);
            e.printStackTrace();
            rs = null;
            conn1.closeStatement();
            return false;
        }
        if (selectBoxName == null) {
            selectBoxName = "";
        }
        if (imageType == null) {       /* 02/15/2013 DES */
            imageType = "";            /* 02/15/2013 DES */
        }                              /* 02/15/2013 DES */
        if (qTextBoxSize != 0 || !qObject.equals("S")) {
            selectBoxName = "";
            if (imageType.toUpperCase().equals("ISBF")) {              /* 02/15/2013 DES */
                displayUnits = ".";                                    /* 02/15/2013 DES */
                isImageSelectBox = true;                               /* 02/15/2013 DES */
                imagePrefix = ruleScope.toLowerCase() + seqNum + "-";  /* 02/15/2013 DES */
                if (ruleScope.toUpperCase().equals("L")) {             /* 02/15/2013 DES */
                    imagePrefix = subfamilyCode.toLowerCase() + "/" + imagePrefix; /* 02/15/2013 DES */
                }                                                      /* 02/15/2013 DES */
            }                                                          /* 02/15/2013 DES */
        }
        if (selectBoxName.length() > 0 ) {
            selectBox = new GPSselectBox();
            if (ruleScope.equals("G")) {
                aSubfamilyCode = "*";
            }
            rc = selectBox.open(conn1, familyCode, aSubfamilyCode, selectBoxName);
            if (rc > 0) {
                selectBoxExists = true;
            } else {
                message = "**** Error " + rc + " opening select box for Family/Subfamily/Scope/SeqNum/SelectBoxName: " 
                          + familyCode + "/" + aSubfamilyCode + "/" + ruleScope + "/" + seqNum + "/" + selectBoxName;
                debug (0, message);
                selectBox = null;
                selectBoxName = "";
                selectBoxExists = false;
            }
        }
        debug(4, "**** Created options array for Family/Subfamily/Scope/SeqNum/SelectBoxName: " 
                          + familyCode + "/" + subfamilyCode + "/" + ruleScope + "/" + seqNum + "/" + selectBoxName);
        return true;
    }          

    private boolean addElement(String work) {
        if (!optionListRaw.contains(work)) {
            return optionListRaw.add(work);
        }
        return false;
    }
 
    public boolean addOption(String rawValue) {
        boolean flag = false;
        String[] rawValues;
              
        if (!selectBoxExists) {
            if (delimiterAllowed) {
                rawValues = rawValue.split(parmDelimiter);
                for (int i = 0; i < rawValues.length; i++) {
                    rawValue = rawValues[i];
                    if (tildeAllowed) {
                        tildeFound = tildeFound || rawValue.contains("~");
                    }
                    flag = addElement(rawValue) || flag;
                }
                return flag;
            }
            if (tildeAllowed) {
                tildeFound = tildeFound || rawValue.contains("~");
            }
            return addElement(rawValue);
        }    
        return addElement(rawValue);
    }
        
    public boolean buildChildren(String childRawRange) {
        String beginChild = "";
        String cooked1 = "";
        GPScvt cvt = new GPScvt();
        String endChild = "";
        int i = 0;
        int k = 0;
        String raw1 = "";
        String work = "";
        
        childrenCooked.clear();
        childrenRaw.clear();
        
        // Parse begin and end raw values
        
        if (!childRawRange.startsWith(beginPair)) {
            return false;
        }
        if (!childRawRange.endsWith(endPair)) {
            return false;
        }
        i = childRawRange.indexOf(midPair);
        if (i < 3) {
            return false;
        }
        beginChild = childRawRange.substring(2, i);
        endChild = childRawRange.substring(i + 3, childRawRange.length() - 2);
        
        // Locate beginning child Value
        
        for (i = 0; i < optionListSorted.size(); i++) {
            if (beginChild.equals(optionListSorted.get(i))) {
                break;
            }
        }
        
        /* CREATE HEADER Entry here
        childrenRaw.add(parmName);
        childrenCooked.add(displayUnits);
        */
                
        while (i < optionListSorted.size()) {
            raw1 = optionListSorted.get(i);
            cooked1 = cvt.toCooked(raw1, displayMultipliers, 
                        parmDelimiter, decShift, 
                        duplicatesAllowed, tildeAllowed, true);
            childrenRaw.add(raw1);
            childrenCooked.add(cooked1);
            if (raw1.equals(endChild)) {
                break;
            }
            i++;
        }
        if (childrenRaw.size() < 2) {
            return false;
        }
        return true;
    }

    public void cookOptionList() {
        String cooked1 = "";
        String cooked2 = "";
        GPScvt cvt = null;
        double dWork = 0.0;
        int i = 0;
        int iwork = 0;
        int j = 0;
        int k = 0;
        int lowestPointer = 0;
        double lowestValue;
        int m = 0;
        String optionText = "";
        String optionValue1 = "";              
        String raw1 = "";
        String raw2 = "";
        boolean selectBoxLocal = false;
        String selectBoxSFC = "";
        int size = 0;
        int skipp = 1;
        String work = "";
        if (!selectBoxExists && parmDataType.equals("N") ) {
            cvt = new GPScvt();
        }
        /*************************************************************
        *  Handle Logical parm fields here                           *
        *************************************************************/
        if (parmDataType.equals("L") ) {
            //work = "\"" + seqNum + "\",\"" + parmName + "\",\"\"";
            //optionListEntries.add(work);  
            for (i = 0; i < optionListRaw.size(); i++) {
                raw1 = optionListRaw.get(i);
                if (raw1.equals("N") ) {
                    //work = "\"N\",\"No\"";
                    //optionListEntries.add(work);
                    optionListEntriesRaw.add("N");
                    optionListEntriesCooked.add("No");
                }
                if (raw1.equals("Y") ) {
                    //work = "\"Y\",\"Yes\"";
                    //optionListEntries.add(work);
                    optionListEntriesRaw.add("Y");
                    optionListEntriesCooked.add("Yes");
                }
            }
            debug (4, "L Options Cooker processed " + (optionListEntriesRaw.size() - 1) + " options for " + parmName); 
            return;
        }
        /********************************************************************
        *  Process pre-defined Select Boxes for Strings and Numerics here   *
        ********************************************************************/
        if (selectBoxExists) {
            //work = "\"" + seqNum + "\",\"" + parmName + "\",\"" + displayUnits + "\"";
            //optionListEntries.add(work);
            foundImage = false;
            isImageSelectBox = selectBox.getImageCount() > 0;
            if (isImageSelectBox) {
                isImageSelectBox = selectBox.showImages();
            }
            if (isImageSelectBox) {
                displayUnits = ".";
                selectBoxLocal = !selectBox.getSubfamilyCode().equals("*");
                if (selectBoxLocal) {
                    selectBoxSFC = selectBox.getSubfamilyCode().toLowerCase() + "/";
                } else {
                    selectBoxSFC = "";
                }
            }
            size = selectBox.size();
            for (i = 0; i <= size; i++) {
                    /* get each raw option value 1 in database select box order
                       and if it exists in our option-List-Raw array
                       create the corresponding option list entry */
                optionValue1 = selectBox.getOptionValue1(i);
                iwork        = optionListRaw.indexOf(optionValue1);
                if (iwork > -1) {
                    optionText = selectBox.getOptionText(i);
                    //work       = "\"" + optionValue1 + "\",\"" + optionText + "\"";
                    optionListRaw.set(iwork, "");
                    //optionListEntries.add(work);
                    optionListEntriesRaw.add(optionValue1);
                    optionListEntriesCooked.add(optionText);
                    if (isImageSelectBox) {
                        optionImage = selectBox.getOptionImage(i).trim();
                        if (!optionImage.equals("")) {
                            optionImage = selectBoxSFC + optionImage;
                            foundImage = true;
                        }
                        optionListEntriesImages.add(optionImage);
                    }
                }
            }
            isImageSelectBox = foundImage;
            if (optionListRaw.size() > optionListEntriesRaw.size() - 1) {
                /* Did we have any uncooked values left over?? */
                size = optionListRaw.size();
                for (i = 0; i < size; i++) {
                    if (!optionListRaw.get(i).equals("") ) {
                        message = "Could not resolve raw value '" + optionListRaw.get(i) + "' for family/subfamily/selectbox: "
                                    + familyCode + "/" + subfamilyCode + "/" + selectBoxName;
                        debug (0, message);
                    }
                }
            }
            debug (4, "SB Options Cooker processed " + (optionListRaw.size() - 1) + " options for " + parmName);
            return;   /* RETURN EARLY here */
        }
        /********************************************************************
        * Process dynamic numeric text box data here (No ranges)            *
        ********************************************************************/
        if (parmDataType.equals("N") && !tildeFound) {
            debug (4, "Sorting Numeric values for " + parmName + " in ascending order.");
            optionListSorted = new ArrayList<String>();
            while (optionListRaw.size() > 0) {
                lowestPointer = 0;
                raw1 = optionListRaw.get(0);

                lowestValue = Double.parseDouble(raw1);
                for (i = 1; i < optionListRaw.size(); i++) {
                     raw1 = optionListRaw.get(i);
                /* Temp Debug 
                if (raw1.equals("BLACK")) {
                    debug(0, "parmDataType = '" + parmDataType + "', tildeFound = '" + tildeFound + "'.");
                    debug(0, "parmName = '" + parmName + "', selectBoxName = '" + selectBoxName + "'.");
                    debug(0, "seqNum = '" + seqNum + "', selectBoxName = '" + selectBoxName + "'.");
                    debug(0, "familyCode = '" + familyCode + "', subfamilyCode = '" + subfamilyCode + "'.");
                }
                End debug  */
                     dWork = Double.parseDouble(raw1);
                     if (dWork < lowestValue) {
                         lowestValue = dWork;
                         lowestPointer = i;
                     }
                }
                raw1 = optionListRaw.remove(lowestPointer);
                optionListSorted.add(raw1);
            }
            size = optionListSorted.size();
            debug(4, "Sorting complete; building options for " + parmName);
            debug(4, "Found " + size + " numeric options...");
            //work = "\"" + seqNum + "\",\"" + parmName + "\",\"" + displayUnits + "\"";
            //optionListEntries.add(work);
            /* Do we need to build a parent select box? */ 
            if (size > 24 ) {
                k = size;
                //while (k > 24) {
                    k = (int) Math.pow(k, 0.5);
                    skipp = skipp * k;
                //}
                skipp--;
                //raw2 = optionListSorted.get(0);
                //for (m = 0; m < size; m++) {
                for (m = 1; m < size;) {
                    raw1 = optionListSorted.get(m - 1);
                    m = m + skipp;
                    if (m > size - 2) {
                        m = size;
                    }
                    raw2 = optionListSorted.get(m - 1);
                    cooked1 = cvt.toCooked(raw1, displayMultipliers, 
                        parmDelimiter, decShift, 
                        duplicatesAllowed, tildeAllowed, true);
                    cooked2 = cvt.toCooked(raw2, displayMultipliers, 
                        parmDelimiter, decShift, 
                        duplicatesAllowed, tildeAllowed, true);
                    work = beginPair + raw1 + midPair + raw2 + endPair;
                    optionListEntriesRaw.add(work);
                    // work = cooked1 + " ~ " + cooked2;  // Deleted 4/21/2010  DES
                    work = cooked1 + " to " + cooked2; // Added 4/21/2010  DES
                    debug (10, work);
                    optionListEntriesCooked.add(work);
                }
            }
            else {
                // handle the child boxes here
                for (m = 0; m < size; m++) {
                    raw1 = optionListSorted.get(m);
                    cooked1 = cvt.toCooked(raw1, displayMultipliers, 
                        parmDelimiter, decShift, 
                        duplicatesAllowed, tildeAllowed, true);
                    //work = "\"" + raw1 + "\",\"" + cooked1 + "\"";
                    //optionListEntries.add(work); 
                    optionListEntriesRaw.add(raw1);
                    optionListEntriesCooked.add(cooked1);
                }
            }
            debug(4, "N Options Cooker processed " + optionListSorted.size() + " options for " + parmName);
            return;
        }
                
        /////////////////////////////////////////////////////////////////////
        // Process everything else here                                    //
        /////////////////////////////////////////////////////////////////////
            
        if (parmDataType.equals("N")) {
            optionListRaw = sortNumericRanges(optionListRaw);
        }
        if (parmDataType.equals("S")) {
            optionListRaw = sortStringRaw(optionListRaw);
        }
        size = optionListRaw.size();
        for (m = 0; m < size; m++) {
             raw1 = optionListRaw.get(m);
             cooked1 = raw1;
             if (parmDataType.equals("N")) {
                 cooked1 = cvt.toCooked(raw1, displayMultipliers, 
                        parmDelimiter, decShift, 
                        duplicatesAllowed, tildeAllowed, true);
             }
             if (tildeFound) {
                 if (cooked1.contains("~")) {
                     cooked1 = cooked1.replace("~", " to ");  // Added 4/21/2010  DES
                     cooked1 = "[" + cooked1 + "]";
                     debug (8, cooked1);
                 }
             }
             optionListEntriesRaw.add(raw1);
             optionListEntriesCooked.add(cooked1);
             if (isImageSelectBox) {                                                      /* 02/15/2013 DES */
                 optionImage = raw1.trim();                                               /* 02/15/2013 DES */
                 optionImage = optionImage.replace(" ", "_");                             /* 02/15/2013 DES */
                 optionImage = optionImage.replace("/", "_");                             /* 02/15/2013 DES */
                 optionImage = optionImage.replace("\\", "_");                            /* 02/15/2013 DES */
                 optionImage = optionImage.toLowerCase();                                 /* 02/15/2013 DES */
                 optionListEntriesImages.add(imagePrefix + optionImage + imageSuffix);    /* 02/15/2013 DES */
             }                                                                            /* 02/15/2013 DES */
        }
        cvt = null;
        return; 
    }
         
    private void debug (int level, String x) {
        if (debugLevel >= level) {
            System.out.println(x);
        }
    }
    
    public String getChildCooked(int i) {
        if (i < 0 || i >= childrenCooked.size()) {
            return null;
        }
        return childrenCooked.get(i);
    }
    
    public String getChildRaw(int i) {
        if (i < 0 || i >= childrenRaw.size()) {
            return null;
        }
        return childrenRaw.get(i);
    }
    
    public int getChildrenSize() {
        return childrenRaw.size();
    }
    
    public String getDataType() {
        return parmDataType;
    }

    public boolean getDelimiterAllowed() {
        return delimiterAllowed;
    }

    public String getDisplayUnits() {
        return displayUnits;
    }
    
    public String getFamilyCode() {
        return familyCode;
    }
    
    public boolean getIsImageSelectBox() {
        return isImageSelectBox;
    }

    public int getListEntriesRawSize() {
        return optionListEntriesRaw.size();
    }

    public String getListEntryCooked(int i) {
        if (i < 0 || i >= optionListEntriesCooked.size()) {
            return null;
        }
        return optionListEntriesCooked.get(i);
    }
    
    public String getListEntryImage(int i) {
        if (i < 0 || i >= optionListEntriesImages.size()) {
            return null;
        }
        return optionListEntriesImages.get(i);
    }
   
    public String getListEntryRaw(int i) {
        if (i < 0 || i >= optionListEntriesRaw.size()) {
            return null;
        }
        return optionListEntriesRaw.get(i);
    }
    
    public int getOptionListSize() {
        return optionListRaw.size();
    }
    
    public String getOptionListValue(int i) {
        if (i < 0 || i >= optionListRaw.size()) {
            return null;
        }
        return optionListRaw.get(i);
    }
    
    private int getOptionListRawIndex(String work) {
        return optionListRaw.indexOf(work);
    }

    public String getParmName() {
        return parmName;
    }
    
    public String getRuleScope() {
        return ruleScope.toUpperCase();
    }
        
    public String getSelectBoxName() {
        return selectBoxName;
    }

    public int getSeqNum() {
        return seqNum;
    }
    
    public String getSubfamilyCode() {
        return subfamilyCode;
    }
    
    public boolean getTildeAllowed() {
        return tildeAllowed;
    }
    
    private String parseRangeHigh(String work) {
        
        int ptr = 0;
        String resultt = "";

        resultt = work;
        ptr     = work.indexOf(midPair);
        if (ptr > 1 && work.startsWith(beginPair) && work.endsWith(endPair) ) {
            resultt = work.substring(ptr + 3, work.indexOf(endPair));
        }
        return resultt;       
    }
    
    private String parseRangeLow(String work) {
        String resultt = "";
        int ptr = 0;

        resultt = work;
        ptr     = work.indexOf(midPair);
        if (ptr > 1 && work.startsWith(beginPair) && work.endsWith(endPair) ) {
            resultt = work.substring(2, ptr);
        }
        return resultt;
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
