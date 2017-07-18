package org.homeria.webratioassistant.elements;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.homeria.webratioassistant.webratio.NewLink;
import org.homeria.webratioassistant.webratio.ProjectParameters;
import org.homeria.webratioassistant.webratio.Utilities;
import org.homeria.webratioassistant.webratio.WebRatioCalls;

import com.webratio.commons.internal.mf.MFElement;
import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.operations.SetAttributeMFOperation;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.ILinkParameter;
import com.webratio.ide.model.IOperationUnit;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.IUnit;

public class NormalNavigationFlow extends Link {
	private IEntity entity;
	private Map<IRelationshipRole, IAttribute> relshipsSelected;
	private boolean validate;

	public NormalNavigationFlow(String id, String name, String sourceId, String targetId, String type, String validate, IEntity entity) {
		super(id, name, sourceId, targetId, type);
		this.entity = entity;

		if (validate.equals("false"))
			this.validate = false;
		else
			this.validate = true;
	}

	public void setRelshipsSelected(Map<IRelationshipRole, IAttribute> relshipsSelected) {
		this.relshipsSelected = relshipsSelected;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement source = createdElements.get(this.sourceId);
		IMFElement target = createdElements.get(this.targetId);

		WebRatioCalls evento = new NewLink(this.name, source, target, "normal");
		IMFElement link = evento.execute();

		if (this.type.equals(ElementTypes.FLOW_ENTRY_TO_CREATE) || this.type.equals(ElementTypes.FLOW_ENTRY_TO_UPDATE)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingEntryToCreateModify(source, target, link, this.entity, this.relshipsSelected);

		} else if (this.type.equals(ElementTypes.NORMALFLOW_FIXED_VALUE)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingFixedValue(this.entity, target, link, String.valueOf(0));
		} else if (this.type.equals(ElementTypes.NORMALFLOW_IS_NOT_NULL)) {
			IMFElement oidField = ProjectParameters.entryKeyfieldMap.get(source);

			this.putCoupling(oidField, (IOperationUnit) target, "isnotnull", link);
		}

		if (this.validate == false)
			Utilities.setAttribute(link, "validate", "false");

		return link;
	}

	/**
	 * Parameter de Link desde origen a destino para una keycondition en la cual en el mapeo el origen va a ser un valor fijo.
	 * 
	 */
	private void guessCouplingFixedValue(IEntity sourceEntity, IMFElement target, IMFElement link, String value) {

		// Use to keyCondition:

		// <Link id="ln3" name="New Town" to="seu1" type="normal" validate="true">
		// <LinkParameter id="par21" name="0_KeyCondition3 [oid]" sourceValue="0" target="kcond3.att2"/>
		// </Link>

		IAttribute attribute;
		String fieldName;
		IMFElement linkParameter;
		String keyAtributo;

		try {
			// Get list of source entity attributes
			List<IAttribute> attList = sourceEntity.getAllAttributeList();

			// Generate map for the lists of fields and attributes
			Map<String, IAttribute> attMap = new HashMap<String, IAttribute>();

			for (Iterator<IAttribute> iter = attList.iterator(); iter.hasNext();) {
				attribute = iter.next();

				keyAtributo = Utilities.getAttribute(attribute, "key");
				if (keyAtributo.equals("true")) {
					attMap.put(Utilities.getAttribute(attribute, "name"), attribute);
				}
			}

			if (null != attMap && attMap.size() > 0) {
				for (Iterator<String> iter = attMap.keySet().iterator(); iter.hasNext();) {
					fieldName = iter.next();
					attribute = attMap.get(fieldName);
					// If it returns an attribute is a coupling by attribute
					if (attribute != null) {
						keyAtributo = Utilities.getAttribute(attribute, "key");
						if (keyAtributo.equals("true")) {
							linkParameter = this.createParameterField2AttValor(attribute, target, value, link, true);
							((MFElement) link).addChild(linkParameter, null);
						}
					}
				}
			}
		} catch (Exception e) {
			// nothing to do, its only a linkParameter
		}
	}

	private IMFElement createParameterField2AttValor(IAttribute attribute, IMFElement targetUnit, String value, IMFElement link, boolean key) {

		IMFElement linkParameter;
		String idAtt = Utilities.getAttribute(attribute, "id");

		linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();
		// If it is not of type key (to relate the oid) it is done just like the previous function.
		if (key) {
			// If it is related to an oid you need the keyCondition of the element to create the parameter of the link, besides changing the
			// format of the name
			IMFElement keyCondition = targetUnit.selectSingleElement("Selector").selectSingleElement("KeyCondition");
			String nameKey = Utilities.getAttribute(keyCondition, "name");

			new SetAttributeMFOperation(linkParameter, "name", value + "_" + nameKey + " [oid]", link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "sourceValue", value, link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(keyCondition.getIdsByFinalId().toString()) + "." + idAtt,
					link.getRootElement()).execute();

		}
		return linkParameter;
	}

	/**
	 * Nombre: putCoupling Funcion: Pone un coupling entre dos unidades, especialmente se usa para las unidades isNotNullUnit
	 * 
	 * @param oidField
	 *            : campo del formulario/entidad que se usa para el copling
	 * @param target
	 *            : unidad de destino (isNotNullUnit)
	 * @param tipo
	 *            : tipo de coupling (por ejemplo isnotnull)
	 * @param link
	 *            : link en el que se crea el coupling
	 */
	private void putCoupling(IMFElement oidField, IUnit target, String tipo, IMFElement link) {
		// The automaticCoupling of the link is removed, to do it manually
		Utilities.setAttribute(link, "automaticCoupling", null);
		String name = Utilities.getAttribute(oidField, "name").toLowerCase();

		ILinkParameter linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "name", name + "_" + name, link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(oidField.getIdsByFinalId().toString()), link.getRootElement())
				.execute();
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(target.getIdsByFinalId().toString()) + "." + tipo,
				link.getRootElement()).execute();

		((MFElement) link).addChild(linkParameter, null);
	}

	@Override
	public WebRatioElement getCopy() {
		return new NormalNavigationFlow(this.id, this.name, this.sourceId, this.targetId, this.type, String.valueOf(this.validate),
				this.entity);
	}
}
