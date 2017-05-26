package org.homeria.webratioassistant.units;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Table;
import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;
import org.homeria.webratioassistant.plugin.Utilities;
import org.homeria.webratioassistant.temporal.ElementType;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;

public class PowerIndexUnit extends Unit {

	private String parentId;
	private Table table;

	public PowerIndexUnit(String id, String name, String parentId, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
		this.parentId = parentId;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent = createdElements.get(this.parentId);

		Evento evento = new EventoNuevaUnit(parent, ElementType.POWER_INDEX_UNIT, this.position.x, this.position.y, this.name, this.entity);

		IMFElement pIndexUnit = evento.ejecutar();
		// Se añaden los atributos
		Utilities.setAttribute(pIndexUnit, "displayAttributes", this.getSelectedAttributes());
		return pIndexUnit;
	}

	private String getSelectedAttributes() {
		List<IAttribute> entityList = this.entity.getAllAttributeList();
		List<IAttribute> itemsCheckedList = new ArrayList<IAttribute>();

		for (int i = 0; i < entityList.size(); i++) {
			if (this.table.getItem(i).getChecked())
				itemsCheckedList.add(entityList.get(i));
		}

		// Transformo los atributos a cadena de texto:
		boolean entidadWR = false;
		String tipoEntidad = Utilities.getAttribute(this.entity, "id");
		if (tipoEntidad.equals("User") || tipoEntidad.equals("Group") || tipoEntidad.equals("Module"))
			entidadWR = true;

		String atributos = "";
		for (IAttribute atributo : itemsCheckedList) {
			if (!entidadWR) {
				// Si no es propia de webratio se general con el formato ent1#att1
				atributos = atributos + this.entity.getFinalId() + "#" + atributo.getFinalId() + " ";
			} else {
				atributos = atributos + atributo.getFinalId() + " ";
			}
		}
		if (atributos.length() != 0)
			// Formateamos correctamente la cadena eliminando el espacio en blanco final
			atributos = atributos.substring(0, atributos.length() - 1);

		return atributos;
	}

}
