package org.homeria.webratioassistant.wizards;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.homeria.webratioassistant.generation.Generate;

public class StepGenerationAppWindow extends ApplicationWindow {

	Generate generate;

	public StepGenerationAppWindow(Shell parentShell, Generate generate) {
		super(parentShell);
		this.generate = generate;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		FillLayout compositeLayout = new FillLayout(SWT.HORIZONTAL);
		composite.setLayout(compositeLayout);

		Button next = new Button(composite, 0);
		next.setText("Next Step");
		next.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);
				if(!StepGenerationAppWindow.this.generate.next())
					StepGenerationAppWindow.this.close();
			}
		});

		Button fastForward = new Button(composite, 0);
		fastForward.setText("Fast Forward");
		fastForward.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);
				StepGenerationAppWindow.this.generate.end();
				StepGenerationAppWindow.this.close();
			}
		});

		return super.createContents(composite);
	}
}
