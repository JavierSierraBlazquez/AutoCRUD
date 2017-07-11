/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.webratio;

import java.util.List;

import org.eclipse.gef.commands.CommandStack;
import org.homeria.webratioassistant.webratioaux.AddSelectorConditionCommand;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.core.UnitHelper;
import com.webratio.ide.units.core.ISubUnitType;
import com.webratio.ide.units.internal.core.Selector;

@SuppressWarnings("restriction")
public final class NewSelector extends WebRatioCalls {

	private String type;
	private IMFElement element;

	public NewSelector(IMFElement element, String type) {
		super(null, 0, 0);
		this.element = element;
		this.type = type;
	}

	@Override
	public IMFElement execute() {
		List<ISubUnitType> list = UnitHelper.getUnitType(this.element).getSubUnitTypes();
		ISubUnitType keyCondition = null;
		Selector selector;
		IMFElement condition = null;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof Selector) {

				selector = (Selector) list.get(i);
				keyCondition = selector.getSubUnitType(this.type);

				AddSelectorConditionCommand cmd = new AddSelectorConditionCommand(this.element, keyCondition);
				cmd.setEditor(ProjectParameters.getWebProjectEditor().getActiveGraphEditor());
				((CommandStack) ProjectParameters.getWorkbenchPartWebRatio().getAdapter(CommandStack.class)).execute(cmd);
				condition = this.element.selectSingleElement(selector.getElementName()).selectSingleElement(keyCondition.getElementName());
			}
		}
		return condition;
	}
}
