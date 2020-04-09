package net.ssehub.rightsmanagement;

/**
 * Manages illegal/invalid characters in the path or name.
 * 
 * @author Kunold
 *
 */
public class StringUtils {
    /**
     * Avoids instantiation of utility class.
     */
    private StringUtils() {}

    /**
     * Normalizes the path when used for uploading / downloading files.
     * @param path that needs to be normalized.
     * @return the normalized path.
     */
    public static String normalizePath(String path) {
        StringBuffer normalizedPath = new StringBuffer();
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            
            switch (c) {
            case '\t':
                // falls through
            case ' ':
                normalizedPath.append("%20");                
                break;
            case '&':
                normalizedPath.append("%26");
                break;
            case '@':
                normalizedPath.append("%40");
                break;
            case '~':
                normalizedPath.append("%7e");
                break;
            case '*':
                normalizedPath.append("%2a");
                break;
            case '#':
                normalizedPath.append("%23");
                break;
            case '<':
                normalizedPath.append("%3c");
                break;
            case '>':
                normalizedPath.append("%3e");
                break;
            case '%':
                normalizedPath.append("%25");
                break;
            case '|':
                normalizedPath.append("%7c");
                break;
            case '{':
                normalizedPath.append("%7b");
                break;
            case '}':
                normalizedPath.append("%7d");
                break;
            case '^':
                normalizedPath.append("%5e");
                break;
            case '`':
                normalizedPath.append("%60");
                break;
            default:
                // Add char, if it is not problematic.
                normalizedPath.append(c);
                break;
            }
        }
        
        return normalizedPath.toString();
    }
    
    /**
     * Normalizes the name of a group.
     * @param name that needs to be normalized.
     * @return the normalized name.
     */
    public static String normalizeName(String name) {
        StringBuffer normalizedName = new StringBuffer();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            
            switch (c) {
            case '\t':
                //falls through
            case ' ':
                normalizedName.append('_');
                break;
            default:
                // Add char, if it is not problematic.
                normalizedName.append(c);
                break;
            }
        }
        
        return normalizedName.toString();
    }
    
}
