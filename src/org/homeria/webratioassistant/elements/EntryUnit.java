package org.homeria.webratioassistant.elements;

import java.util.List;
import java.util.Map;

import org.homeria.webratioassistant.webratio.NewUnit;
import org.homeria.webratioassistant.webratio.ProjectParameters;
import org.homeria.webratioassistant.webratio.Utilities;
import org.homeria.webratioassistant.webratio.WebRatioCalls;
import org.homeria.webratioassistant.webratioaux.CompositeMFCommand;

import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.operations.SetAttributeMFOperation;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;

public class EntryUnit extends Unit {
	private String parentId;
	private Map<IRelationshipRole, IAttribute> relshipsSelected;
	private String type;
	private IMFElement fieldOid;

	public EntryUnit(String id, String name, String parentId, String type, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
		this.parentId = parentId;
		this.entity = entity;
		this.type = type;
	}

	public void setRelshipsSelected(Map<IRelationshipRole, IAttribute> relshipsSelected) {
		this.relshipsSelected = relshipsSelected;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent = createdElements.get(this.parentId);

		WebRatioCalls newUnitWRCall = new NewUnit(parent, ElementTypes.ENTRY_UNIT, this.position.x, this.position.y, this.name, this.entity);
		IMFElement entryUnit = newUnitWRCall.execute();
		this.setFields(entryUnit);

		ProjectParameters.entryKeyfieldMap.put(entryUnit, this.fieldOid);
		return entryUnit;

	}

	private void setFields(IMFElement element) {
		CompositeMFCommand cmd = new CompositeMFCommand(element.getRootElement());
		String name;
		String type;
		IMFElement field;

		List<IAttribute> attList = this.entity.getAllAttributeList();
		// We go through the final list of attributes to create the fields in the form
		for (IAttribute attribute : attList) {
			// No derived att:
			if (null == Utilities.getAttribute(attribute, "derivationQuery")
					|| Utilities.getAttribute(attribute, "derivationQuery").isEmpty()) {
				name = Utilities.getAttribute(attribute, "name");
				type = Utilities.getAttribute(attribute, "type");
				field = cmd.addSubUnit(Utilities.getSubUnitType(element, "Field"), element);
				// If the type is password we pass it to type string for convenience at the time of modifying the form
				if (type.equals("password"))
					type = "string";
				Utilities.setAttribute(field, "type", type);

				if (this.type.equals(ElementTypes.ENTRYUNIT_PRELOADED))
					Utilities.setAttribute(field, "preloaded", "true");

				new SetAttributeMFOperation(field, "name", name, element.getRootElement()).execute();

				if ((Utilities.getAttribute(attribute, "name").contains("oid") || Utilities.getAttribute(attribute, "name").contains("OID"))
						&& Utilities.getAttribute(attribute, "key").equals("true")) {
					Utilities.setAttribute(field, "hidden", "true");
					Utilities.setAttribute(field, "modifiable", "false");
					this.fieldOid = field;

					// Keep the contentType of the fields in the form.
					if (null != Utilities.getAttribute(attribute, "contentType")) {
						Utilities.setAttribute(field, "contentType", Utilities.getAttribute(attribute, "contentType"));
					}
				}
			}
		}

		// Now we do the same thing by creating selectionField in case of relations 1aN and multiSelectionField in case of relations NaN
		IRelationship relation;
		String maxCard1;
		String maxCard2;
		IAttribute attribute;

		for (IRelationshipRole role : this.relshipsSelected.keySet()) {
			relation = (IRelationship) role.getParentElement();
			attribute = this.relshipsSelected.get(role);

			name = Utilities.getAttribute(role, "name");
			type = Utilities.getAttribute(attribute, "type");

			maxCard1 = Utilities.getAttribute(relation.getRelationshipRole1(), "maxCard");
			maxCard2 = Utilities.getAttribute(relation.getRelationshipRole2(), "maxCard");

			if (maxCard1.equals("N") && maxCard2.equals("N")) {
				field = cmd.addSubUnit(Utilities.getSubUnitType(element, "MultiSelectionField"), element);
			} else {
				field = cmd.addSubUnit(Utilities.getSubUnitType(element, "SelectionField"), element);
			}

			Utilities.setAttribute(field, "type", type);

			new SetAttributeMFOperation(field, "name", name, element.getRootElement()).execute();
		}
	}

	@Override
	public WebRatioElement getCopy() {
		return new EntryUnit(this.id, this.name, this.parentId, this.type, String.valueOf(this.position.x),
				String.valueOf(this.position.y), this.entity);
	}
}
