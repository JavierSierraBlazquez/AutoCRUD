package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISiteView;

public class ConnectUnit extends Unit {
	private IRelationshipRole role;
	private ISiteView siteView;

	public ConnectUnit(String id, String name, String x, String y, IEntity entity, IRelationshipRole role) {
		super(id, name, x, y, entity);
		this.role = role;
	}

	public void setSiteView(ISiteView siteView) {
		this.siteView = siteView;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		// String nombreRole = Utilities.getAttribute(this.role, "name");
		String idRole = Utilities.getAttribute(this.role, "id");
		Evento evento = new EventoNuevaUnit(this.siteView, ElementType.CONNECT_UNIT, this.position.x, this.position.y, this.name,
				this.entity);
		IMFElement connectUnit = evento.ejecutar();
		Utilities.setAttribute(connectUnit, "relationship", idRole);

		return connectUnit;
	}

}
