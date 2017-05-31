package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISiteView;

public class ReconnectUnit extends Unit {
	private IRelationshipRole role;
	private ISiteView siteView;

	public ReconnectUnit(String id, String name, String x, String y, IEntity entity, IRelationshipRole role) {
		super(id, name, x, y, entity);
		this.role = role;
	}

	public void setSiteView(ISiteView siteView) {
		this.siteView = siteView;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		String idRole = Utilities.getAttribute(this.role, "id");
		Evento evento = new EventoNuevaUnit(this.siteView, ElementType.RECONNECT_UNIT, this.position.x, this.position.y, this.name,
				this.entity);
		IMFElement reconnectUnit = evento.ejecutar();
		Utilities.setAttribute(reconnectUnit, "relationship", idRole);

		return reconnectUnit;
	}

}