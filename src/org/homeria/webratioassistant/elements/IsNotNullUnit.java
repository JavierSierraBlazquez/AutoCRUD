package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.homeria.webratioassistant.webratio.WebRatioCalls;
import org.homeria.webratioassistant.webratio.NewUnit;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;

public class IsNotNullUnit extends UnitOutsidePage {

	public IsNotNullUnit(String id, String name, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {

		WebRatioCalls newUnitWRCall = new NewUnit(this.parent, ElementTypes.IS_NOT_NULL_UNIT, this.position.x, this.position.y, this.name,
				this.entity);
		return newUnitWRCall.execute();
	}

	@Override
	public WebRatioElement getCopy() {
		return new IsNotNullUnit(this.id, this.name, String.valueOf(this.position.x), String.valueOf(this.position.y), this.entity);
	}

}
