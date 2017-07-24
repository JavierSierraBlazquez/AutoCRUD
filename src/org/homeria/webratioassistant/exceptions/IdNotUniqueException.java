package org.homeria.webratioassistant.exceptions;

public class IdNotUniqueException extends Exception {
	private static final long serialVersionUID = -398373768189331969L;

	public IdNotUniqueException(String id, String section) {
		super("'Id' values must be unique. Check the id: '" + id + "' in " + section + " in the pattern definition.");
	}

}
