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
import com.webratio.ide.model.IPage;
import com.webratio.ide.ui.commands.AddAlternativeCommand;

@SuppressWarnings("restriction")
public final class NewAlternative extends WebRatioCalls {

	private String name;
	private IMFElement page;

	public NewAlternative(IMFElement parent, int x, int y, String name) {
		super(parent, x, y);
		this.name = name;
	}

	/**
	 * 
	 */
	public IMFElement execute() {
		try {
			// We verify that the parent is a page, since the alternative can only go within a page
			if (this.getParent() instanceof IPage) {
				SelectionCommand cmd = new AddAlternativeCommand(this.getParent().getModelId());
				List<IMFElement> list = new ArrayList<IMFElement>();
				list.add(this.getParent());
				cmd.setSelection(list);
				cmd.setLocation(this.getPoint());

				((CommandStack) ProjectParameters.getWorkbenchPartWebRatio().getAdapter(CommandStack.class)).execute(cmd);
				// We get the page that has been created within the alternative zone
				this.page = this.getLastArea(this.getParent());
				Utilities.setAttribute(this.page, "name", this.name);

			}
		} catch (Exception e) {
			Debug.println(this.getClass().toString() + " " + new Exception().getStackTrace()[0].getMethodName(), "Failed to add page");
			e.printStackTrace();
		}
		return this.page;

	}

	/**
	 * 
	 * Nombre: getLastArea Funcion:
	 * 
	 * @param element
	 * @return
	 */
	private IMFElement getLastArea(IMFElement element) {
		IPage page;
		if (element instanceof IPage) {
			page = (IPage) element;
			int numberAlternatives = page.getAlternativeList().size();
			return (page.getAlternativeList().get(numberAlternatives - 1));
		}
		return null;
	}

}
