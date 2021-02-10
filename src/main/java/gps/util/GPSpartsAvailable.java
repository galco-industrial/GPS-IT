/*
 * GPSpartsAvailable.java
 *
 * Created on August 18, 2008, 5:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gps.util;

import java.util.*;

/**
 *
 * @author Sauter
 */
public class GPSpartsAvailable {
    ArrayList<String> partNums = new ArrayList<String>();
    ArrayList<String> available = new ArrayList<String>();
    
    /** Creates a new instance of GPSpartsAvailable */
    public GPSpartsAvailable() {
    }
    
    public void addPartsAvailable(String aPartNum, int aAvailable) {
        String work = Integer.toString(aAvailable);
        partNums.add(aPartNum);
        available.add(work);
    }
    
    public int getPartsAvailable(String aPartNum) {
        int index = partNums.indexOf(aPartNum);
        if (index > -1) {
            return Integer.parseInt(available.get(index));
        } else {
            return -1;
        }
    }
    
    public int size() {
        return partNums.size();
    }
  
}
