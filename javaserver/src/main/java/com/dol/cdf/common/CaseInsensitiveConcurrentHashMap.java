package com.dol.cdf.common;

import java.util.concurrent.ConcurrentHashMap;

public class CaseInsensitiveConcurrentHashMap extends ConcurrentHashMap<String, String> {

	private static final long serialVersionUID = 1L;

	@Override
	public String put(String key, String value) {
		return super.put(key.toLowerCase(), value);
	}

	// not @Override because that would require the key parameter to be of
	// type Object
	public String get(String key) {
		return super.get(key.toLowerCase());
	}
}