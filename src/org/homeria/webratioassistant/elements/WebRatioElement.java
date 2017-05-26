package org.homeria.webratioassistant.elements;

import java.util.Map;

import com.webratio.commons.mf.IMFElement;

public abstract class WebRatioElement {
	protected String id;
	protected String name;

	public WebRatioElement(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public abstract IMFElement generate(Map<String, IMFElement> createdElements);

}
