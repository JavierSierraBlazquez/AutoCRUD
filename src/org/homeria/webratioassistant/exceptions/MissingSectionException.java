package org.homeria.webratioassistant.exceptions;

public class MissingSectionException extends Exception {

	private static final long serialVersionUID = -4984373145553010055L;

	public MissingSectionException(String section, Throwable cause) {
		super("Missing: " + section + ". Check the " + section + " in the pattern definition (XML file selected).");
	}
}
