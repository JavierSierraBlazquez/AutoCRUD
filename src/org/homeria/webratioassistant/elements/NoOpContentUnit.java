package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.webratio.WebRatioCalls;
import org.homeria.webratioassistant.webratio.NewUnit;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;

public class NoOpContentUnit extends Unit {

	private String parentId;

	public NoOpContentUnit(String id, String name, String parentId, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
		this.parentId = parentId;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent = createdElements.get(this.parentId);

		WebRatioCalls newUnitWRCall = new NewUnit(parent, ElementTypes.NO_OP_CONTENT_UNIT, this.position.x, this.position.y, this.name,
				this.entity);

		return newUnitWRCall.execute();
	}

	@Override
	public WebRatioElement getCopy() {
		return new NoOpContentUnit(this.id, this.name, this.parentId, String.valueOf(this.position.x), String.valueOf(this.position.y),
				this.entity);
	}
}
