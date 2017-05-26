package org.homeria.webratioassistant.units;

import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;
import org.homeria.webratioassistant.temporal.ElementType;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;

public class MultiMessageUnit extends Unit {

	private String parentId;

	public MultiMessageUnit(String id, String name, String parentId, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
		this.parentId = parentId;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent = createdElements.get(this.parentId);

		Evento evento = new EventoNuevaUnit(parent, ElementType.MULTI_MESSAGE_UNIT, this.position.x, this.position.y, this.name,
				this.entity);

		return evento.ejecutar();
	}

}
