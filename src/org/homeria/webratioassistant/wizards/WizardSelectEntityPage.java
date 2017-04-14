/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.wizards;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.homeria.webratioassistant.plugin.MyIEntityComparator;
import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;

/**
 * WizardSelectEntityPage: Clase que genera los elementos visuales que se
 * muestran en la p�gina que permite seleccionar la entidad sobre la que se
 * generar� el CRUD
 */
public class WizardSelectEntityPage extends WizardPage {
	private Composite container = null;
	private Group entityGroup = null;
	private Group speedGenGroup = null;
	private List<IEntity> listEntity;
	private org.eclipse.swt.widgets.List widgetListEntity = null;

	public WizardSelectEntityPage() {
		super("wizardSelectEntityPage");
		setTitle("WebRatio Assistant");
		setDescription("This page lets you select the entity to perform the CRUD.");
	}

	@Override
	public boolean canFlipToNextPage() {
		if (this.widgetListEntity.getSelectionIndex() >= 0)
			return true;
		else
			return false;
	}

	private void combo1WidgetSelected(SelectionEvent evt) {
		this.getWizard().getContainer().updateButtons();
	}

	public void createControl(Composite parent) {
		this.container = new Composite(parent, SWT.NULL);
		FillLayout containerLayout = new FillLayout(SWT.HORIZONTAL);
		containerLayout.marginHeight = 40;
		containerLayout.marginWidth = 40;
		this.container.setLayout(containerLayout);

		this.entityGroup = new Group(this.container, SWT.NONE);
		FillLayout entityGroupLayout = new FillLayout(SWT.VERTICAL);
		entityGroupLayout.marginHeight = 10;
		entityGroupLayout.marginWidth = 10;
		this.entityGroup.setLayout(entityGroupLayout);
		this.entityGroup.setText("Select an Entity");

		this.widgetListEntity = new org.eclipse.swt.widgets.List(this.entityGroup, SWT.SINGLE | SWT.V_SCROLL);
		this.widgetListEntity.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				combo1WidgetSelected(evt);
			}
		});

		initialize();
		setControl(this.container);
		this.container.layout(true, true);
		try {
			this.dispose();
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public IWizardPage getNextPage() {
		WizardCRUDPage crud = (WizardCRUDPage) this.getWizard().getPage("wizardCRUDPage");

		crud.setEntity((IEntity) this.getSelectedElement());

		crud.initialize();

		return crud;
	}

	public IMFElement getSelectedElement() {
		return this.listEntity.get(this.widgetListEntity.getSelectionIndex());
	}

	private void initialize() {
		if (ProjectParameters.getWebProjectEditor() != null) {
			try {
				ProjectParameters.init();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			this.listEntity = ProjectParameters.getDataModel().getEntityList();
			this.listEntity = ProjectParameters.getDataModel().getAllEntityList();
			Collections.sort(this.listEntity, new MyIEntityComparator());

			IMFElement imfe;

			Iterator<IEntity> iter = this.listEntity.iterator();
			while (iter.hasNext()) {
				imfe = iter.next();
				this.widgetListEntity.add(Utilities.getAttribute(imfe, "name"));
			}
		}
	}
}