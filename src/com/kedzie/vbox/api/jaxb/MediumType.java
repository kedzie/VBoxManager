package com.kedzie.vbox.api.jaxb;

public enum MediumType {

	NORMAL("Normal"),

	IMMUTABLE("Immutable"),

	WRITETHROUGH("Writethrough"),

	SHAREABLE("Shareable"),

	READONLY("Readonly"),

	MULTI_ATTACH("MultiAttach");
	private final String value;

	MediumType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static MediumType fromValue(String v) {
		for (MediumType c : MediumType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
