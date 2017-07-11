/** 
 * @author Javier Sierra Blázquez
 * */
package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.webratio.WebRatioCalls;
import org.homeria.webratioassistant.webratio.NewUnit;
import org.homeria.webratioassistant.webratio.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationshipRole;

/**
 * This class contains the data previously parsed that is needed to generate the ConnectUnit using generate method
 */
public class ConnectUnit extends UnitOutsidePage {
	private IRelationshipRole role;

	public ConnectUnit(String id, String name, String x, String y, IEntity entity, IRelationshipRole role) {
		super(id, name, x, y, entity);
		this.role = role;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#generate(java.util.Map)
	 */
	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		String idRole = Utilities.getAttribute(this.role, "id");
		WebRatioCalls evento = new NewUnit(this.parent, ElementType.CONNECT_UNIT, this.position.x, this.position.y, this.name, this.entity);
		IMFElement connectUnit = evento.execute();
		Utilities.setAttribute(connectUnit, "relationship", idRole);
		return connectUnit;
	}

	@Override
	public WebRatioElement getCopy() {
		return new ConnectUnit(this.id, this.name, String.valueOf(this.position.x), String.valueOf(this.position.y), this.entity, this.role);
	}

}
