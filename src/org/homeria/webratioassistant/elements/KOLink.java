package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevoLink;

import com.webratio.commons.mf.IMFElement;

public class KOLink extends Link {
	private String message;

	public KOLink(String id, String name, String sourceId, String destinyId, String message) {
		super(id, name, sourceId, destinyId);
		this.message = message;
		if (null == message)
			this.message = "";
		else
			this.message = message;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement source = createdElements.get(this.sourceId);
		IMFElement target = createdElements.get(this.targetId);

		Evento evento = new EventoNuevoLink(this.name, source, target, "KOLink");
		IMFElement link = evento.ejecutar();

		if (!this.message.isEmpty()) {
			this.putMessageOnMultiMessageUnit(link, target, this.message);
			this.removeAutomaticCoupling(link);
		}

		return link;
	}

}
