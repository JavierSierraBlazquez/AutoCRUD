package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.eclipse.draw2d.geometry.Point;

import com.webratio.commons.mf.IMFElement;

public abstract class WebRatioElement {
	protected String id;
	protected String name;
	protected Point position;

	public WebRatioElement(String id, String name, String x, String y) {
		this.id = id;
		this.name = name;

		if (x == null || y == null)
			this.position = null;
		else
			try {
				this.position = new Point(Integer.valueOf(x), Integer.valueOf(y));
			} catch (NumberFormatException e) {
				this.position = new Point(0, 0);
			}
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public abstract WebRatioElement getCopy();

	public abstract IMFElement generate(Map<String, IMFElement> createdElements);
}
