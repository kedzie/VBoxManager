package org.kobjects.pim;

/**
 * @author haustein
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class VCard extends PimItem {

	public VCard() {
	   }


	public VCard(VCard orig) {
		super(orig);
	   }

	
	public String getType() {
		return "vcard";
	}
	/**
	 * @see org.kobjects.pim.PimItem#getArraySize(java.lang.String)
	 */
	public int getArraySize(String name) {
		if (name.equals("n")) return 5;
		else if (name.equals("adr")) return 6;
		return -1;
	}
}
