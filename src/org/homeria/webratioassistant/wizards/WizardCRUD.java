/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */

package org.homeria.webratioassistant.wizards;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.homeria.webratioassistant.elements.Link;
import org.homeria.webratioassistant.elements.Unit;
import org.homeria.webratioassistant.elements.WebRatioElement;
import org.homeria.webratioassistant.exceptions.CantOpenFileException;
import org.homeria.webratioassistant.exceptions.CantParseXmlFileException;
import org.homeria.webratioassistant.exceptions.ExceptionHandler;
import org.homeria.webratioassistant.exceptions.IdNotUniqueException;
import org.homeria.webratioassistant.exceptions.MissingSectionException;
import org.homeria.webratioassistant.exceptions.NoIdException;
import org.homeria.webratioassistant.exceptions.NoSourceIdException;
import org.homeria.webratioassistant.exceptions.NoTargetIdException;
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

	private Queue<WebRatioElement> pages;
	private List<Unit> units;
	private List<Link> links;
	private List<IMFElement> siteViewsAreas;
	private Map<IRelationshipRole, IAttribute> relshipsSelected;

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

	public Queue<WebRatioElement> getPagesGen() {
		return this.pages;
	}

	public List<Unit> getUnits() {
		return this.units;
	}

	public List<Link> getLinks() {
		return this.links;
	}

	public List<IMFElement> getSiteViewsAreas() {
		return this.siteViewsAreas;
	}

	public Map<IRelationshipRole, IAttribute> getRelshipsSelected() {
		return this.relshipsSelected;
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

	@Override
	public boolean performFinish() {

		try {
			this.patternPage.finalizePage();

			this.pages = this.patternPage.getPages();
			this.units = this.patternPage.getUnits();
			this.links = this.patternPage.getLinks();
			this.siteViewsAreas = this.patternPage.getSvAreasChecked();
			this.relshipsSelected = this.patternPage.getRelationshipsSelected();

		} catch (CantOpenFileException e) {
			ExceptionHandler.handle(e);
		} catch (CantParseXmlFileException e) {
			ExceptionHandler.handle(e);
		} catch (NoIdException e) {
			ExceptionHandler.handle(e);
		} catch (IdNotUniqueException e) {
			ExceptionHandler.handle(e);
		} catch (NoSourceIdException e) {
			ExceptionHandler.handle(e);
		} catch (NoTargetIdException e) {
			ExceptionHandler.handle(e);
		} catch (MissingSectionException e) {
			ExceptionHandler.handle(e);
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
