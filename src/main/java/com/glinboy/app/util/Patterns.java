package com.glinboy.app.util;

public final class Patterns {
	
	private Patterns() {}
	
	public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
	
	public static final String SMS_PATTERN = "^\\+(?:[0-9]●?){6,14}[0-9]$";

}
