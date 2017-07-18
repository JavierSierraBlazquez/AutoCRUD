/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.webratio;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.ResourceUtil;

import com.webratio.commons.internal.mf.MFElement;
import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.IMFIdProvider;
import com.webratio.commons.mf.operations.CreateMFOperation;
import com.webratio.commons.mf.operations.MFUpdater;
import com.webratio.commons.mf.ui.commands.SetAttributeCommand;
import com.webratio.commons.mf.ui.editors.MFMultiEditor;
import com.webratio.ide.core.UnitHelper;
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.ILinkParameter;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISiteView;
import com.webratio.ide.units.core.ISubUnitType;

public class Utilities {
	public final static int unitHeight = 125;
	public final static int unitWidth = 150;

	private static SetAttributeCommand setCommand;
	private static MFUpdater updater;
	private static WizardDialog parentDialog;
	private static boolean isClosed;

	/**
	 * 
	 * Nombre: buscarHueco Funcion:
	 * 
	 * @return
	 */
	public static Point findGap() {

		Point startingPoint = new Point();
		try {
			// You get the selected editor (a siteView)
			EditPartViewer editP = ProjectParameters.getEditPartViewer();
			// The coordinate map is obtained, each one representing an element of the site See
			Map map = editP.getVisualPartMap();
			Set<IFigure> shapes = map.keySet();

			Iterator<IFigure> it = shapes.iterator();
			IFigure shape;
			int maximumRight = 0;
			int maximumWidth = 0;

			while (it.hasNext()) {
				shape = it.next();
				// Check that the border on the right is different from the one we already have
				if (shape.getBounds().width != maximumWidth) {
					// And now it is verified that there is a hole that is high of at least 1250 pixels
					if (shape.getBounds().getLocation().y < 1250) {
						// If so, that position will be valid, and we will only stay with the farthest to the right...
						if (shape.getBounds().getLocation().x > maximumRight) {
							maximumRight = shape.getBounds().getLocation().x;
						}
					}
				}
			}
			// If it is the initial position is added 25 pixels so that it does not go wrong visually
			if (maximumRight == 0)
				maximumRight = maximumRight + 25;
			else
				// If there are already drawn elements 200 pixels are added to separate them
				maximumRight = maximumRight + 200;
			// The Y position will always be 25 pixels
			startingPoint.setLocation(maximumRight, 25);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return startingPoint;
	}

	public static ILinkParameter createLinkParameter(String modelId, IMFIdProvider idProvider, String parentId) {
		Class<ILinkParameter> publicType = ILinkParameter.class;
		ILinkParameter newLinkParameter = (ILinkParameter) new CreateMFOperation(publicType, modelId).execute();
		((MFElement) newLinkParameter).setAttribute("id", idProvider.getFirstFreeId(parentId, publicType, null, true).first);
		return newLinkParameter;
	}

	/**
	 * 
	 * Nombre: getAttribute Funcion:
	 * 
	 * @param element
	 * @param attribute
	 * @return
	 */
	public static String getAttribute(IMFElement element, String attribute) {
		updater = element.getRootElement().getModelUpdater();
		return (updater.getAttribute(element, attribute));
	}

	public static String getDisplayName(IMFElement element) {
		String displayName = "";
		if (element instanceof ISiteView || element instanceof IArea)
			displayName = Utilities.getAttribute(element, "name") + " (" + element.getFinalId() + ")";

		return displayName;
	}

	/**
	 * 
	 */
	public static ISubUnitType getSubUnitType(IMFElement element, String name) {
		return UnitHelper.getUnitType(element).getSubUnitType(name);
	}

	/**
	 * 
	 * Nombre: setAttribute Funcion:
	 * 
	 * @param element
	 * @param attribute
	 * @param newValue
	 * @return
	 */
	public static boolean setAttribute(IMFElement element, String attribute, String newValue) {
		boolean canExecute;
		setCommand = new SetAttributeCommand(element, attribute, newValue, element.getModelId(), ProjectParameters.getEditPartViewer());
		canExecute = setCommand.canExecute();
		if (canExecute) {
			setCommand.execute();
		}
		return canExecute;
	}

	/**
	 * 
	 * Nombre: switchSiteView Funcion:
	 * 
	 * @param siteView
	 */
	public static void switchSiteView(ISiteView siteView) {
		try {
			MFMultiEditor multiEditor = (MFMultiEditor) ProjectParameters.getMFGraphEditor(siteView).getAdapter(MFMultiEditor.class);
			multiEditor.activateEditor(ProjectParameters.getMFGraphEditor(siteView));
			ProjectParameters.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Nombre: getTargetEntity Funcion:
	 * 
	 * @param role
	 * @param entity
	 * @return
	 */
	public static IEntity getTargetEntity(IRelationshipRole role, IEntity entity) {
		IRelationship relation = (IRelationship) role.getParentElement();
		if (relation.getTargetEntity() == entity) {
			return relation.getSourceEntity();
		} else
			return relation.getTargetEntity();
	}

	public static String getPatternsPath() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage iworkbenchpage = window.getActivePage();
		IEditorInput ieditorpart = iworkbenchpage.getActiveEditor().getEditorInput();
		IPath path = ResourceUtil.getFile(ieditorpart).getLocation();
		// remove file (example: Model.wr):
		path = path.removeLastSegments(1).addTrailingSeparator().append("patterns").addTrailingSeparator();
		return path.toString();
	}

	public static String getProjectName() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage iworkbenchpage = window.getActivePage();
		IEditorInput ieditorpart = iworkbenchpage.getActiveEditor().getEditorInput();

		return ResourceUtil.getFile(ieditorpart).getProject().getName();
	}

	public static void setParentDialog(WizardDialog dialog) {
		parentDialog = dialog;

	}

	public static void closePlugin() {
		isClosed = true;
		parentDialog.close();
	}

	public static boolean isPluginClosed() {
		return isClosed;
	}

	public static void setIsClosed(boolean isClosed) {
		Utilities.isClosed = isClosed;
	}

	public static void showErrorUIMessage(String message) {
		MessageBox messageBox = new MessageBox(ProjectParameters.getShell(), SWT.ICON_ERROR);
		messageBox.setText("Error");
		messageBox.setMessage(message);

		messageBox.open();
	}
}
