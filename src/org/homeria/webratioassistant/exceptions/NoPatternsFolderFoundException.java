package org.homeria.webratioassistant.exceptions;

public class NoPatternsFolderFoundException extends Exception {

	private static final long serialVersionUID = 2819026587756669276L;

	public NoPatternsFolderFoundException(String path) {
		super("You need to create the directory 'patterns' in your Web Proyect folder.\n (" + path + ")");
	}
}
