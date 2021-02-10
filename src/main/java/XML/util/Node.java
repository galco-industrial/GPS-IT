/*
 * Node.java
 *
 * Created on August 15, 2007, 3:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 *
 *  
 */

package XML.util;

/**
 *
 * @author Sauter
 *
 * 8/17/2007
 *
 */
public class Node {
    
    public final static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    private final String VERSION = "1.6.00";
    
    /** Creates a new instance of Node */
    public Node() {
    }
    
    public static String textNode(String tag, String data) {
        String result = "<error>Error</error>";
        if (tag.length() == 0) {
            return result;
        }
        String begin = tag.substring(0,1);
        if (begin.compareTo("a") < 0) {
            return result;
        }
        if (begin.compareTo("z") > 0) {
            return result;
        }
        
        // Check tagname for valid chars a-z, _, and 0-9   FIX ME!!!!!!!!!!!!!!!1
        
        if (data.length() != 0) {
            result = "<" + tag + ">" + data + "</" + tag + ">";
            // System.out.println(result);
        } else {
            result = "<" + tag + " />";
        }
        return result;
    }
    
    public static String doXMLEntities(String inData) {
        String result = "";
        result = inData.replace("&", "&amp;");
        result = result.replace("<", "&lt;");
        result = result.replace(">", "&gt;");
        result = result.replace("'", "&apos;");
        result = result.replace("\"", "&quot;");
        return result;
    }
 
}