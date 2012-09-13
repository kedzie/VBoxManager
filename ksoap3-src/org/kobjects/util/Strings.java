package org.kobjects.util;

/**
 * @author haustein
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Strings {

	public static String replace(String src, String what, String by) {
		
		int i0 = src.indexOf(what);
		
		if (i0 == -1) return src;
		
		StringBuffer buf = new StringBuffer(src.substring(0, i0));
		while(true) {
			buf.append(by);
			i0 += what.length();
			int i1 = src.indexOf(what, i0);
			if (i1 == -1) break;
			buf.append(src.substring(i0, i1));
			i0 = i1;
		}	
		
		buf.append(src.substring(i0));
		return buf.toString();
	}

	public static String toAscii(String src){
		StringBuffer buf = new StringBuffer();
		
		for(int i=0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (c <= ' ') buf.append(' ');
			else if (c < 127) buf.append(c);
			else {
				switch(c){
					case '\u00c4': buf.append("Ae"); break;
					case '\u00e4': buf.append("ae"); break;
					case '\u00d6': buf.append("Oe"); break;
					case '\u00f6': buf.append("oe"); break;
					case '\u00dc': buf.append("Ue"); break;
					case '\u00fc': buf.append("ue"); break;
					case '\u00df': buf.append("ss"); break;
					default: buf.append('?');
				}
			}
		}	
		
		return buf.toString();
	}

	public static String fill(String s, int len, char c) {
	    boolean left = len < 0;
	    len = Math.abs(len);
	
	    if (s.length() >= len)
	        return s;
	
	    StringBuffer buf = new StringBuffer();
	    len -= s.length();
	    while (len > 0) {
	        buf.append(c);
	        len--;
	    }
	
	    if (left) {
	        buf.append(s);
	        return buf.toString();
	    }
	    return s + buf.toString();
	}
	
	public static String beautify(String s) {
		StringBuffer buf = new StringBuffer();

		if (s.length() > 0) {
			buf.append(Character.toUpperCase(s.charAt(0)));

			for (int i = 1; i < s.length() - 1; i++) {

				char c = s.charAt(i);
				if (Character.isUpperCase(c)
					&& Character.isLowerCase(s.charAt(i - 1))
					&& Character.isLowerCase(s.charAt(i + 1)))
					buf.append(" ");

				buf.append(c);
			}

			if (s.length() > 1)
				buf.append(s.charAt(s.length() - 1));
		}

		return buf.toString();
	}

	
	public static String lTrim(String s, String chars){
		int i = 0;
		int len = s.length();
		while(i < len 
			&& (chars == null 
				? (s.charAt(i) <= ' ') 
				: (chars.indexOf(s.charAt(i)) != -1))){
			i++;
		}
		return i == 0 ? s : s.substring(i);
	}

	public static String rTrim(String s, String chars){
		int i = s.length()-1;
		while(i >= 0 
			&& (chars == null 
				? (s.charAt(i) <= ' ') 
				: (chars.indexOf(s.charAt(i)) != -1))){
			i--;
		}
		return i == s.length()-1 
			? s 
			: s.substring(0, i+1);
	}

	
}
