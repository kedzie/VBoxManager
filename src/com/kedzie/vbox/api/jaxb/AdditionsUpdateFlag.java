package com.kedzie.vbox.api.jaxb;

public enum AdditionsUpdateFlag {

	NONE("None"),

	WAIT_FOR_UPDATE_START_ONLY("WaitForUpdateStartOnly");
	private final String value;

	AdditionsUpdateFlag(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static AdditionsUpdateFlag fromValue(String v) {
		for (AdditionsUpdateFlag c : AdditionsUpdateFlag.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
