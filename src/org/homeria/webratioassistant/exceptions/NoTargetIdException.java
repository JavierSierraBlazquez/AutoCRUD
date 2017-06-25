package org.homeria.webratioassistant.exceptions;

public class NoTargetIdException extends Exception {
	private static final long serialVersionUID = 529557982713625403L;

	public NoTargetIdException(String tagName, String section) {
		super("Missing 'targetId' attribute for " + tagName + " in " + section + " in the pattern definition (XML file selected).");
	}
}
