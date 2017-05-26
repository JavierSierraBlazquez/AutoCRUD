package org.homeria.webratioassistant.units;

import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;
import org.homeria.webratioassistant.temporal.ElementType;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.ISiteView;

public class CreateUnit extends Unit {
	private ISiteView siteView;

	public CreateUnit(String id, String name, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
	}

	public void setSiteView(ISiteView siteView) {
		this.siteView = siteView;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {

		Evento evento = new EventoNuevaUnit(this.siteView, ElementType.CREATE_UNIT, this.position.x, this.position.y, this.name,
				this.entity);
		return evento.ejecutar();
	}

}