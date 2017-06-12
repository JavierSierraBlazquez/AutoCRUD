package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;

public class IsNotNullUnit extends Unit {
	private IMFElement parent;

	public IsNotNullUnit(String id, String name, String parentId, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
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

		Evento evento = new EventoNuevaUnit(this.parent, ElementType.IS_NOT_NULL_UNIT, this.position.x, this.position.y, this.name,
				this.entity);
		return evento.ejecutar();
	}

}
