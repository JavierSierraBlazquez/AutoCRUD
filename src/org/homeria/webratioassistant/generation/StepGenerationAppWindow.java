package org.homeria.webratioassistant.generation;

import javax.xml.transform.TransformerException;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

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
				try {
					if (!StepGenerationAppWindow.this.generate.next())
						StepGenerationAppWindow.this.close();
				} catch (TransformerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		Button fastForward = new Button(composite, 0);
		fastForward.setText("Fast Forward");
		fastForward.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);
				try {
					StepGenerationAppWindow.this.generate.end();
					StepGenerationAppWindow.this.close();
				} catch (TransformerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		return super.createContents(composite);
	}
}