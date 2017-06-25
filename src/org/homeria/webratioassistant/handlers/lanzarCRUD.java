/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.handlers;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.homeria.webratioassistant.elements.Link;
import org.homeria.webratioassistant.elements.Unit;
import org.homeria.webratioassistant.elements.WebRatioElement;
import org.homeria.webratioassistant.generation.Generate;
import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.plugin.Utilities;
import org.homeria.webratioassistant.wizards.StepGenerationAppWindow;
import org.homeria.webratioassistant.wizards.WizardCRUD;

import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.ui.viewers.SelectionHelper;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationshipRole;

public class lanzarCRUD extends AbstractHandler {
	public lanzarCRUD() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			// Obtenemos, mediante la busqueda en el workbench, el elemento seleccionado, si es una entidad se evitará usar la primera
			// página del asistente, en caso de estar seleccionado otro elemento el asistente será completo
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			IWorkbenchPage page = window.getActivePage();
			IEditorPart editor = page.getActiveEditor();
			ProjectParameters.setShell(window.getShell());

			ISelection selection = null;
			IStructuredSelection structuredSelection = null;
			if (editor != null) {
				selection = editor.getSite().getSelectionProvider().getSelection();

				if (((selection instanceof IStructuredSelection)) & (!((IStructuredSelection) selection).isEmpty())) {
					structuredSelection = (IStructuredSelection) selection;
				}

				IMFElement element;
				element = SelectionHelper.getModelElement(structuredSelection, true);

				WizardCRUD wizard;

				ProjectParameters.init();
				// Si el asistente se inicia con una entidad ya seleccionada nos ahorramos una pagina, en caso contrario mostramos el
				// asistente completo dando la opci�n de elegir la entidad de la que queremos obtener el CRUD
				if (element instanceof IEntity) {
					wizard = new WizardCRUD((IEntity) element);
				} else {
					wizard = new WizardCRUD();
				}
				wizard.init(window.getWorkbench(), structuredSelection);
				WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
				dialog.setPageSize(1000, 440);
				Utilities.setParentDialog(dialog);
				Utilities.setIsClosed(false);
				if (dialog.open() == Window.OK && !Utilities.isPluginClosed()) {
					// Get all the Data from Wizard Page
					Queue<WebRatioElement> pages = wizard.getPagesGen();
					List<Unit> units = wizard.getUnits();
					List<Link> links = wizard.getLinks();
					List<IMFElement> siteViewsAreas = wizard.getSiteViewsAreas();
					Map<IRelationshipRole, IAttribute> relshipsSelected = wizard.getRelshipsSelected();

					Generate generate = new Generate(pages, units, links, siteViewsAreas, relshipsSelected);
					StepGenerationAppWindow appWindow = new StepGenerationAppWindow(window.getShell(), generate);

					appWindow.open();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
