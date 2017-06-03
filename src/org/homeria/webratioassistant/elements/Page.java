package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.eclipse.swt.graphics.Point;
import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaPagina;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.ISiteView;

public class Page extends WebRatioElement {

	private ISiteView siteView;
	private Point position;
	private String parentId;

	public Page(String id, String name, String parentId, String x, String y) {
		super(id, name);
		this.position = new Point(Integer.valueOf(x), Integer.valueOf(y));
		this.parentId = parentId;
	}

	public void setSiteView(ISiteView siteView) {
		this.siteView = siteView;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent;
		if (null == this.parentId)
			parent = this.siteView;
		else
			parent = createdElements.get(this.parentId);

		Evento evento = new EventoNuevaPagina(parent, this.position.x, this.position.y, this.name);
		return evento.ejecutar();
	}

}
