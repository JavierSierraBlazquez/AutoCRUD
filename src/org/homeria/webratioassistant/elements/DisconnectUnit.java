package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationshipRole;

public class DisconnectUnit extends UnitOutsidePage {
	private IRelationshipRole role;

	public DisconnectUnit(String id, String name, String x, String y, IEntity entity, IRelationshipRole role) {
		super(id, name, x, y, entity);
		this.role = role;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		String idRole = Utilities.getAttribute(this.role, "id");
		Evento evento = new EventoNuevaUnit(this.parent, ElementType.DISCONNECT_UNIT, this.position.x, this.position.y, this.name,
				this.entity);
		IMFElement disconnectUnit = evento.ejecutar();
		Utilities.setAttribute(disconnectUnit, "relationship", idRole);

		return disconnectUnit;
	}

	@Override
	public WebRatioElement getCopy() {
		return new DisconnectUnit(this.id, this.name, String.valueOf(this.position.x), String.valueOf(this.position.y), this.entity, this.role);
	}

}
