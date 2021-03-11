package com.valentin.hasm;

public class Utils {
	// Utility methods shared between classes
	
	public static String removeWhiteSpace(String s) {
		// Remove all white space from the string
		char [] charString = s.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (char letter : charString) {
			if (letter != ' ' && letter != '\t')
				sb.append(letter);
		}
		return sb.toString();
	}
	
	public static boolean isInt(String s) {
		// Utility method to check whether the symbol is an integer
		try {
			Integer.parseInt(s);
			return true;
		} catch(NumberFormatException e) {
			// If there was an exception, it's a legal symbol
			return false;
		}
	}
}
