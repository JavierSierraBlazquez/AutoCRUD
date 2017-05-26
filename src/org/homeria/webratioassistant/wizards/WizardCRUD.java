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
import org.homeria.webratioassistant.elements.Link;
import org.homeria.webratioassistant.elements.NormalNavigationFlow;
import org.homeria.webratioassistant.elements.Page;
import org.homeria.webratioassistant.elements.Unit;
import org.homeria.webratioassistant.elements.WebRatioElement;
import org.homeria.webratioassistant.plugin.Debug;
import org.homeria.webratioassistant.plugin.ProjectParameters;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISiteView;

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
		// FIXME canFinish
		// return this.patternPage.canFinish();
		return true;
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
			List<WebRatioElement> pages = this.patternPage.getPages();
			List<Unit> units = this.patternPage.getUnits();
			List<Link> links = this.patternPage.getLinks();
			List<ISiteView> siteViews = this.patternPage.getSiteViewsChecked();
			Map<IRelationshipRole, IAttribute> relshipsSelected = this.patternPage.getRelationshipsSelected();
			IEntity entity = this.patternPage.getEntitySelected();

			for (ISiteView siteView : siteViews) {

				// pruebas
				for (WebRatioElement page : pages) {
					System.out.println(page.getId() + " - " + page.getName());
				}

				for (Unit unit : units) {

					System.out.println(unit.getId() + " - " + unit.getName());
				}

				for (Link link : links) {

					System.out.println(link.getId() + " - " + link.getName() + " - " + link.getSourceId() + " - " + link.getTargetId());
				}

				// fin pruebas
				Map<String, IMFElement> createdElements = new HashMap<String, IMFElement>();

				for (WebRatioElement page : pages) {
					((Page) page).setSiteView(siteView);
					createdElements.put(page.getId(), page.generate(createdElements));
				}

				for (Unit unit : units) {
					if (unit instanceof EntryUnit)
						((EntryUnit) unit).setRelshipsSelected(relshipsSelected);

					else if (unit instanceof DeleteUnit)
						((DeleteUnit) unit).setSiteView(siteView);

					else if (unit instanceof CreateUnit)
						((CreateUnit) unit).setSiteView(siteView);

					else if (unit instanceof ConnectUnit)
						((ConnectUnit) unit).setSiteView(siteView);

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
			/*
						if (operationsChecked.contains(Utilities.Operations.CREATE))
							this.create = new Create(entidad, this.crudPage.getSiteViewsChecked(), this.crudPage.getAreas(),
									this.crudPage.getRelationShipsCreate(), this.crudPage.getAttributesDataCreate(),
									this.selectEntityPage.getGenerationDelay());
						if (operationsChecked.contains(Utilities.Operations.READ))
							this.read = new Read(entidad, this.crudPage.getSiteViewsChecked(), this.crudPage.getAreas(),
									this.crudPage.getAttributesIndexRead(), this.crudPage.getAttributesDataRead(),
									this.selectEntityPage.getGenerationDelay());
						if (operationsChecked.contains(Utilities.Operations.UPDATE))
							this.update = new Update(entidad, this.crudPage.getSiteViewsChecked(), this.crudPage.getAreas(),
									this.crudPage.getRelationShipsUpdate(), this.crudPage.getAttributesUpdate(),
									this.crudPage.getAttributesShowUpdate(), this.selectEntityPage.getGenerationDelay());
						if (operationsChecked.contains(Utilities.Operations.DELETE))
							this.delete = new Delete(entidad, this.crudPage.getSiteViewsChecked(), this.crudPage.getAreas(),
									this.crudPage.getAttributesIndexDelete(), this.selectEntityPage.getGenerationDelay());
						if (operationsChecked.contains(Utilities.Operations.ALLINONE))
							this.allinone = new AllInOne(entidad, this.crudPage.getSiteViewsChecked(), this.crudPage.getAreas(),
									this.crudPage.getRelationShipsAllInOne(), this.crudPage.getAttributesIndexAllInOne(),
									this.crudPage.getAttributesDataAllInOne(), this.crudPage.getAttributesDetailAllInOne(),
									this.selectEntityPage.getGenerationDelay());

						final Create c = this.create;
						final Read r = this.read;
						final Update u = this.update;
						final Delete d = this.delete;
						final AllInOne a = this.allinone;
						IRunnableWithProgress op2 = new IRunnableWithProgress() {
							public void run(IProgressMonitor monitor) throws InvocationTargetException {
								try {
									doFinish(a, c, r, u, d, monitor);
								} catch (CoreException e) {
									throw new InvocationTargetException(e);
								} finally {
									monitor.done();
								}
							}
						};
						try {
							getContainer().run(false, false, op2);

							ProjectParameters.getActiveEditor().doSave(null);
						} catch (InterruptedException e) {
							return false;
						} catch (InvocationTargetException e) {
							Throwable realException = e.getTargetException();
							MessageDialog.openError(getShell(), "Error", realException.getMessage());
							e.printStackTrace();
							return false;
						}
			*/
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
			// this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
}
