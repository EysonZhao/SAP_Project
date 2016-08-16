package sme.perf.utility;

import java.util.ResourceBundle;

public class PropertyFile {
	
	private static ResourceBundle rb = ResourceBundle.getBundle("application");
	
	public static String getValue(String key){
		return rb.getString(key);
	}
}
