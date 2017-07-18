package org.homeria.webratioassistant.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Table;
import org.homeria.webratioassistant.webratio.NewUnit;
import org.homeria.webratioassistant.webratio.Utilities;
import org.homeria.webratioassistant.webratio.WebRatioCalls;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;

public class DataUnit extends Unit {

	private String parentId;
	private String selectedAttributes;
	private Table table;

	public DataUnit(String id, String name, String parentId, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
		this.parentId = parentId;
		this.selectedAttributes = "";
	}

	public void setTable(Table table) {
		this.table = table;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent = createdElements.get(this.parentId);

		WebRatioCalls newUnitWRCall = new NewUnit(parent, ElementTypes.DATA_UNIT, this.position.x, this.position.y, this.name, this.entity);

		IMFElement dataUnit = newUnitWRCall.execute();

		Utilities.setAttribute(dataUnit, "displayAttributes", this.selectedAttributes);
		return dataUnit;
	}

	public void extractTableAttributes() {
		this.selectedAttributes = this.getSelectedAttributes();
	}

	public String getSelectedAttributes() {
		List<IAttribute> entityList = this.entity.getAllAttributeList();
		List<IAttribute> itemsCheckedList = new ArrayList<IAttribute>();

		for (int i = 0; i < entityList.size(); i++) {
			if (this.table.getItem(i).getChecked())
				itemsCheckedList.add(entityList.get(i));
		}

		// Transform the attributes to string
		boolean webRatioEntity = false;
		String entityType = Utilities.getAttribute(this.entity, "id");
		if (entityType.equals("User") || entityType.equals("Group") || entityType.equals("Module"))
			webRatioEntity = true;

		String attributes = "";
		for (IAttribute att : itemsCheckedList) {
			if (!webRatioEntity) {
				// If it is not own webratio it is generated with the format ent1 # att1
				attributes = attributes + this.entity.getFinalId() + "#" + att.getFinalId() + " ";
			} else {
				attributes = attributes + att.getFinalId() + " ";
			}
		}
		if (attributes.length() != 0)
			// We format the string correctly by removing the final white space
			attributes = attributes.substring(0, attributes.length() - 1);

		return attributes;
	}

	@Override
	public WebRatioElement getCopy() {
		return new DataUnit(this.id, this.name, this.parentId, String.valueOf(this.position.x), String.valueOf(this.position.y),
				this.entity);
	}

}
