/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.webratio;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.CommandStack;

import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.ui.commands.SelectionCommand;
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.ISiteView;
import com.webratio.ide.ui.commands.AddAreaCommand;

@SuppressWarnings("restriction")
public final class NewArea extends WebRatioCalls {

	private String name;
	private IMFElement element;

	public NewArea(IMFElement parent, int x, int y, String name) {
		super(parent, x, y);
		this.name = name;
	}

	public IMFElement execute() {
		try {
			// We verify that the parent is a SiteView or Area, the Area can only go within a SiteView or another Area
			if ((this.getParent() instanceof ISiteView) || (this.getParent() instanceof IArea)) {

				SelectionCommand cmd = new AddAreaCommand(this.getParent().getModelId());
				if (this.getParent() instanceof ISiteView)
					Utilities.switchSiteView((ISiteView) this.getParent());

				List<IMFElement> list = new ArrayList<IMFElement>();
				list.add(this.getParent());
				cmd.setSelection(list);
				cmd.setLocation(this.getPoint());

				// Execute
				((CommandStack) ProjectParameters.getWorkbenchPartWebRatio().getAdapter(CommandStack.class)).execute(cmd);
				// We get the page that has been created within the alternative zone
				this.element = this.getLastArea(this.getParent());
				Utilities.setAttribute(this.element, "name", this.name);

			}
		} catch (Exception e) {
			Debug.println(this.getClass().toString() + " " + new Exception().getStackTrace()[0].getMethodName(), "Failed to add area");
			e.printStackTrace();
		}
		return this.element;

	}

	private IMFElement getLastArea(IMFElement element) {
		ISiteView siteView;
		IArea area;
		if (element instanceof ISiteView) {
			siteView = (ISiteView) element;
			int number = siteView.getAreaList().size();
			return (siteView.getAreaList().get(number - 1));
		}
		if (element instanceof IArea) {
			area = (IArea) element;
			int number = area.getAreaList().size();
			return (area.getAreaList().get(number - 1));
		}
		return null;
	}

}
