package org.homeria.webratioassistant.exceptions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.plugin.Utilities;

public class ExceptionHandler {

	public static void handle(Exception exception) {
		MessageBox messageBox = new MessageBox(ProjectParameters.getShell(), SWT.ICON_ERROR);
		messageBox.setText("Error");
		messageBox.setMessage(exception.getMessage());

		messageBox.open();

		Utilities.closePlugin();
	}
}
