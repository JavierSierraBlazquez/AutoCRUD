package org.homeria.webratioassistant.exceptions;

public class NoSourceIdException extends Exception {
	private static final long serialVersionUID = 529557982713625403L;

	public NoSourceIdException(String tagName, String section) {
		super("Missing 'sourceId' attribute for " + tagName + " in " + section + " in the pattern definition (XML file selected).");
	}
}
