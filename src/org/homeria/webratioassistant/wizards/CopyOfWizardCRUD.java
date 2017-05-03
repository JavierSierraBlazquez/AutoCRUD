/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */

package org.homeria.webratioassistant.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
import org.homeria.webratioassistant.plugin.EventoNuevaArea;
import org.homeria.webratioassistant.plugin.EventoNuevaSiteView;
import org.homeria.webratioassistant.plugin.ObjStViewArea;
import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.ISiteView;

/**
 * @author Carlos Aguado Fuentes
 * @class WizardCRUD
 */
/**
 * WizardCRUD: Clase principal encargada del asistente gr�fico
 */
public class CopyOfWizardCRUD extends Wizard implements INewWizard {
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
	public CopyOfWizardCRUD() {
		super();
		setNeedsProgressMonitor(true);
		this.entidadSeleccionada = null;

	}

	/**
	 * 
	 * @param selection
	 */
	public CopyOfWizardCRUD(IEntity selection) {
		super();
		setNeedsProgressMonitor(true);
		this.entidadSeleccionada = selection;
	}

	/**
	 * 
	 */
	public void addPages() {
		// FIXME: Pattern remodelation
		/*if (this.entidadSeleccionada == null) {
			this.selectEntityPage = new WizardSelectEntityPage();
			addPage(this.selectEntityPage);
		}

		this.crudPage = new WizardCRUDPage(this.entidadSeleccionada);
		addPage(this.crudPage);*/

		this.patternPage = new WizardPatternPage();
		addPage(this.patternPage);

	}

	/**
	 * 
	 */
	public boolean canFinish() {
		//TODO pattern remodelation
		if (getContainer().getCurrentPage() == this.crudPage && 
				!this.crudPage.getOperationsChecked().isEmpty() && 
				!this.crudPage.getSiteViewsChecked().isEmpty())
			return true;
		else
			return false;
	}

	/**
	 * 
	 * Nombre: doInicial Funcion:
	 * 
	 * @param monitor
	 * @throws CoreException
	 */
	private void doInicial(IProgressMonitor monitor) throws CoreException {
		// TODO pattern remodelation (esto no sirve creo, no se crean sites) 
		try {
			// Viene informado con los SiteView y Areas
			if (null != ProjectParameters.getlistaSiteViewArea() && ProjectParameters.getlistaSiteViewArea().size() > 0) {

				List listaSiteViewAreaAlta = ProjectParameters.getlistaSiteViewArea();

				for (Iterator iterator = listaSiteViewAreaAlta.iterator(); iterator.hasNext();) {
					ObjStViewArea objSiteView = (ObjStViewArea) iterator.next();
					if (null != objSiteView && null != objSiteView.getEsNuevo() && objSiteView.getEsNuevo()) {

						EventoNuevaSiteView crearSite = new EventoNuevaSiteView(objSiteView.getNombre());
						crearSite.ejecutar(new SubProgressMonitor(monitor, 1), objSiteView.getNombre());
						this.crudPage.actualizarListaSiteViews();
					}

					if (null != objSiteView.getListHijos() && objSiteView.getListHijos().size() > 0) {
						this.crudPage.actualizarListaSiteViews();
						crearAreas(objSiteView.getListHijos(), objSiteView.getNombre(), objSiteView.getNombre());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			monitor.done();
		}
	}

	/**
	 * 
	 * Nombre: crearAreas Funcion:
	 * 
	 * @param listaObjSiteView
	 * @param nombreSiteView
	 * @param nombrePadre
	 * @throws ExecutionException
	 */
	private void crearAreas(List<ObjStViewArea> listaObjSiteView, String nombreSiteView, String nombrePadre) throws ExecutionException {

		for (Iterator iterator2 = listaObjSiteView.iterator(); iterator2.hasNext();) {

			ObjStViewArea objecthijo = (ObjStViewArea) iterator2.next();
			// Es un area
			if (null != objecthijo && null != objecthijo.getEsNuevo() && objecthijo.getEsNuevo()) {

				// buscar el nodo padre del objeto que vamos a crear, para que
				// lo situe en el
				ISiteView siteView = this.crudPage.buscarElementoSiteView(nombreSiteView);
				if (nombreSiteView.compareTo(nombrePadre) != 0 && null != siteView.getAreaList() && siteView.getAreaList().size() > 0) {
					IArea areaEnc = this.crudPage.buscarElementoAreaRecursivo(siteView.getAreaList(), nombrePadre);
					EventoNuevaArea nuevaArea = new EventoNuevaArea(areaEnc, 150, 150, objecthijo.getNombre());
					nuevaArea.ejecutar();
				} else {
					EventoNuevaArea nuevaArea = new EventoNuevaArea(siteView, 150, 150, objecthijo.getNombre());
					nuevaArea.ejecutar();
				}

				this.crudPage.actualizarListaSiteViews();
			}

			if (null != objecthijo.getListHijos() && objecthijo.getListHijos().size() > 0) {
				List hijos2 = objecthijo.getListHijos();
				crearAreas(hijos2, nombreSiteView, objecthijo.getNombre());
			}
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
			final IEntity entidad;
			if (this.entidadSeleccionada == null) {
				entidad = (IEntity) this.selectEntityPage.getSelectedElement();
			} else {
				entidad = this.entidadSeleccionada;
			}

			// Primera parte parte para la creacion de siteView y que para la
			// segunda parte ya esten todos los SiteView activos
			IRunnableWithProgress op1 = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor2) throws InvocationTargetException {
					try {
						doInicial(monitor2);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor2.done();
					}
				}
			};
			try {
				getContainer().run(false, false, op1);
				ProjectParameters.getActiveEditor().doSave(null);
			} catch (InterruptedException e) {
				return false;
			} catch (InvocationTargetException e) {
				Throwable realException = e.getTargetException();
				MessageDialog.openError(getShell(), "Error", realException.getMessage());
				e.printStackTrace();
				return false;
			}

			// Resetar los project parameters para que siteView este correcto
			// segun siteView creada en metodo doInicial
			this.crudPage.actualizarListaSiteViews();
			// fin de la primera parte

			// Hay que pasarle SiteViews checkeadas y existentes, hay que pasale
			// Areas checkeadas y ya existentes

			List<Utilities.Operations> operationsChecked = this.crudPage.getOperationsChecked();

			if (operationsChecked.contains(Utilities.Operations.CREATE))
				this.create = new Create(entidad, 
						this.crudPage.getSiteViewsChecked(), 
						this.crudPage.getAreas(),
						this.crudPage.getRelationShipsCreate(), 
						this.crudPage.getAttributesDataCreate(),
						this.selectEntityPage.getGenerationDelay());
			if (operationsChecked.contains(Utilities.Operations.READ))
				this.read = new Read(entidad, 
						this.crudPage.getSiteViewsChecked(), 
						this.crudPage.getAreas(),
						this.crudPage.getAttributesIndexRead(), 
						this.crudPage.getAttributesDataRead(),
						this.selectEntityPage.getGenerationDelay());
			if (operationsChecked.contains(Utilities.Operations.UPDATE))
				this.update = new Update(entidad, 
						this.crudPage.getSiteViewsChecked(), 
						this.crudPage.getAreas(),
						this.crudPage.getRelationShipsUpdate(), 
						this.crudPage.getAttributesUpdate(),
						this.crudPage.getAttributesShowUpdate(),
						this.selectEntityPage.getGenerationDelay());
			if (operationsChecked.contains(Utilities.Operations.DELETE))
				this.delete = new Delete(entidad, 
						this.crudPage.getSiteViewsChecked(), 
						this.crudPage.getAreas(),
						this.crudPage.getAttributesIndexDelete(),
						this.selectEntityPage.getGenerationDelay());
			if (operationsChecked.contains(Utilities.Operations.ALLINONE))
				this.allinone = new AllInOne(entidad, 
						this.crudPage.getSiteViewsChecked(), 
						this.crudPage.getAreas(),
						this.crudPage.getRelationShipsAllInOne(), 
						this.crudPage.getAttributesIndexAllInOne(),
						this.crudPage.getAttributesDataAllInOne(),
						this.crudPage.getAttributesDetailAllInOne(),
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
