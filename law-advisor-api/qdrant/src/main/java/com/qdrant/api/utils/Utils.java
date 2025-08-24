package com.qdrant.api.utils;

public class Utils {

	public static String cleanBlock(String pageContent) {
	    if (pageContent == null) {
	        return "";
	    }

	    return pageContent
	            .replaceAll("\\s+", " ")
	            .replaceAll(" ", " ")
	            .replaceAll("•", "-")
	            .trim();
	}
}
