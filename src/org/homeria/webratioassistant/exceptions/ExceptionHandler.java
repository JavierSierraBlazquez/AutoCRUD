package org.homeria.webratioassistant.exceptions;

import org.homeria.webratioassistant.plugin.Utilities;

public class ExceptionHandler {

	public static void handle(Exception exception) {
		Utilities.showErrorUIMessage(exception.getMessage());
		Utilities.closePlugin();
	}
}
