package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationshipRole;

public class ReconnectUnit extends Unit {
	private IRelationshipRole role;
	private IMFElement parent;

	public ReconnectUnit(String id, String name, String x, String y, IEntity entity, IRelationshipRole role) {
		super(id, name, x, y, entity);
		this.role = role;
	}

	/**
	 * Set the SiteView or Area which is the parent of the unit
	 * 
	 * @param parent
	 *            the SiteView or Area
	 */

	public void setParent(IMFElement parent) {
		this.parent = parent;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		String idRole = Utilities.getAttribute(this.role, "id");
		Evento evento = new EventoNuevaUnit(this.parent, ElementType.RECONNECT_UNIT, this.position.x, this.position.y, this.name,
				this.entity);
		IMFElement reconnectUnit = evento.ejecutar();
		Utilities.setAttribute(reconnectUnit, "relationship", idRole);

		return reconnectUnit;
	}

}
