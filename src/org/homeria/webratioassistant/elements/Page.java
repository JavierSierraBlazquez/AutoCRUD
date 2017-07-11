package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.homeria.webratioassistant.webratio.WebRatioCalls;
import org.homeria.webratioassistant.webratio.NewPage;
import org.homeria.webratioassistant.webratio.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IAlternative;
import com.webratio.ide.model.IPage;
import com.webratio.ide.model.ISiteView;

public class Page extends WebRatioElement {

	private String parentId;
	private boolean isLandmark;
	private boolean isDefaultPage;

	private IMFElement parent;

	public Page(String id, String name, String parentId, String defaultPage, String landmark, String x, String y) {
		super(id, name, x, y);
		this.parentId = parentId;
		this.parent = null;

		if (defaultPage.equals("true"))
			this.isDefaultPage = true;
		else
			this.isDefaultPage = false;

		if (landmark.equals("true"))
			this.isLandmark = true;
		else
			this.isLandmark = false;

	}

	public void setParent(IMFElement parent) {
		this.parent = parent;
	}

	public IMFElement getParent() {
		return this.parent;
	}

	public void addToCurrentPosition(Point coords) {
		if (null == this.parentId && this.parent instanceof ISiteView) {
			this.position.x += coords.x;
			this.position.y += coords.y;
		}
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent;
		if (null == this.parentId)
			parent = this.parent;
		else
			parent = createdElements.get(this.parentId);

		if (this.isDefaultPage && parent instanceof IAlternative) {
			// Get the default page from XOR page
			IPage defaultPage = ((IAlternative) parent).getPageList().get(0);
			Utilities.setAttribute(defaultPage, "name", this.name);

			return defaultPage;
		}

		WebRatioCalls evento = new NewPage(parent, this.position.x, this.position.y, this.name, this.isLandmark);
		return evento.execute();
	}

	@Override
	public WebRatioElement getCopy() {
		return new Page(this.id, this.name, this.parentId, String.valueOf(this.isDefaultPage), String.valueOf(this.isLandmark),
				String.valueOf(this.position.x), String.valueOf(this.position.y));
	}
}
