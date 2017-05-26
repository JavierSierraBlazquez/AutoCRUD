package org.homeria.webratioassistant.units;

import java.util.Map;

import org.eclipse.swt.graphics.Point;
import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaPagina;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.ISiteView;

public class Page extends WebRatioElement {

	private ISiteView siteView;
	protected Point position;

	public Page(String id, String name, String x, String y) {
		super(id, name);
		this.position = new Point(Integer.valueOf(x), Integer.valueOf(y));
	}

	public void setSiteView(ISiteView siteView) {
		this.siteView = siteView;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		Evento evento = new EventoNuevaPagina(this.siteView, this.position.x, this.position.y, this.name);
		return evento.ejecutar();
	}

}
