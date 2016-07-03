package controller.util;

public class StringUtils {
	
	public static final int INDEX_NOT_FOUND = -1;
	
	public static boolean isEmpty(String str) {
         return str == null || str.length() == 0;
     }

	 public static String strip(String str, String stripChars) {
         if (isEmpty(str)) {
             return str;
         }
         str = stripStart(str, stripChars);
         return stripEnd(str, stripChars);
     }

     public static String stripStart(String str, String stripChars) {
         int strLen;
         if (str == null || (strLen = str.length()) == 0) {
             return str;
         }
         int start = 0;
         if (stripChars == null) {
             while ((start != strLen) && Character.isWhitespace(str.charAt(start))) {
                 start++;
             }
         } else if (stripChars.length() == 0) {
             return str;
         } else {
             while ((start != strLen) && (stripChars.indexOf(str.charAt(start)) != INDEX_NOT_FOUND)) {
                 start++;
             }
         }
         return str.substring(start);
     }

     public static String stripEnd(String str, String stripChars) {
         int end;
         if (str == null || (end = str.length()) == 0) {
             return str;
         }
 
         if (stripChars == null) {
             while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
                 end--;
             }
         } else if (stripChars.length() == 0) {
             return str;
         } else {
             while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != INDEX_NOT_FOUND)) {
                 end--;
             }
		 }
		 return str.substring(0, end);
     }
}
