package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.webratio.WebRatioCalls;
import org.homeria.webratioassistant.webratio.NewLink;

import com.webratio.commons.mf.IMFElement;

public class KOLink extends Link {
	private String message;

	public KOLink(String id, String name, String sourceId, String targetId, String message, String type) {
		super(id, name, sourceId, targetId, type);
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

		WebRatioCalls newLinkWRCall = new NewLink(this.name, source, target, "KOLink");
		IMFElement link = newLinkWRCall.execute();

		if (this.type.equals(ElementTypes.KO_LINK_NO_COUPLING)) {
			this.removeAutomaticCoupling(link);

		} else if (!this.message.isEmpty()) {
			this.putMessageOnMultiMessageUnit(link, target, this.message);
			this.removeAutomaticCoupling(link);
		}

		return link;
	}

	@Override
	public WebRatioElement getCopy() {
		return new KOLink(this.id, this.name, this.sourceId, this.targetId, this.message, this.type);
	}
}
