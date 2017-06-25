package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaSelector;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationshipRole;

public class SelectorUnit extends UnitOutsidePage {

	private String parentId;
	private String type;
	IRelationshipRole role;

	public SelectorUnit(String id, String name, String parentId, String type, String x, String y, IEntity entity, IRelationshipRole role) {
		super(id, name, x, y, entity);
		this.entity = entity;
		this.type = type;
		this.role = role;
	}

	public SelectorUnit(String id, String name, String parentId, String type, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
		this.entity = entity;
		this.type = type;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent;
		if (null == this.parentId || "" == this.parentId)
			parent = this.parent;
		else
			parent = createdElements.get(this.parentId);

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

	@Override
	public void addToCurrentPosition(Point coords) {
		if (null == this.parentId) {
			this.position.x += coords.x;
			this.position.y += coords.y;
		}
	}

	@Override
	public WebRatioElement getCopy() {
		return new SelectorUnit(this.id, this.name, this.parentId, this.type, String.valueOf(this.position.x),
				String.valueOf(this.position.y), this.entity, this.role);
	}
}
