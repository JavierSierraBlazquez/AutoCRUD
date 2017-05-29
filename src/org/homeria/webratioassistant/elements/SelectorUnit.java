package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaSelector;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationshipRole;

public class SelectorUnit extends Unit {

	private String parentId;
	private String type;
	IRelationshipRole role;

	public SelectorUnit(String id, String name, String parentId, String type, String x, String y, IEntity entity, IRelationshipRole role) {
		super(id, name, x, y, entity);
		this.parentId = parentId;
		this.entity = entity;
		this.type = type;
		this.role = role;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent = createdElements.get(this.parentId);

		Evento evento = new EventoNuevaUnit(parent, ElementType.SELECTOR_UNIT, this.position.x, this.position.y, this.name, this.entity);

		IMFElement selector = evento.ejecutar();

		if (this.type.equals(ElementType.SELECTOR_KEYCONDITION)) {
			Evento eventoAddKey = new EventoNuevaSelector(selector, "KeyCondition");
			eventoAddKey.ejecutar();

		} else if (this.type.equals(ElementType.SELECTOR_ROLECONDITION)) {
			Evento eventoAddRole = new EventoNuevaSelector(selector, "RelationshipRoleCondition");

			IMFElement roleCondition = eventoAddRole.ejecutar();
			String idRole = Utilities.getAttribute(this.role, "id");
			Utilities.setAttribute(roleCondition, "role", idRole);
		}

		return selector;
	}

}
