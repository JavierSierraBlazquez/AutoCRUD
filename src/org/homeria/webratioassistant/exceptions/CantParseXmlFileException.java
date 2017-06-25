package org.homeria.webratioassistant.exceptions;

public class CantParseXmlFileException extends Exception {

	private static final long serialVersionUID = -747125127562211497L;

	public CantParseXmlFileException(String filePath) {
		super("Can't parse the XML file: " + filePath + ". Review the structure of the XML file.");
	}
}
