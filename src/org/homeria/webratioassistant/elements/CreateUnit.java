package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;

/**
 * This class contains the data previously parsed that is needed to generate the CreateUnit using generate method
 */
public class CreateUnit extends Unit {
	private IMFElement parent;

	public CreateUnit(String id, String name, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
	}

	/**
	 * Set the SiteView which is the parent of the unit
	 * 
	 * @param parent
	 *            the SiteView
	 */
	public void setParent(IMFElement parent) {
		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#generate(java.util.Map)
	 */
	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {

		Evento evento = new EventoNuevaUnit(this.parent, ElementType.CREATE_UNIT, this.position.x, this.position.y, this.name, this.entity);
		return evento.ejecutar();
	}

}
