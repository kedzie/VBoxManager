/*
 * Created on 08.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.kobjects.rss;

import java.io.IOException;
import java.io.Reader;

import org.kobjects.xml.XmlReader;

/**
 * @author haustein
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RssReader {

	public static final int TITLE = 0;
	public static final int LINK = 1;
	public static final int DESCRIPTION = 2;
	public static final int DATE = 3;
	public static final int AUTHOR = 4;
	

	XmlReader xr;
	
	public RssReader(Reader reader) throws IOException{
		xr = new XmlReader(reader);
	}
	
	void readText(StringBuffer buf) throws IOException{
		while(xr.next() != XmlReader.END_TAG){
			switch(xr.getType()){
				case XmlReader.TEXT : buf.append(xr.getText()); break;
				case XmlReader.START_TAG: readText(buf); break;	
			}
		}	
	}

	/** Returns null if no further elements */

	public String[] next() throws IOException{
		
		String[] item = new String[5];
		
		while(xr.next() != XmlReader.END_DOCUMENT){
			if(xr.getType() == XmlReader.START_TAG){
				String n = xr.getName().toLowerCase();
				if(n.equals("item")||n.endsWith(":item")){
					while(xr.next() != XmlReader.END_TAG) {
						if(xr.getType() == XmlReader.START_TAG){
							String name = xr.getName().toLowerCase();
							int cut = name.indexOf(":");
							if(cut != -1) 
								name = name.substring(cut+1);
							StringBuffer buf = new StringBuffer();
							readText(buf);
							String text	=buf.toString();
							if(name.equals("title"))
								item[TITLE] = text;
							else if (name.equals("link"))
								item[LINK] = text;
							else if (name.equals("description"))
								item[DESCRIPTION] = text;
							else if (name.equals("date"))
								item[DATE] = text;
							else if (name.equals("author"))
								item[AUTHOR] = text;
						}
					}
					return item;
				}
			}
		}
		return null;
	}
}
