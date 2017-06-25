package org.homeria.webratioassistant.exceptions;

public class CantOpenFileException extends Exception {

	private static final long serialVersionUID = -4984373145553010055L;

	public CantOpenFileException(String filePath) {
		super("Can't open this file: " + filePath + ". Check file read permissions.");
	}
}
