/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */

package org.homeria.webratioassistant.wizards;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.homeria.webratioassistant.elements.ConnectUnit;
import org.homeria.webratioassistant.elements.CreateUnit;
import org.homeria.webratioassistant.elements.DataFlow;
import org.homeria.webratioassistant.elements.DeleteUnit;
import org.homeria.webratioassistant.elements.EntryUnit;
import org.homeria.webratioassistant.elements.IsNotNullUnit;
import org.homeria.webratioassistant.elements.Link;
import org.homeria.webratioassistant.elements.NormalNavigationFlow;
import org.homeria.webratioassistant.elements.Page;
import org.homeria.webratioassistant.elements.ReconnectUnit;
import org.homeria.webratioassistant.elements.Unit;
import org.homeria.webratioassistant.elements.UpdateUnit;
import org.homeria.webratioassistant.elements.WebRatioElement;
import org.homeria.webratioassistant.plugin.Debug;
import org.homeria.webratioassistant.plugin.ProjectParameters;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationshipRole;

/**
 * @author Carlos Aguado Fuentes
 * @class WizardCRUD
 */
/**
 * WizardCRUD: Clase principal encargada del asistente gr�fico
 */
public class WizardCRUD extends Wizard implements INewWizard {
	private WizardPatternPage patternPage;

	/**
	 * 
	 */
	public WizardCRUD() {
		super();
		this.setNeedsProgressMonitor(true);
	}

	/**
	 * 
	 * @param selection
	 */
	public WizardCRUD(IEntity selection) {
		super();
		this.setNeedsProgressMonitor(true);
	}

	/**
	 * 
	 */
	public void addPages() {
		this.patternPage = new WizardPatternPage();
		this.addPage(this.patternPage);
	}

	/**
	 * 
	 */
	public boolean canFinish() {
		return this.patternPage.canFinish();
	}

	/**
	 * 
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		Debug.setOn();
		try {
			ProjectParameters.init();
			ProjectParameters.initSiteViews();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Debug.println(this.getClass().toString(), e.toString());
			e.printStackTrace();
		}
	}

	/***
	 * 
	 * Nombre: doFinish Funcion:
	 * 
	 * @param a
	 * @param c
	 * @param r
	 * @param u
	 * @param d
	 * @param monitor
	 * @throws CoreException
	 */

	@Override
	public boolean performFinish() {
		try {
			this.patternPage.finalizePage();
			Queue<WebRatioElement> pages = this.patternPage.getPages();
			List<Unit> units = this.patternPage.getUnits();
			List<Link> links = this.patternPage.getLinks();
			List<IMFElement> siteViewsAreas = this.patternPage.getSvAreasChecked();
			Map<IRelationshipRole, IAttribute> relshipsSelected = this.patternPage.getRelationshipsSelected();
			for (IMFElement parent : siteViewsAreas) {

				Map<String, IMFElement> createdElements = new HashMap<String, IMFElement>();

				for (WebRatioElement page : pages) {
					if (page instanceof Page)
						((Page) page).setParent(parent);
					createdElements.put(page.getId(), page.generate(createdElements));
				}

				for (Unit unit : units) {
					if (unit instanceof EntryUnit)
						((EntryUnit) unit).setRelshipsSelected(relshipsSelected);

					else if (unit instanceof DeleteUnit)
						((DeleteUnit) unit).setParent(parent);

					else if (unit instanceof CreateUnit)
						((CreateUnit) unit).setParent(parent);

					else if (unit instanceof UpdateUnit)
						((UpdateUnit) unit).setParent(parent);

					else if (unit instanceof ConnectUnit)
						((ConnectUnit) unit).setParent(parent);

					else if (unit instanceof ReconnectUnit)
						((ReconnectUnit) unit).setParent(parent);

					else if (unit instanceof IsNotNullUnit)
						((IsNotNullUnit) unit).setParent(parent);

					createdElements.put(unit.getId(), unit.generate(createdElements));
				}

				for (Link link : links) {
					if (link instanceof NormalNavigationFlow)
						((NormalNavigationFlow) link).setRelshipsSelected(relshipsSelected);

					if (link instanceof DataFlow)
						((DataFlow) link).setRelshipsSelected(relshipsSelected);
					createdElements.put(link.getId(), link.generate(createdElements));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 
	 */
	public void finalize() {
		try {
			this.dispose();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
}
