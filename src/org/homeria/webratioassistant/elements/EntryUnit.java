package org.homeria.webratioassistant.elements;

import java.util.List;
import java.util.Map;

import org.homeria.webratioassistant.webratio.WebRatioCalls;
import org.homeria.webratioassistant.webratio.NewUnit;
import org.homeria.webratioassistant.webratio.ProjectParameters;
import org.homeria.webratioassistant.webratio.Utilities;
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

		WebRatioCalls evento = new NewUnit(parent, ElementType.ENTRY_UNIT, this.position.x, this.position.y, this.name, this.entity);
		IMFElement entryUnit = evento.execute();
		this.setFields(entryUnit);

		ProjectParameters.entryKeyfieldMap.put(entryUnit, this.fieldOid);
		return entryUnit;

	}

	private void setFields(IMFElement element) {
		CompositeMFCommand cmd = new CompositeMFCommand(element.getRootElement());
		String tipo;
		IMFElement field;
		String nombre;

		List<IAttribute> listaAtributos = this.entity.getAllAttributeList();
		// Recorremos la lista final de atributos para crear los campos en el formulario
		for (IAttribute atributo : listaAtributos) {
			// No att. derivados:
			if (null == Utilities.getAttribute(atributo, "derivationQuery")
					|| Utilities.getAttribute(atributo, "derivationQuery").isEmpty()) {
				nombre = Utilities.getAttribute(atributo, "name");
				tipo = Utilities.getAttribute(atributo, "type");
				field = cmd.addSubUnit(Utilities.getSubUnitType(element, "Field"), element);
				// Si el tipo es password lo pasamos a tipo string por comodidad a la hora de modificar el formulario
				if (tipo.equals("password"))
					tipo = "string";
				Utilities.setAttribute(field, "type", tipo);

				if (this.type.equals(ElementType.ENTRYUNIT_PRELOADED))
					Utilities.setAttribute(field, "preloaded", "true");

				new SetAttributeMFOperation(field, "name", nombre, element.getRootElement()).execute();

				if ((Utilities.getAttribute(atributo, "name").contains("oid") || Utilities.getAttribute(atributo, "name").contains("OID"))
						&& Utilities.getAttribute(atributo, "key").equals("true")) {
					Utilities.setAttribute(field, "hidden", "true");
					Utilities.setAttribute(field, "modifiable", "false");
					this.fieldOid = field;

					// Mantener el contenType de los campos en el formulario.
					if (null != Utilities.getAttribute(atributo, "contentType")) {
						Utilities.setAttribute(field, "contentType", Utilities.getAttribute(atributo, "contentType"));
					}
				}
			}
		}

		// Ahora hacemos lo mismo creando selectionField en caso de relaciones
		// 1aN y multiSelectionField en caso de relaciones NaN
		IRelationship relation;
		String maxCard1;
		String maxCard2;
		IAttribute atributo;

		for (IRelationshipRole role : this.relshipsSelected.keySet()) {
			relation = (IRelationship) role.getParentElement();
			atributo = this.relshipsSelected.get(role);

			nombre = Utilities.getAttribute(role, "name");
			tipo = Utilities.getAttribute(atributo, "type");

			maxCard1 = Utilities.getAttribute(relation.getRelationshipRole1(), "maxCard");
			maxCard2 = Utilities.getAttribute(relation.getRelationshipRole2(), "maxCard");

			if (maxCard1.equals("N") && maxCard2.equals("N")) {
				field = cmd.addSubUnit(Utilities.getSubUnitType(element, "MultiSelectionField"), element);
			} else {
				field = cmd.addSubUnit(Utilities.getSubUnitType(element, "SelectionField"), element);
			}

			Utilities.setAttribute(field, "type", tipo);

			new SetAttributeMFOperation(field, "name", nombre, element.getRootElement()).execute();
		}
	}

	@Override
	public WebRatioElement getCopy() {
		return new EntryUnit(this.id, this.name, this.parentId, this.type, String.valueOf(this.position.x), String.valueOf(this.position.y),
				this.entity);
	}
}
