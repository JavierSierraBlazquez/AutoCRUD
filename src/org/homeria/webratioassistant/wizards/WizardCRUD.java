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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.homeria.webratioassistant.crud.AllInOne;
import org.homeria.webratioassistant.crud.Create;
import org.homeria.webratioassistant.crud.Delete;
import org.homeria.webratioassistant.crud.Read;
import org.homeria.webratioassistant.crud.Update;
import org.homeria.webratioassistant.plugin.Debug;
import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.plugin.Utilities;
import org.homeria.webratioassistant.temporal.ContentUnit;
import org.homeria.webratioassistant.temporal.ElementType;
import org.homeria.webratioassistant.temporal.Generate;
import org.homeria.webratioassistant.temporal.Link;

import com.webratio.ide.model.IContentUnit;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IPage;
import com.webratio.ide.model.ISiteView;

/**
 * @author Carlos Aguado Fuentes
 * @class WizardCRUD
 */
/**
 * WizardCRUD: Clase principal encargada del asistente gr�fico
 */
public class WizardCRUD extends Wizard implements INewWizard {
	public static Create c = null;
	// FIXME Pattern remodelation
	private WizardCRUDPage crudPage;
	private WizardSelectEntityPage selectEntityPage;
	private WizardPatternPage patternPage;

	private AllInOne allinone;
	private Create create;
	private Read read;
	private Update update;
	private Delete delete;
	private IEntity entidadSeleccionada;

	/**
	 * 
	 */
	public WizardCRUD() {
		super();
		setNeedsProgressMonitor(true);
		this.entidadSeleccionada = null;

	}

	/**
	 * 
	 * @param selection
	 */
	public WizardCRUD(IEntity selection) {
		super();
		setNeedsProgressMonitor(true);
		this.entidadSeleccionada = selection;
	}

	/**
	 * 
	 */
	public void addPages() {
		// FIXME: Pattern remodelation
		/*
		 * if (this.entidadSeleccionada == null) { this.selectEntityPage = new
		 * WizardSelectEntityPage(); addPage(this.selectEntityPage); }
		 * 
		 * this.crudPage = new WizardCRUDPage(this.entidadSeleccionada);
		 * addPage(this.crudPage);
		 */

		this.patternPage = new WizardPatternPage();
		addPage(this.patternPage);

	}

	/**
	 * 
	 */
	public boolean canFinish() {
		// TODO pattern remodelation
		/*
		if (getContainer().getCurrentPage() == this.crudPage && !this.crudPage.getOperationsChecked().isEmpty()
				&& !this.crudPage.getSiteViewsChecked().isEmpty())
			return true;
		else
			return false;
			*/
		return true;
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
	private void doFinish(AllInOne a, Create c, Read r, Update u, Delete d, IProgressMonitor monitor) throws CoreException {
		try {

			List<Utilities.Operations> operationsChecked = this.crudPage.getOperationsChecked();
			int numTareas = operationsChecked.size();

			monitor.beginTask("Running READ", numTareas);
			if (operationsChecked.contains(Utilities.Operations.READ))
				r.ejecutar(new SubProgressMonitor(monitor, 1));
			monitor.setTaskName("Running CREATE");
			if (operationsChecked.contains(Utilities.Operations.CREATE))
				c.ejecutar(new SubProgressMonitor(monitor, 1));
			monitor.setTaskName("Running ALLINONE");
			if (operationsChecked.contains(Utilities.Operations.ALLINONE))
				a.ejecutar(new SubProgressMonitor(monitor, 1));
			monitor.setTaskName("Running UPDATE");
			if (operationsChecked.contains(Utilities.Operations.UPDATE))
				u.ejecutar(new SubProgressMonitor(monitor, 1));
			monitor.setTaskName("Running DELETE");
			if (operationsChecked.contains(Utilities.Operations.DELETE))
				d.ejecutar(new SubProgressMonitor(monitor, 1));

		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			monitor.done();
		}
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

			List<ContentUnit> contentUnits = this.patternPage.getContentUnits();
			List<Link> links = this.patternPage.getLinks();
			IEntity entity = this.patternPage.getEntitySelected();
			Map<String, IPage> createdPages = new HashMap<String, IPage>();
			Map<String, IContentUnit> createdCU = new HashMap<String, IContentUnit>();
			IPage page;

			for (ISiteView sv : this.patternPage.getSiteViewsChecked()) {
				for (ContentUnit cu : contentUnits) {
					if (null == (page = createdPages.get(cu.getParentId()))) {
						// Creamos pagina nueva
						page = Generate.page(sv, cu.getParentId());
						createdPages.put(cu.getParentId(), page);
					}

					if (cu.getType().contentEquals(ElementType.POWER_INDEX_UNIT))
						createdCU.put(cu.getId(), Generate.powerIndexUnit(page, cu, entity));
					else if (cu.getType().contentEquals(ElementType.DATA_UNIT))
						createdCU.put(cu.getId(), Generate.dataUnit(page, cu, entity));
				}

				for (Link link : links) {
					Generate.normalLink(link.getName(), createdCU.get(link.getSourceId()), createdCU.get(link.getDestinyId()));
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
