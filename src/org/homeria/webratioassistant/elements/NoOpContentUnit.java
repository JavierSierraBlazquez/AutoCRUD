package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;

public class NoOpContentUnit extends Unit {

	private String parentId;

	public NoOpContentUnit(String id, String name, String parentId, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
		this.parentId = parentId;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent = createdElements.get(this.parentId);

		Evento evento = new EventoNuevaUnit(parent, ElementType.NO_OP_CONTENT_UNIT, this.position.x, this.position.y, this.name,
				this.entity);

		return evento.ejecutar();
	}

	@Override
	public WebRatioElement getCopy() {
		return new NoOpContentUnit(this.id, this.name, this.parentId, String.valueOf(this.position.x), String.valueOf(this.position.y),
				this.entity);
	}
}
