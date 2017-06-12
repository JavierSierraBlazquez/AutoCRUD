package org.homeria.webratioassistant.elements;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevoLink;
import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.plugin.Utilities;

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

	public DataFlow(String id, String name, String sourceId, String destinyId, String type, IEntity entity, IRelationshipRole role) {
		super(id, name, sourceId, destinyId, type);
		this.entity = entity;
		this.role = role;

	}

	public DataFlow(String id, String name, String sourceId, String destinyId, String type, IEntity entity) {
		super(id, name, sourceId, destinyId, type);
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

		Evento evento = new EventoNuevoLink(this.name, source, target, "transport");
		IMFElement link = evento.ejecutar();

		if (this.type.equals(ElementType.DATAFLOW_PRELOAD)) {
			this.removeAutomaticCoupling(link);
			this.putPreload(target, this.role, link);

		} else if (this.type.equals(ElementType.DATAFLOW_ENTRY_TO_CONNECT) || this.type.equals(ElementType.DATAFLOW_ENTRY_TO_RECONNECT)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingEntryToConnect(source, target, this.getTargetEntity(this.role), this.role, link);

		} else if (this.type.equals(ElementType.DATAFLOW_UNIT_TO_ENTRY)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingUnitToEntry(source, target, this.entity, link);

		} else if (this.type.equals(ElementType.DATAFLOW_UNIT_TO_ENTRY_ROLE)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingUnitToEntry(source, target, this.entity, link, this.role);

		} else if (this.type.equals(ElementType.FLOW_ENTRY_TO_CREATE)) {
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
		IEntity entidadOrigen = this.getTargetEntity(role);
		IAttribute atributoSeleccion = this.relshipsSelected.get(role);
		ISubUnit field;
		String nombreCampo;
		List<ISubUnit> listaFields = ((IUnit) destino).getSubUnitList();
		String nombreRole = Utilities.getAttribute(role, "name");
		IEntity entidadPreload = this.getTargetEntity(role);
		List<IAttribute> listaAt = entidadPreload.getAllAttributeList();
		IAttribute atributo = null;
		for (int i = 0; i < listaAt.size(); i++) {
			if (Utilities.getAttribute(listaAt.get(i), "key").equals("true")) {
				atributo = listaAt.get(i);
				break;
			}
		}

		for (Iterator<ISubUnit> iter = listaFields.iterator(); iter.hasNext();) {
			field = iter.next();
			nombreCampo = Utilities.getAttribute(field, "name");
			if (nombreCampo.equals(nombreRole)) {
				this.createParameterPreload(atributo, field, atributoSeleccion, link, entidadOrigen);
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
	 * @param atributo
	 *            : atributo que queremos conectar
	 * @param subUnit
	 *            : campo del formulario que queremos conectar
	 * @param link
	 *            : link que contendrá el parametro
	 * @return: el parametro creado
	 */
	private IMFElement createParameter(IAttribute atributo, ISubUnit subUnit, IMFElement link) {
		IMFElement linkParameter;
		IMFElement field = subUnit;
		String nombre = Utilities.getAttribute(field, "name");
		// Creamos un linkParameter con los datos necesarios
		linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		// Id: se forma de las id del link más las id de los elementos
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();
		// Nombre: se forma con el nombre del field
		new SetAttributeMFOperation(linkParameter, "name", nombre + "_" + nombre, link.getRootElement()).execute();
		// Origen: con los datos del atributo
		new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(atributo.getIdsByFinalId().toString()) + "Array",
				link.getRootElement()).execute();
		// Destino: se crea con los campos del field del formulario
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(field.getIdsByFinalId().toString()) + "_slot",
				link.getRootElement()).execute();

		return linkParameter;
	}

	/**
	 * Nombre: createParameterPreload Funcion: Crea un parametro en un link para poder hacer el preload de atributos
	 * 
	 * @param atributo
	 *            : atributo que queremos relacionar
	 * @param field
	 *            : campo que queremos precargar
	 * @param atributoSeleccion
	 *            : atributo necesario para obtener el nombre del source
	 * @param link
	 *            : link que contendrá el paramtro
	 * @param entidadOrigen
	 *            : entidad de la que proceden los atributos
	 */
	private void createParameterPreload(IAttribute atributo, ISubUnit field, IAttribute atributoSeleccion, IMFElement link,
			IEntity entidadOrigen) {
		IMFElement linkParameter;
		IEntity padre = entidadOrigen;

		String id = this.cleanIds(link.getIdsByFinalId().toString());
		String source_label = this.cleanIds(atributoSeleccion.getIdsByFinalId().toString()) + "Array";
		String source_output = this.cleanIds(atributo.getIdsByFinalId().toString()) + "Array";

		String name_label = Utilities.getAttribute(atributoSeleccion, "name") + "_" + Utilities.getAttribute(padre, "name") + " [label]";
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
	 * @param origen
	 *            : unidadOrigen (entryUnit)
	 * @param destino
	 *            : unidadDestino (connect o disconnect unit)
	 * @param entidadDestino
	 *            : Entidad de la conectUnit, no tiene por que ser la misma que la del CRUD
	 * @param role
	 *            : role que esta en la roleCondition de la unidad destino
	 * @param link
	 *            : link que enlaza la unidad origen y destino, para añadirle el linkParameter
	 */
	private void guessCouplingEntryToConnect(IMFElement origen, IMFElement destino, IEntity entidadDestino, IRelationshipRole role,
			IMFElement link) {
		// Obtenemos la keyCondition de la unidadDestino
		IOperationUnit connectUnit = (IOperationUnit) destino;
		IMFElement keyCondition = connectUnit.selectSingleElement("TargetSelector").selectSingleElement("KeyCondition");
		String name = Utilities.getAttribute(keyCondition, "name");
		String nombreBuscar = Utilities.getAttribute(entidadDestino, "name");

		IAttribute keyAtributo = null;
		IUnit entryUnit;
		entryUnit = (IUnit) origen;
		// Obtenemos la lista de campos del formularios y la lista de atributos
		// de la entidad destino
		List<ISubUnit> listaFields = entryUnit.getSubUnitList();
		List<IAttribute> listaAtributos = entidadDestino.getAllAttributeList();

		// Buscamos el atributo que funciona como key
		for (int i = 0; i < listaAtributos.size(); i++) {
			if (Utilities.getAttribute(listaAtributos.get(i), "key").equals("true")) {
				keyAtributo = listaAtributos.get(i);
				break;
			}
		}

		// Creamos un hashMap con el nombre del campo y el campo.
		Map<String, ISubUnit> mapaCampos = new HashMap<String, ISubUnit>();
		Iterator<ISubUnit> iteratorCampos = listaFields.iterator();
		ISubUnit field;
		while (iteratorCampos.hasNext()) {
			field = iteratorCampos.next();
			mapaCampos.put(Utilities.getAttribute(field, "name"), field);
		}

		// Obtenemos el campo que se relaciona con la role
		field = mapaCampos.get(Utilities.getAttribute(role, "name"));

		// Creamos el link parameter que añadimos al link.
		ILinkParameter linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(field.getIdsByFinalId().toString()), link.getRootElement())
				.execute();

		new SetAttributeMFOperation(linkParameter, "name", nombreBuscar + "_" + name + " [oid] [" + nombreBuscar + "] [Target]",
				link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(keyCondition.getIdsByFinalId().toString()) + "."
				+ this.cleanIds(keyAtributo.getIdsByFinalId().toString()), link.getRootElement()).execute();

		((MFElement) link).addChild(linkParameter, null);
	}

	/**
	 * Nombre: guessCouplingUnitToEntry Funcion: Simula un guessCoupling entre cualquier unidad y una entryUnit llamando a los metodos
	 * creados anteriormente
	 * 
	 * @param origen
	 *            : unidad (SelectorUnit, contentUnit...)
	 * @param destino
	 *            : entryUnit
	 * @param entidadOrigen
	 *            : entidad que esta seleccionada en la unidad de origen
	 * @param link
	 *            : link sobre el que se creara el linkParameter
	 */
	private void guessCouplingUnitToEntry(IMFElement origen, IMFElement destino, IEntity entidadOrigen, IMFElement link) {

		// Obtener lista FIELDS
		List<ISubUnit> listaFields = ((IUnit) destino).getSubUnitList();
		// Obtener lista Atributos de la entidad origen
		List<IAttribute> listaAtributos = entidadOrigen.getAllAttributeList();

		// Generar mapas para las listas de campos y atributos
		Map<String, IAttribute> mapaAtributos = new HashMap<String, IAttribute>();
		Map<String, ISubUnit> mapaCampos = new HashMap<String, ISubUnit>();

		// Iniciar los hashMap
		for (ISubUnit field : listaFields)
			mapaCampos.put(Utilities.getAttribute(field, "name"), field);

		for (IAttribute atributo : listaAtributos)
			mapaAtributos.put(Utilities.getAttribute(atributo, "name"), atributo);

		ISubUnit field;
		IAttribute atributo;
		String tipoCampo;
		IMFElement linkParameter;
		IRelationshipRole relationRole;
		// Recorremos todos los campos
		for (String nombreCampo : mapaCampos.keySet()) {
			atributo = mapaAtributos.get(nombreCampo);
			// Si nos retorna un atributo es un coupling por atributo
			if (atributo != null) {
				linkParameter = this.createParameter(atributo, mapaCampos.get(nombreCampo), link);
				((MFElement) link).addChild(linkParameter, null);
			} else {
				// En caso contrario es un coupling con selection o multiselection
				relationRole = this.buscarRelation(nombreCampo, this.entity);

				field = mapaCampos.get(nombreCampo);
				tipoCampo = field.getQName().getName();
				if (tipoCampo.equals("SelectionField")) {
					linkParameter = this.createParameterRoleToField(relationRole, field, link, false);
					((MFElement) link).addChild(linkParameter, null);
				}
			}
		}
	}

	/**
	 * Nombre: guessCouplingUnitToEntry Funcion: Simula un guess Coupling entre cualquier unidad y la entryUnit
	 * 
	 * @param origen
	 *            : unidad origen
	 * @param destino
	 *            : unidad destino, en este caso entryUnit
	 * @param entidadOrigen
	 *            : entidad que esta seleccionada en la unidad origen
	 * @param link
	 *            : link sobre el que se creara el linkPArameter
	 * @param role
	 *            : role que de la unidad origen, en caso de tenerla
	 */
	private void guessCouplingUnitToEntry(IMFElement origen, IMFElement destino, IEntity entidadOrigen, IMFElement link,
			IRelationshipRole role) {
		ISubUnit field;
		ISubUnit preselect = null;
		String nombreCampo;
		IMFElement linkParameter;
		String nombreRole = Utilities.getAttribute(role, "name");
		// Obtener lista FIELDS
		List<ISubUnit> listaFields = ((IUnit) destino).getSubUnitList();

		// Iniciar los hashMap
		for (Iterator<ISubUnit> iter = listaFields.iterator(); iter.hasNext();) {
			field = iter.next();
			nombreCampo = Utilities.getAttribute(field, "name");
			if (nombreCampo.contains(nombreRole)) {
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
		IAttribute atributo = this.relshipsSelected.get(role);
		IEntity entidadParent = (IEntity) atributo.getParentElement();

		List<IAttribute> listaAt = entidadParent.getAllAttributeList();

		for (int i = 0; i < listaAt.size(); i++) {
			if (Utilities.getAttribute(listaAt.get(i), "key").equals("true")) {
				atributo = listaAt.get(i);
				break;
			}
		}

		linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());

		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();

		if (multi) {
			new SetAttributeMFOperation(linkParameter, "name", Utilities.getAttribute(atributo, "name") + "_" + nameRole
					+ " - Preselection", link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(atributo.getIdsByFinalId().toString()) + "Array",
					link.getRootElement()).execute();
		} else {

			new SetAttributeMFOperation(linkParameter, "name", nameRole + ".oid_" + nameRole + " - Preselection", link.getRootElement())
					.execute();

			new SetAttributeMFOperation(linkParameter, "source", idRole + "_" + this.cleanIds(atributo.getIdsByFinalId().toString())
					+ "Array", link.getRootElement()).execute();
		}

		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(field.getIdsByFinalId().toString()) + "_presel",
				link.getRootElement()).execute();

		return linkParameter;
	}

}
