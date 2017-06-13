package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaPagina;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IAlternative;
import com.webratio.ide.model.IPage;
import com.webratio.ide.model.ISiteView;

public class Page extends WebRatioElement {

	private IMFElement parent;
	private Point position;
	private String parentId;
	private boolean isLandmark;
	private boolean isDefaultPage;

	public Page(String id, String name, String parentId, String defaultPage, String landmark, String x, String y) {
		super(id, name);
		this.position = new Point(Integer.valueOf(x), Integer.valueOf(y));
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

		Evento evento = new EventoNuevaPagina(parent, this.position.x, this.position.y, this.name, this.isLandmark);
		return evento.ejecutar();
	}

}
