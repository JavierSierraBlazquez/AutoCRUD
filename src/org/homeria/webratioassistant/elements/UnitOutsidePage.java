package org.homeria.webratioassistant.elements;

import org.eclipse.draw2d.geometry.Point;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;

public abstract class UnitOutsidePage extends Unit {
	protected IMFElement parent;

	public UnitOutsidePage(String id, String name, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
	}

	/**
	 * Set the SiteView or Area which is the parent of the unit
	 * 
	 * @param parent
	 *            the SiteView or Area
	 */

	public void setParent(IMFElement parent) {
		this.parent = parent;
	}

	public void addToCurrentPosition(Point coords) {
		this.position.x += coords.x;
		this.position.y += coords.y;
	}
}
