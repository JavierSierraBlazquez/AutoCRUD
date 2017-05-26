package org.homeria.webratioassistant.units;

import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevoLink;

import com.webratio.commons.mf.IMFElement;

public class OKLink extends Link {
	private String message;

	public OKLink(String id, String name, String sourceId, String destinyId, String message) {
		super(id, name, sourceId, destinyId);
		if (null == message)
			this.message = "";
		else
			this.message = message;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement source = createdElements.get(this.sourceId);
		IMFElement target = createdElements.get(this.targetId);

		Evento evento = new EventoNuevoLink(this.name, source, target, "OKLink");
		IMFElement link = evento.ejecutar();

		if (!this.message.isEmpty()) {
			this.putMessageOnMultiMessageUnit(link, target, this.message);
			this.removeAutomaticCoupling(link);
		}

		return link;
	}

}
