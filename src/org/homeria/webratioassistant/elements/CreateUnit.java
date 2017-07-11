package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.webratio.WebRatioCalls;
import org.homeria.webratioassistant.webratio.NewUnit;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;

/**
 * This class contains the data previously parsed that is needed to generate the CreateUnit using generate method
 */
public class CreateUnit extends UnitOutsidePage {

	public CreateUnit(String id, String name, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#generate(java.util.Map)
	 */
	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {

		WebRatioCalls evento = new NewUnit(this.parent, ElementType.CREATE_UNIT, this.position.x, this.position.y, this.name, this.entity);
		return evento.execute();
	}
	
	@Override
	public WebRatioElement getCopy() {
		return new CreateUnit(this.id, this.name, String.valueOf(this.position.x), String.valueOf(this.position.y), this.entity);
	}

}
