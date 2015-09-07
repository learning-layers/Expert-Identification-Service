/**
 * 
 */
package i5.las2peer.services.servicePackage.utils;

/**
 * @author sathvik
 *
 */
public class TextUtils {
    public static boolean isEmpty(String text) {
	if (text != null && text.length() > 0) {
	    return false;
	}
	return true;
    }
}

