

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for DragAndDropAction.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="DragAndDropAction">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Ignore"/>
 *     &lt;enumeration value="Copy"/>
 *     &lt;enumeration value="Move"/>
 *     &lt;enumeration value="Link"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum DragAndDropAction {

	IGNORE("Ignore"),
	COPY("Copy"),
	MOVE("Move"),
	LINK("Link");
	private final String value;

	DragAndDropAction(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static DragAndDropAction fromValue(String v) {
		for (DragAndDropAction c : DragAndDropAction.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
