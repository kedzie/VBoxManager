package com.kedzie.vbox.api.jaxb;

public enum NATAliasMode {

	ALIAS_LOG("AliasLog"),

	ALIAS_PROXY_ONLY("AliasProxyOnly"),

	ALIAS_USE_SAME_PORTS("AliasUseSamePorts");
	private final String value;

	NATAliasMode(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static NATAliasMode fromValue(String v) {
		for (NATAliasMode c : NATAliasMode.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
