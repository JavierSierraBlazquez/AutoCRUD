package org.homeria.webratioassistant.registry;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.homeria.webratioassistant.exceptions.ExceptionHandler;
import org.homeria.webratioassistant.webratio.Utilities;
import org.xml.sax.SAXException;

public class RegistryDialog extends Dialog {
	private static final int WIDTH = 500;
	private static final int HEIGHT = 500;

	SortedMap<String, PatternRegisteredPOJO> pattDataMap;

	Combo pattCombo;
	Text pattText;
	String allOutputText;

	int svSummary;
	int elemSummary;

	public RegistryDialog(Shell parentShell) {
		super(parentShell);

		this.svSummary = 0;
		this.elemSummary = 0;

	}

	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		if (id == IDialogConstants.OK_ID || id == IDialogConstants.CANCEL_ID)
			return null;
		return super.createButton(parent, id, label, defaultButton);
	}

	protected boolean isResizable() {
		return true;
	}

	@Override
	public void create() {
		super.create();
		this.checkRegistryExists();
		try {
			this.pattDataMap = Registry.getInstance().getAllData();
		} catch (SAXException e) {
			ExceptionHandler.handle(e);
		} catch (IOException e) {
			ExceptionHandler.handle(e);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

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

		// General (top) group

		Group generalGroup = new Group(shell, SWT.NONE);
		generalGroup.setText("General");
		generalGroup.setLayout(new GridLayout(1, false));
		GridData generalGroupData = new GridData(SWT.FILL, SWT.FILL, true, false);
		generalGroup.setLayoutData(generalGroupData);

		Label generalLabel = new Label(generalGroup, SWT.LEFT);

		// Create pattern group and content
		Group pattGroup = new Group(shell, SWT.NONE);
		pattGroup.setText("Pattern");
		pattGroup.setLayout(new GridLayout(1, false));
		GridData pattGroupData = new GridData(SWT.FILL, SWT.FILL, true, true);
		pattGroupData.heightHint = 200;
		pattGroup.setLayoutData(pattGroupData);

		this.pattCombo = new Combo(pattGroup, SWT.NONE);

		this.addItemsToCombo();
		this.pattCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RegistryDialog.this.comboSelectionListener();
			}

		});

		this.pattText = new Text(pattGroup, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		this.pattText.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.pattText.setEditable(false);

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

		// Load allOutputText with pattDataMap's data
		this.loadDataMap();

		generalLabel.setText(this.loadSummaryData());

		// Set - All - to combo default selection
		this.pattCombo.select(0);
		this.comboSelectionListener();
	}

	private String loadSummaryData() {
		String output = "";

		output += "Project: " + Utilities.getProjectName() + "\n";
		output += "Patterns: " + this.pattDataMap.size() + "\n";
		output += "SiteViews used: " + this.svSummary + "\n";
		output += "Elements generated: " + this.elemSummary + "\n";

		return output;
	}

	private void checkRegistryExists() {
		if (!Registry.getInstance().fileExists()) {
			Utilities.showErrorUIMessage("Registry file is not created yet. Try to generate a pattern first.");
			this.close();
		}
	}

	private void addItemsToCombo() {
		// this.entitySelected = this.entityList.get(this.entityCombo.getSelectionIndex());

		this.pattCombo.add(" - All - ");

		for (String id : this.pattDataMap.keySet()) {
			this.pattCombo.add(id);
		}
	}

	private void loadDataMap() {
		SortedMap<String, Integer> allSv = new TreeMap<String, Integer>();
		SortedMap<String, Integer> allElements = new TreeMap<String, Integer>();
		String pattOutput = " - Patterns: \n";
		String svOutput = " - SiteViews: \n";
		String elementsOutput = " - Elements: \n";
		Integer auxInt;

		for (PatternRegisteredPOJO data : this.pattDataMap.values()) {
			pattOutput += "\t" + data.getId() + " (" + data.getTimesUsed() + ")\n";

			for (String sv : data.getSvReg().keySet()) {
				auxInt = data.getSvReg().get(sv);

				if (allSv.containsKey(sv))
					auxInt += allSv.get(sv);
				allSv.put(sv, auxInt);
			}

			for (String unit : data.getElementsReg().keySet()) {
				auxInt = data.getElementsReg().get(unit);

				if (allElements.containsKey(unit))
					auxInt += allElements.get(unit);
				allElements.put(unit, auxInt);
			}

		}
		pattOutput += "\n";

		for (String sv : allSv.keySet())
			svOutput += "\t" + sv + " (" + allSv.get(sv) + ")\n";
		this.svSummary = allSv.size();

		svOutput += "\n";

		int auxNum;
		for (String element : allElements.keySet()) {
			auxNum = allElements.get(element);
			this.elemSummary += auxNum;
			elementsOutput += "\t" + element + " (" + auxNum + ")\n";
		}
		this.allOutputText = pattOutput + svOutput + elementsOutput;
	}

	public void comboSelectionListener() {
		int selectionIndex = this.pattCombo.getSelectionIndex();
		String selectionText = this.pattCombo.getText();

		// Format Data and set it to the widget
		if (selectionIndex == 0) {
			// All
			this.pattText.setText(this.allOutputText);

		} else {
			if (!selectionText.isEmpty()) {
				PatternRegisteredPOJO data = this.pattDataMap.get(selectionText);

				this.pattText.setText(data.toString());

			}
		}
	}
}
