package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.webratio.NewUnit;
import org.homeria.webratioassistant.webratio.Utilities;
import org.homeria.webratioassistant.webratio.WebRatioCalls;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationshipRole;

public class ReconnectUnit extends UnitOutsidePage {
	private IRelationshipRole role;

	public ReconnectUnit(String id, String name, String x, String y, IEntity entity, IRelationshipRole role) {
		super(id, name, x, y, entity);
		this.role = role;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		String idRole = Utilities.getAttribute(this.role, "id");
		WebRatioCalls newUnitWRCall = new NewUnit(this.parent, ElementTypes.RECONNECT_UNIT, this.position.x, this.position.y, this.name,
				this.entity);
		IMFElement reconnectUnit = newUnitWRCall.execute();
		Utilities.setAttribute(reconnectUnit, "relationship", idRole);

		return reconnectUnit;
	}

	@Override
	public WebRatioElement getCopy() {
		return new ReconnectUnit(this.id, this.name, String.valueOf(this.position.x), String.valueOf(this.position.y), this.entity,
				this.role);
	}
}
