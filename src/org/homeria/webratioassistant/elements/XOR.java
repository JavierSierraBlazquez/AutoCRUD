package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.webratio.WebRatioCalls;
import org.homeria.webratioassistant.webratio.NewAlternative;

import com.webratio.commons.mf.IMFElement;

public class XOR extends WebRatioElement {

	private String parentId;

	public XOR(String id, String name, String parentId, String x, String y) {
		super(id, name, x, y);
		this.parentId = parentId;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent = createdElements.get(this.parentId);

		WebRatioCalls newAlternWRCall = new NewAlternative(parent, this.position.x, this.position.y, this.name);
		return newAlternWRCall.execute();
	}

	@Override
	public WebRatioElement getCopy() {
		return new XOR(this.id, this.name, this.parentId, String.valueOf(this.position.x), String.valueOf(this.position.y));
	}
}
