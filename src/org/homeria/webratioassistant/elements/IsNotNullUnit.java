package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.ISiteView;

public class IsNotNullUnit extends Unit {

	private String parentId;
	private ISiteView siteView;

	public IsNotNullUnit(String id, String name, String parentId, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
		this.parentId = parentId;
	}

	public void setSiteView(ISiteView siteView) {
		this.siteView = siteView;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		Evento evento = new EventoNuevaUnit(this.siteView, ElementType.IS_NOT_NULL_UNIT, this.position.x, this.position.y, this.name, this.entity);

		return evento.ejecutar();
	}

}
