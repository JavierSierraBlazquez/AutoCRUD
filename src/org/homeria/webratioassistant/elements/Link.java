package org.homeria.webratioassistant.elements;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.homeria.webratioassistant.webratio.ProjectParameters;
import org.homeria.webratioassistant.webratio.Utilities;

import com.webratio.commons.internal.mf.MFElement;
import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.operations.SetAttributeMFOperation;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.ILinkParameter;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISubUnit;
import com.webratio.ide.model.IUnit;

public abstract class Link extends WebRatioElement {

	protected String sourceId;
	protected String targetId;
	protected String type;

	public Link(String id, String name, String sourceId, String targetId, String type) {
		super(id, name, null, null);
		this.sourceId = sourceId;
		this.targetId = targetId;
		this.type = type;
	}

	public String getSourceId() {
		return this.sourceId;
	}

	public String getTargetId() {
		return this.targetId;
	}

	protected void removeAutomaticCoupling(IMFElement link) {
		Utilities.setAttribute(link, "automaticCoupling", null);
	}

	/**
	 * Nombre: guessCouplingEntryToCreateModify Funcion: Simula el guessCoupling entre la entryUnit y la Create o Modify unit
	 * 
	 * @param source
	 *            : entry unit
	 * @param target
	 *            : unidad create o modify
	 * @param link
	 *            : link sobre el que añadir el linkParameter
	 * @param relshipsSelected
	 * @param entity
	 */
	protected void guessCouplingEntryToCreateModify(IMFElement source, IMFElement target, IMFElement link, IEntity entity,
			Map<IRelationshipRole, IAttribute> relshipsSelected) {
		// Variables
		String fieldType;
		IMFElement linkParameter;
		IRelationshipRole role;
		String keyAtt;
		IEntity guessEntity = entity;

		// Getting field list
		List<ISubUnit> fieldList = ((IUnit) source).getSubUnitList();
		// Get list of source entity attributes
		List<IAttribute> attList = (guessEntity).getAllAttributeList();

		String targetType = target.getQName().getName();

		// Generate maps for lists of fields and attributes
		Map<String, IAttribute> attMap = new HashMap<String, IAttribute>();
		Map<String, ISubUnit> fieldMap = new HashMap<String, ISubUnit>();

		// Init maps
		for (ISubUnit field : fieldList)
			fieldMap.put(Utilities.getAttribute(field, "name"), field);

		for (IAttribute attribute : attList)
			attMap.put(Utilities.getAttribute(attribute, "name"), attribute);

		IAttribute attribute;
		ISubUnit field;

		for (String fieldName : fieldMap.keySet()) {
			attribute = attMap.get(fieldName);
			// If it returns an attribute is a coupling by attribute
			if (attribute != null) {
				keyAtt = Utilities.getAttribute(attribute, "key");

				if (!keyAtt.equals("true")) {
					linkParameter = this.createParameterField2Att(attribute, target, fieldMap.get(fieldName), link, false);
					((MFElement) link).addChild(linkParameter, null);
				} else {
					if (targetType.equals("ModifyUnit")) {

						linkParameter = this.createParameterField2Att(attribute, target, fieldMap.get(fieldName), link, true);
						((MFElement) link).addChild(linkParameter, null);
					}
				}
			} else {
				// Otherwise it is a coupling with selection or multiselection
				role = this.findRelation(fieldName, entity);

				field = fieldMap.get(fieldName);
				fieldType = field.getQName().getName();
				if (fieldType.equals("SelectionField")) {
					linkParameter = this.createParameterFieldToRole(role, target, field, link, relshipsSelected);
					((MFElement) link).addChild(linkParameter, null);
				}
			}
		}
	}

	/**
	 * Nombre: createParameterFieldToRole Funcion: Crea un parametro desde un campo de un formulario con una roleCondition. El procedimiento
	 * es igual a las anteriores, solo variando el nombre y algunos datos mas
	 * 
	 * @param role
	 *            : role que queremos conectar
	 * @param targetUnit
	 *            : unidad en la que estará la roleCondition (createUnit, modifyUnit...)
	 * @param field
	 *            : campo del formulario
	 * @param link
	 *            : link que contendrá el parametro
	 * @param relshipsSelected
	 * @return: el parametro creado
	 */
	private IMFElement createParameterFieldToRole(IRelationshipRole role, IMFElement targetUnit, ISubUnit field, IMFElement link,
			Map<IRelationshipRole, IAttribute> relshipsSelected) {
		IMFElement linkParameter;
		String roleName = Utilities.getAttribute(role, "name");
		String idRole = Utilities.getAttribute(role, "id");
		IAttribute attribute = relshipsSelected.get(role);
		IEntity parentEntity = (IEntity) attribute.getParentElement();
		String entityName = Utilities.getAttribute(parentEntity, "name");

		List<IAttribute> attList = parentEntity.getAllAttributeList();

		// Browse the attributes by searching for the keyCondition
		for (int i = 0; i < attList.size(); i++) {
			if (Utilities.getAttribute(attList.get(i), "key").equals("true")) {
				attribute = attList.get(i);
				break;
			}
		}
		// As in the previous methods, you create the necessary fields (id, name, source, target)
		String attId = Utilities.getAttribute(attribute, "id");
		String attName = Utilities.getAttribute(attribute, "name");
		linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());

		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "name", roleName + "_" + entityName + "." + attName + "(" + roleName + ")",
				link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(field.getIdsByFinalId().toString()), link.getRootElement())
				.execute();
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(targetUnit.getIdsByFinalId().toString()) + "." + idRole + "."
				+ attId, link.getRootElement()).execute();

		return linkParameter;
	}

	/**
	 * Nombre: createParameterField2Att Funcion: Su funcionamiento es igual que la funcion createParameter pero de manera inversa, se crea
	 * la relacion entre el campo del formulario y el atributo de una unidad (create, modify...)
	 * 
	 * @param attribute
	 *            : atributo que queremos relacionar
	 * @param sourceUnit
	 *            : unidad sobre la que trabajar
	 * @param subUnit
	 *            : field
	 * @param link
	 *            : link que contendrá el parametro
	 * @param key
	 *            : para indicar si es un oid
	 * @return: el parametro creado
	 */
	private IMFElement createParameterField2Att(IAttribute attribute, IMFElement sourceUnit, ISubUnit subUnit, IMFElement link, boolean key) {
		IMFElement linkParameter;
		IMFElement field = subUnit;

		String name = Utilities.getAttribute(field, "name");

		String attId = Utilities.getAttribute(attribute, "id");

		linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();
		// If it is not of type key (to relate the oid) it is done just like the previous function.
		if (!key) {
			new SetAttributeMFOperation(linkParameter, "name", name + "_" + name, link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(field.getIdsByFinalId().toString()), link.getRootElement())
					.execute();

			new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(sourceUnit.getIdsByFinalId().toString()) + "." + attId,
					link.getRootElement()).execute();
		} else {
			// If it is related to an oid you need the keyCondition of the element to create the parameter of the link, besides changing the
			// format of the name
			IMFElement keyCondition = sourceUnit.selectSingleElement("Selector").selectSingleElement("KeyCondition");
			String keyName = Utilities.getAttribute(keyCondition, "name");

			new SetAttributeMFOperation(linkParameter, "name", name + "_" + keyName + " [oid]", link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(field.getIdsByFinalId().toString()), link.getRootElement())
					.execute();

			new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(keyCondition.getIdsByFinalId().toString()) + "." + attId,
					link.getRootElement()).execute();

		}
		return linkParameter;
	}

	/**
	 * Nombre: putMessageOnMultiMessageUnit Funcion: añade un mensaje a un link OK o linkKO que van dirigidos a una multiMessageUnit
	 * 
	 * @param link
	 *            : link al que añadir el mensaje
	 * @param target
	 *            : multiMessageUnit que mostrará el mensaje
	 * @param message
	 *            : mensaje a mostrar
	 */
	protected void putMessageOnMultiMessageUnit(IMFElement link, IMFElement target, String message) {
		ILinkParameter linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());

		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "name", message + "_" + "Shown Messages", link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "sourceValue", message, link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(target.getIdsByFinalId().toString()) + ".shownMessages",
				link.getRootElement()).execute();
		((MFElement) link).addChild(linkParameter, null);
	}

	/**
	 * Nombre: cleanIds Funcion: Funcion auxiliar, limpia una cadena eliminando el primer y ultimo caracter
	 * 
	 * @param string
	 *            : string que se quiere tratar
	 * @return: cadena resultante
	 */
	protected String cleanIds(String string) {
		return string.substring(1, string.length() - 1);
	}

	/**
	 * Nombre: buscarRelation Funcion: Busca una relacion por su nombre
	 * 
	 * @param fieldName
	 *            : nombre por el que buscar
	 * @return la relacion si se encuentra, null en caso contrario
	 */
	protected IRelationshipRole findRelation(String fieldName, IEntity entity) {

		List<IRelationship> relationList = entity.getIncomingRelationshipList();
		relationList.addAll(entity.getOutgoingRelationshipList());

		IRelationship relation;
		IRelationshipRole role;
		for (Iterator<IRelationship> iter = relationList.iterator(); iter.hasNext();) {
			relation = iter.next();
			role = relation.getRelationshipRole1();
			if (Utilities.getAttribute(role, "name").equals(fieldName)) {
				return role;
			}
			role = relation.getRelationshipRole2();
			if (Utilities.getAttribute(role, "name").equals(fieldName)) {
				return role;
			}
		}
		return null;
	}
}
