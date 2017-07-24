package org.homeria.webratioassistant.exceptions;

import org.homeria.webratioassistant.webratio.Utilities;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ExceptionHandler implements ErrorHandler {

	public static void handle(Exception exception) {
		Utilities.showErrorUIMessage(exception.getMessage());
		Utilities.closePlugin();
	}

	private String getParseExceptionInfo(SAXParseException spe) {
		String systemId = spe.getSystemId();
		if (systemId == null) {
			systemId = "null";
		}
		String info = " - URI=" + systemId + "\n - Line=" + spe.getLineNumber() + ": " + spe.getMessage();
		return info;
	}

	// The following methods are standard SAX ErrorHandler methods.

	public void warning(SAXParseException spe) throws SAXException {
		System.out.println("Warning:\n" + this.getParseExceptionInfo(spe));
	}

	public void error(SAXParseException spe) throws SAXException {
		String message = "Error:\n" + this.getParseExceptionInfo(spe);
		throw new SAXException(message);
	}

	public void fatalError(SAXParseException spe) throws SAXException {
		String message = "Fatal Error:\n" + this.getParseExceptionInfo(spe);
		throw new SAXException(message);
	}
}
