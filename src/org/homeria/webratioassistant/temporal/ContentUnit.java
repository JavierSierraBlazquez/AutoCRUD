package org.homeria.webratioassistant.temporal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Table;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;

public class ContentUnit {
	private String id;
	private String name;
	private String parentId;
	private String type;
	private Table table;

	public ContentUnit(String id, String name, String parentId, String type) {
		this.id = id;
		this.name = name;
		this.parentId = parentId;
		this.type = type;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getParentId() {
		return this.parentId;
	}

	public String getType() {
		return this.type;
	}

	public String getSelectedAttributes(IEntity entity) {
		List<IAttribute> entityList = entity.getAllAttributeList();
		List<IAttribute> itemsCheckedList = new ArrayList<IAttribute>();

		for (int i = 0; i < entityList.size(); i++) {
			if (this.table.getItem(i).getChecked())
				itemsCheckedList.add(entityList.get(i));
		}

		// Transformo los atributos a cadena de texto:
		boolean entidadWR = false;
		String tipoEntidad = Utilities.getAttribute(entity, "id");
		if (tipoEntidad.equals("User") || tipoEntidad.equals("Group") || tipoEntidad.equals("Module"))
			entidadWR = true;

		String atributos = "";
		for (IAttribute atributo : itemsCheckedList) {
			if (!entidadWR) {
				// Si no es propia de webratio se general con el formato ent1#att1
				atributos = atributos + entity.getFinalId() + "#" + atributo.getFinalId() + " ";
			} else {
				atributos = atributos + atributo.getFinalId() + " ";
			}
		}
		if (atributos.length() != 0)
			// Formateamos correctamente la cadena eliminando el espacio en blanco final
			atributos = atributos.substring(0, atributos.length() - 1);

		return atributos;
	}

	public void setTable(Table table) {
		this.table = table;
	}
}
