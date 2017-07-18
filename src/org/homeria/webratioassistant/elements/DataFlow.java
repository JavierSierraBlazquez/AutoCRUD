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
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISubUnit;
import com.webratio.ide.model.IUnit;

public class DataFlow extends Link {
	private IRelationshipRole role;
	private IEntity entity;
	private Map<IRelationshipRole, IAttribute> relshipsSelected;

	public DataFlow(String id, String name, String sourceId, String targetId, String type, IEntity entity, IRelationshipRole role) {
		super(id, name, sourceId, targetId, type);
		this.entity = entity;
		this.role = role;

	}

	public DataFlow(String id, String name, String sourceId, String targetId, String type, IEntity entity) {
		super(id, name, sourceId, targetId, type);
		this.type = type;
		this.entity = entity;
	}

	public void setRelshipsSelected(Map<IRelationshipRole, IAttribute> relshipsSelected) {
		this.relshipsSelected = relshipsSelected;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement source = createdElements.get(this.sourceId);
		IMFElement target = createdElements.get(this.targetId);

		WebRatioCalls newUnitWRCall = new NewLink(this.name, source, target, "transport");
		IMFElement link = newUnitWRCall.execute();

		if (this.type.equals(ElementTypes.DATAFLOW_PRELOAD)) {
			this.removeAutomaticCoupling(link);
			this.putPreload(target, this.role, link);

		} else if (this.type.equals(ElementTypes.DATAFLOW_ENTRY_TO_CONNECT) || this.type.equals(ElementTypes.DATAFLOW_ENTRY_TO_RECONNECT)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingEntryToConnect(source, target, this.getTargetEntity(this.role), this.role, link);

		} else if (this.type.equals(ElementTypes.DATAFLOW_UNIT_TO_ENTRY)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingUnitToEntry(source, target, this.entity, link);

		} else if (this.type.equals(ElementTypes.DATAFLOW_UNIT_TO_ENTRY_ROLE)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingUnitToEntry(source, target, this.entity, link, this.role);

		} else if (this.type.equals(ElementTypes.FLOW_ENTRY_TO_CREATE)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingEntryToCreateModify(source, target, link, this.entity, this.relshipsSelected);
		}

		return link;
	}

	/**
	 * Nombre: putPreload Funcion: Clase que se encarga de poner los datos preload dada una RelationShipRole
	 * 
	 * @param destino
	 *            : unidad destino, entryUnit
	 * @param role
	 *            : role que
	 * @param link
	 */
	private void putPreload(IMFElement destino, IRelationshipRole role, IMFElement link) {
		IEntity sourceEntity = this.getTargetEntity(role);
		IAttribute selectAtt = this.relshipsSelected.get(role);
		ISubUnit field;
		String fieldName;
		List<ISubUnit> fieldList = ((IUnit) destino).getSubUnitList();
		String roleName = Utilities.getAttribute(role, "name");
		IEntity entityPreload = this.getTargetEntity(role);
		List<IAttribute> attList = entityPreload.getAllAttributeList();
		IAttribute attribute = null;
		for (int i = 0; i < attList.size(); i++) {
			if (Utilities.getAttribute(attList.get(i), "key").equals("true")) {
				attribute = attList.get(i);
				break;
			}
		}

		for (Iterator<ISubUnit> iter = fieldList.iterator(); iter.hasNext();) {
			field = iter.next();
			fieldName = Utilities.getAttribute(field, "name");
			if (fieldName.equals(roleName)) {
				this.createParameterPreload(attribute, field, selectAtt, link, sourceEntity);
				break;
			}
		}
	}

	/**
	 * Nombre: getTargetEntity Funcion: Obtiene la entidad de destino dada una Role
	 * 
	 * @param role
	 *            : role de la que obtener la entidad destino
	 * @return: targetEntity
	 */
	private IEntity getTargetEntity(IRelationshipRole role) {
		IRelationship relation = (IRelationship) role.getParentElement();
		if (relation.getTargetEntity() == this.entity) {
			return relation.getSourceEntity();
		} else
			return relation.getTargetEntity();

	}

	/**
	 * Nombre: createParameter Funcion: Añade un parametro a un link, para conectar un atributo con un campo de formulario
	 * 
	 * @param attribute
	 *            : atributo que queremos conectar
	 * @param subUnit
	 *            : campo del formulario que queremos conectar
	 * @param link
	 *            : link que contendrá el parametro
	 * @return: el parametro creado
	 */
	private IMFElement createParameter(IAttribute attribute, ISubUnit subUnit, IMFElement link) {
		IMFElement linkParameter;
		IMFElement field = subUnit;
		String name = Utilities.getAttribute(field, "name");
		// We create a linkParameter with the necessary data
		linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		// Id: form of the id of the link plus the id of the elements
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();
		// Name: is formed with the name of the field
		new SetAttributeMFOperation(linkParameter, "name", name + "_" + name, link.getRootElement()).execute();
		// Source: with attribute data
		new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(attribute.getIdsByFinalId().toString()) + "Array",
				link.getRootElement()).execute();
		// Target: created with form field fields
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(field.getIdsByFinalId().toString()) + "_slot",
				link.getRootElement()).execute();

		return linkParameter;
	}

	/**
	 * Nombre: createParameterPreload Funcion: Crea un parametro en un link para poder hacer el preload de atributos
	 * 
	 * @param attribute
	 *            : atributo que queremos relacionar
	 * @param field
	 *            : campo que queremos precargar
	 * @param selectAtt
	 *            : atributo necesario para obtener el nombre del source
	 * @param link
	 *            : link que contendrá el paramtro
	 * @param sourceEntity
	 *            : entidad de la que proceden los atributos
	 */
	private void createParameterPreload(IAttribute attribute, ISubUnit field, IAttribute selectAtt, IMFElement link, IEntity sourceEntity) {
		IMFElement linkParameter;
		IEntity padre = sourceEntity;

		String id = this.cleanIds(link.getIdsByFinalId().toString());
		String source_label = this.cleanIds(selectAtt.getIdsByFinalId().toString()) + "Array";
		String source_output = this.cleanIds(attribute.getIdsByFinalId().toString()) + "Array";

		String name_label = Utilities.getAttribute(selectAtt, "name") + "_" + Utilities.getAttribute(padre, "name") + " [label]";
		String name_output = "oid_" + Utilities.getAttribute(padre, "name") + " [output]";

		linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", id + "#" + linkParameter.getFinalId(), link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "name", name_label, link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "source", source_label, link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(field.getIdsByFinalId().toString()) + "_label",
				link.getRootElement()).execute();
		((MFElement) link).addChild(linkParameter, null);

		linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", id + "#" + linkParameter.getFinalId(), link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "name", name_output, link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "source", source_output, link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(field.getIdsByFinalId().toString()) + "_output",
				link.getRootElement()).execute();
		((MFElement) link).addChild(linkParameter, null);
	}

	/**
	 * Nombre: guessCouplingEntryToConnect Funcion: Simula el GuessCoupling de webRatio entre una EntryUnit y una ConnectUnit para conectar
	 * el oid de la entidad con la Role que esta en la condicion de la ConnectUnit
	 * 
	 * @param source
	 *            : unidadOrigen (entryUnit)
	 * @param target
	 *            : unidadDestino (connect o disconnect unit)
	 * @param targetEntity
	 *            : Entidad de la conectUnit, no tiene por que ser la misma que la del CRUD
	 * @param role
	 *            : role que esta en la roleCondition de la unidad destino
	 * @param link
	 *            : link que enlaza la unidad origen y destino, para añadirle el linkParameter
	 */
	private void guessCouplingEntryToConnect(IMFElement source, IMFElement target, IEntity targetEntity, IRelationshipRole role,
			IMFElement link) {
		// Obtenemos la keyCondition de la unidadDestino
		IOperationUnit connectUnit = (IOperationUnit) target;
		IMFElement keyCondition = connectUnit.selectSingleElement("TargetSelector").selectSingleElement("KeyCondition");
		String name = Utilities.getAttribute(keyCondition, "name");
		String nameToSearch = Utilities.getAttribute(targetEntity, "name");

		IAttribute keyAtt = null;
		IUnit entryUnit;
		entryUnit = (IUnit) source;
		// We get the list of form fields and the list of attributes of the target entity
		List<ISubUnit> fieldList = entryUnit.getSubUnitList();
		List<IAttribute> attList = targetEntity.getAllAttributeList();

		// We look for the attribute that works as key
		for (int i = 0; i < attList.size(); i++) {
			if (Utilities.getAttribute(attList.get(i), "key").equals("true")) {
				keyAtt = attList.get(i);
				break;
			}
		}

		// We create a hashMap with the name of the field and the field.
		Map<String, ISubUnit> fieldMap = new HashMap<String, ISubUnit>();
		Iterator<ISubUnit> fieldIterator = fieldList.iterator();
		ISubUnit field;
		while (fieldIterator.hasNext()) {
			field = fieldIterator.next();
			fieldMap.put(Utilities.getAttribute(field, "name"), field);
		}

		// We get the field that relates to the role
		field = fieldMap.get(Utilities.getAttribute(role, "name"));

		// We create the link parameter that we add to the link.
		ILinkParameter linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(field.getIdsByFinalId().toString()), link.getRootElement())
				.execute();

		new SetAttributeMFOperation(linkParameter, "name", nameToSearch + "_" + name + " [oid] [" + nameToSearch + "] [Target]",
				link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(keyCondition.getIdsByFinalId().toString()) + "."
				+ this.cleanIds(keyAtt.getIdsByFinalId().toString()), link.getRootElement()).execute();

		((MFElement) link).addChild(linkParameter, null);
	}

	/**
	 * Nombre: guessCouplingUnitToEntry Funcion: Simula un guessCoupling entre cualquier unidad y una entryUnit llamando a los metodos
	 * creados anteriormente
	 * 
	 * @param source
	 *            : unidad (SelectorUnit, contentUnit...)
	 * @param target
	 *            : entryUnit
	 * @param sourceEntity
	 *            : entidad que esta seleccionada en la unidad de origen
	 * @param link
	 *            : link sobre el que se creara el linkParameter
	 */
	private void guessCouplingUnitToEntry(IMFElement source, IMFElement target, IEntity sourceEntity, IMFElement link) {

		// Get field list
		List<ISubUnit> fieldList = ((IUnit) target).getSubUnitList();
		// Get list of source entity attributes
		List<IAttribute> attList = sourceEntity.getAllAttributeList();

		// Generate maps for lists of fields and attributes
		Map<String, IAttribute> attMap = new HashMap<String, IAttribute>();
		Map<String, ISubUnit> fieldMap = new HashMap<String, ISubUnit>();

		// Init hashmaps
		for (ISubUnit field : fieldList)
			fieldMap.put(Utilities.getAttribute(field, "name"), field);

		for (IAttribute att : attList)
			attMap.put(Utilities.getAttribute(att, "name"), att);

		ISubUnit field;
		IAttribute attribute;
		String fieldType;
		IMFElement linkParameter;
		IRelationshipRole relationRole;

		for (String fieldName : fieldMap.keySet()) {
			attribute = attMap.get(fieldName);
			// If it returns an attribute is a coupling by attribute
			if (attribute != null) {
				linkParameter = this.createParameter(attribute, fieldMap.get(fieldName), link);
				((MFElement) link).addChild(linkParameter, null);
			} else {
				// Otherwise it is a coupling with selection or multiselection
				relationRole = this.findRelation(fieldName, this.entity);

				field = fieldMap.get(fieldName);
				fieldType = field.getQName().getName();
				if (fieldType.equals("SelectionField")) {
					linkParameter = this.createParameterRoleToField(relationRole, field, link, false);
					((MFElement) link).addChild(linkParameter, null);
				}
			}
		}
	}

	/**
	 * Nombre: guessCouplingUnitToEntry Funcion: Simula un guess Coupling entre cualquier unidad y la entryUnit
	 * 
	 * @param source
	 *            : unidad origen
	 * @param target
	 *            : unidad destino, en este caso entryUnit
	 * @param sourceEntity
	 *            : entidad que esta seleccionada en la unidad origen
	 * @param link
	 *            : link sobre el que se creara el linkPArameter
	 * @param role
	 *            : role que de la unidad origen, en caso de tenerla
	 */
	private void guessCouplingUnitToEntry(IMFElement source, IMFElement target, IEntity sourceEntity, IMFElement link,
			IRelationshipRole role) {
		ISubUnit field;
		ISubUnit preselect = null;
		String fieldName;
		IMFElement linkParameter;
		String roleName = Utilities.getAttribute(role, "name");
		// Get field list
		List<ISubUnit> listaFields = ((IUnit) target).getSubUnitList();

		// init hashMap
		for (Iterator<ISubUnit> iter = listaFields.iterator(); iter.hasNext();) {
			field = iter.next();
			fieldName = Utilities.getAttribute(field, "name");
			if (fieldName.contains(roleName)) {
				preselect = field;
				break;
			}
		}
		linkParameter = this.createParameterRoleToField(role, preselect, link, true);
		((MFElement) link).addChild(linkParameter, null);
	}

	/**
	 * Nombre: createParameterRoleToField Funcion: Crea los parametros del link que enlaza la role con un campo del formulario
	 * 
	 * @param role
	 *            : role de la que obtener el source del parametro
	 * @param field
	 *            : campo al que queremos enlazar
	 * @param link
	 *            : link que contendrá el parametro
	 * @param multi
	 *            : para distinguir entre selectionField y multiSelectionField
	 * @return: parametro creado.
	 */
	private IMFElement createParameterRoleToField(IRelationshipRole role, ISubUnit field, IMFElement link, boolean multi) {
		IMFElement linkParameter;
		String nameRole = Utilities.getAttribute(role, "name");
		String idRole = Utilities.getAttribute(role, "id");
		IAttribute attribute = this.relshipsSelected.get(role);
		IEntity parentEntity = (IEntity) attribute.getParentElement();

		List<IAttribute> attList = parentEntity.getAllAttributeList();

		for (int i = 0; i < attList.size(); i++) {
			if (Utilities.getAttribute(attList.get(i), "key").equals("true")) {
				attribute = attList.get(i);
				break;
			}
		}

		linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());

		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();

		if (multi) {
			new SetAttributeMFOperation(linkParameter, "name", Utilities.getAttribute(attribute, "name") + "_" + nameRole
					+ " - Preselection", link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(attribute.getIdsByFinalId().toString()) + "Array",
					link.getRootElement()).execute();
		} else {

			new SetAttributeMFOperation(linkParameter, "name", nameRole + ".oid_" + nameRole + " - Preselection", link.getRootElement())
					.execute();

			new SetAttributeMFOperation(linkParameter, "source", idRole + "_" + this.cleanIds(attribute.getIdsByFinalId().toString())
					+ "Array", link.getRootElement()).execute();
		}

		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(field.getIdsByFinalId().toString()) + "_presel",
				link.getRootElement()).execute();

		return linkParameter;
	}

	@Override
	public WebRatioElement getCopy() {
		return new DataFlow(this.id, this.name, this.sourceId, this.targetId, this.type, this.entity, this.role);
	}
}
