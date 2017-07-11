package org.homeria.webratioassistant.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.homeria.webratioassistant.plugin.Utilities;
import org.homeria.webratioassistant.registry.Registry;

public class RegistryDialog extends Dialog {
	private static final int WIDTH = 500;
	private static final int HEIGHT = 500;

	public RegistryDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		if (id == IDialogConstants.OK_ID || id == IDialogConstants.CANCEL_ID)
			return null;
		return super.createButton(parent, id, label, defaultButton);
	}

	@Override
	public void create() {
		super.create();
		this.checkRegistryExists();

		Shell shell = this.getShell();
		shell.setMinimumSize(WIDTH, HEIGHT);
		GridLayout shellLayout = new GridLayout();
		shellLayout.marginHeight = 10;
		shellLayout.marginWidth = 10;

		shell.setLayout(shellLayout);
		// Center the window
		Point parentSize = shell.getParent().getSize();
		Point parentLocation = shell.getParent().getLocation();

		int x = parentLocation.x + (parentSize.x - WIDTH) / 2;
		int y = parentLocation.y + (parentSize.y - HEIGHT) / 2;

		shell.setLocation(x, y);

		// Clean the shell previous adding new components
		for (Control control : shell.getChildren()) {
			control.dispose();
		}

		// Create general group and content
		/*	Group generalGroup = new Group(shell, SWT.NONE);
			generalGroup.setText("General");
			FillLayout generalGroupLayout = new FillLayout(SWT.VERTICAL);
			generalGroupLayout.marginHeight = 10;
			generalGroupLayout.marginWidth = 10;
			generalGroup.setLayout(generalGroupLayout);
		*/

		Group generalGroup = new Group(shell, SWT.NONE);
		generalGroup.setText("General");
		generalGroup.setLayout(new GridLayout(1, false));
		GridData generalGroupData = new GridData(SWT.FILL, SWT.FILL, true, false);
		generalGroup.setLayoutData(generalGroupData);

		Label generalLabel = new Label(generalGroup, SWT.LEFT);
		generalLabel.setText("Prueba \n dos");

		// Create pattern group and content
		Group patternGroup = new Group(shell, SWT.NONE);
		patternGroup.setText("Pattern");
		patternGroup.setLayout(new GridLayout(1, false));
		GridData patternGroupData = new GridData(SWT.FILL, SWT.FILL, true, true);
		patternGroupData.heightHint = 200;
		patternGroup.setLayoutData(patternGroupData);

		Combo patternCombo = new Combo(patternGroup, SWT.NONE);

		// TODO add items

		Text text = new Text(patternGroup, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		// text.setText("hola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndoshola\ndos");
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		text.setEditable(false);

		Composite butCompo = new Composite(shell, SWT.NULL);
		butCompo.setLayout(new GridLayout(2, true));
		butCompo.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false));

		Button exportBut = new Button(butCompo, SWT.CENTER);
		exportBut.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		exportBut.setText("Export CSV");

		Button closeBut = new Button(butCompo, SWT.CENTER);
		closeBut.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		closeBut.setText("Close");
		closeBut.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);
				RegistryDialog.this.close();
			}
		});
	}

	private void checkRegistryExists() {
		if (!Registry.getInstance().fileExists()) {
			Utilities.showErrorUIMessage("Registry file is not created yet. Try to generate a pattern first.");
			this.close();
		}
	}

	protected boolean isResizable() {
		return true;
	}
}
