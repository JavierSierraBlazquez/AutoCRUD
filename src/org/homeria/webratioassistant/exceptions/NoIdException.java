package org.homeria.webratioassistant.exceptions;

public class NoIdException extends Exception {
	private static final long serialVersionUID = -3329980720978392140L;

	public NoIdException(String section) {
		super("An 'Id' attribute is needed for each element. Check the " + section + " in the pattern definition.");
	}
}
