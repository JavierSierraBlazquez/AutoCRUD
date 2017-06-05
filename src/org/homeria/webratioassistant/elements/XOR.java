package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.eclipse.swt.graphics.Point;
import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaAlternantiva;

import com.webratio.commons.mf.IMFElement;

public class XOR extends WebRatioElement {

	private Point position;
	private String parentId;

	public XOR(String id, String name, String parentId, String x, String y) {
		super(id, name);
		this.position = new Point(Integer.valueOf(x), Integer.valueOf(y));
		this.parentId = parentId;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent = createdElements.get(this.parentId);

		Evento evento = new EventoNuevaAlternantiva(parent, this.position.x, this.position.y, this.name);
		return evento.ejecutar();
	}

}
