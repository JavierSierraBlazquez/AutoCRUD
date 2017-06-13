package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;

public class DeleteUnit extends UnitOutsidePage {

	public DeleteUnit(String id, String name, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {

		Evento evento = new EventoNuevaUnit(this.parent, ElementType.DELETE_UNIT, this.position.x, this.position.y, this.name, this.entity);
		return evento.ejecutar();
	}

}
