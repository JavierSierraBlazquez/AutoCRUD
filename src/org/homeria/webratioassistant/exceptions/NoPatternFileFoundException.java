package org.homeria.webratioassistant.exceptions;

public class NoPatternFileFoundException extends Exception {

	private static final long serialVersionUID = 7826870590576594598L;

	public NoPatternFileFoundException(String path) {
		super("No pattern file found in " + path);
	}
}
