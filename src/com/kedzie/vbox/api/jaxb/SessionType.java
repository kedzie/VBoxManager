package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum SessionType implements Serializable {

	NULL("Null"), WRITE_LOCK("WriteLock"), REMOTE("Remote"), SHARED("Shared");
	private final String value;

	SessionType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public String toString() {
		return value;
	}

	public static SessionType fromValue(String v) {
		for (SessionType c : SessionType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
