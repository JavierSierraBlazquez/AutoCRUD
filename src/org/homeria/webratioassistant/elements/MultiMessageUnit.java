package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.webratio.WebRatioCalls;
import org.homeria.webratioassistant.webratio.NewUnit;

import com.webratio.commons.mf.IMFElement;

public class MultiMessageUnit extends Unit {

	private String parentId;

	public MultiMessageUnit(String id, String name, String parentId, String x, String y) {
		super(id, name, x, y, null);
		this.parentId = parentId;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent = createdElements.get(this.parentId);

		WebRatioCalls evento = new NewUnit(parent, ElementType.MULTI_MESSAGE_UNIT, this.position.x, this.position.y, this.name,
				this.entity);

		return evento.execute();
	}

	@Override
	public WebRatioElement getCopy() {
		return new MultiMessageUnit(this.id, this.name, this.parentId, String.valueOf(this.position.x), String.valueOf(this.position.y));
	}
}
