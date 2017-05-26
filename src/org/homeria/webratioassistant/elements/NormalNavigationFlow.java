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
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISubUnit;
import com.webratio.ide.model.IUnit;

public class NormalNavigationFlow extends Link {
	private IEntity entity;
	private String type;
	private Map<IRelationshipRole, IAttribute> relshipsSelected;

	public NormalNavigationFlow(String id, String name, String sourceId, String destinyId, String type, IEntity entity) {
		super(id, name, sourceId, destinyId);
		this.entity = entity;
		this.type = type;
	}

	public void setRelshipsSelected(Map<IRelationshipRole, IAttribute> relshipsSelected) {
		this.relshipsSelected = relshipsSelected;
	}

	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement source = createdElements.get(this.sourceId);
		IMFElement target = createdElements.get(this.targetId);

		Evento evento = new EventoNuevoLink(this.name, source, target, "normal");
		IMFElement link = evento.ejecutar();

		if (this.type.equals(ElementType.NORMALFLOW_ENTRY_TO_CREATE) || this.type.equals(ElementType.NORMALFLOW_ENTRY_TO_CREATE)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingEntryToCreateModify(source, target, link);
		}
		return link;
	}

	/**
	 * Nombre: guessCouplingEntryToCreateModify Funcion: Simula el guessCoupling entre la entryUnit y la Create o Modify unit
	 * 
	 * @param origen
	 *            : entry unit
	 * @param destino
	 *            : unidad create o modify
	 * @param link
	 *            : link sobre el que añadir el linkParameter
	 */
	private void guessCouplingEntryToCreateModify(IMFElement origen, IMFElement destino, IMFElement link) {
		// Variables
		String tipoCampo;
		IMFElement linkParameter;
		IRelationshipRole role;
		String keyAtributo;
		IEntity entidadGuess = this.entity;
		// Obtener lista FIELDS
		List<ISubUnit> listaFields = ((IUnit) origen).getSubUnitList();
		// Obtener lista Atributos de la entidad origen
		List<IAttribute> listaAtributos = (entidadGuess).getAllAttributeList();

		String tipoDestino = destino.getQName().getName();

		// Generar mapas para las listas de campos y atributos
		Map<String, IAttribute> mapaAtributos = new HashMap<String, IAttribute>();
		Map<String, ISubUnit> mapaCampos = new HashMap<String, ISubUnit>();

		// Iniciar los hashMap
		for (ISubUnit field : listaFields)
			mapaCampos.put(Utilities.getAttribute(field, "name"), field);

		for (IAttribute atributo : listaAtributos)
			mapaAtributos.put(Utilities.getAttribute(atributo, "name"), atributo);

		// Recorremos todos los campos
		IAttribute atributo;
		ISubUnit field;

		for (String nombreCampo : mapaCampos.keySet()) {
			atributo = mapaAtributos.get(nombreCampo);
			// Si nos retorna un atributo es un coupling por atributo
			if (atributo != null) {
				keyAtributo = Utilities.getAttribute(atributo, "key");

				if (!keyAtributo.equals("true")) {
					linkParameter = this.createParameterField2Att(atributo, destino, mapaCampos.get(nombreCampo), link, false);
					((MFElement) link).addChild(linkParameter, null);
				} else {
					if (tipoDestino.equals("ModifyUnit")) {

						linkParameter = this.createParameterField2Att(atributo, destino, mapaCampos.get(nombreCampo), link, true);
						((MFElement) link).addChild(linkParameter, null);
					}
				}
			} else {
				// En caso contrario es un coupling con selection o
				// multiselection
				role = this.buscarRelation(nombreCampo);

				field = mapaCampos.get(nombreCampo);
				tipoCampo = field.getQName().getName();
				if (tipoCampo.equals("SelectionField")) {
					linkParameter = this.createParameterFieldToRole(role, destino, field, link);
					((MFElement) link).addChild(linkParameter, null);
				}
			}
		}
	}

	/**
	 * Nombre: createParameterField2Att Funcion: Su funcionamiento es igual que la funcion createParameter pero de manera inversa, se crea
	 * la relacion entre el campo del formulario y el atributo de una unidad (create, modify...)
	 * 
	 * @param atributo
	 *            : atributo que queremos relacionar
	 * @param unidadDestino
	 *            : unidad sobre la que trabajar
	 * @param subUnit
	 *            : field
	 * @param link
	 *            : link que contendrá el parametro
	 * @param key
	 *            : para indicar si es un oid
	 * @return: el parametro creado
	 */
	private IMFElement createParameterField2Att(IAttribute atributo, IMFElement unidadDestino, ISubUnit subUnit, IMFElement link,
			boolean key) {
		IMFElement linkParameter;
		IMFElement field = subUnit;

		String nombre = Utilities.getAttribute(field, "name");

		String idAtributo = Utilities.getAttribute(atributo, "id");

		linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();
		// Si no es de tipo key (para relacionar el oid) se hace igual que la
		// funcion anterior.
		if (!key) {
			new SetAttributeMFOperation(linkParameter, "name", nombre + "_" + nombre, link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(field.getIdsByFinalId().toString()), link.getRootElement())
					.execute();

			new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(unidadDestino.getIdsByFinalId().toString()) + "."
					+ idAtributo, link.getRootElement()).execute();
		} else {
			// Si se relaciona con un oid se necesita la keyCondition del
			// elemento
			// para crear el parametro del link, además de cambiar el formato
			// del nombre
			IMFElement keyCondition = unidadDestino.selectSingleElement("Selector").selectSingleElement("KeyCondition");
			String nameKey = Utilities.getAttribute(keyCondition, "name");

			new SetAttributeMFOperation(linkParameter, "name", nombre + "_" + nameKey + " [oid]", link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(field.getIdsByFinalId().toString()), link.getRootElement())
					.execute();

			new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(keyCondition.getIdsByFinalId().toString()) + "."
					+ idAtributo, link.getRootElement()).execute();

		}
		return linkParameter;
	}

	/**
	 * Nombre: createParameterFieldToRole Funcion: Crea un parametro desde un campo de un formulario con una roleCondition. El procedimiento
	 * es igual a las anteriores, solo variando el nombre y algunos datos mas
	 * 
	 * @param role
	 *            : role que queremos conectar
	 * @param unidadDestino
	 *            : unidad en la que estará la roleCondition (createUnit, modifyUnit...)
	 * @param field
	 *            : campo del formulario
	 * @param link
	 *            : link que contendrá el parametro
	 * @return: el parametro creado
	 */
	private IMFElement createParameterFieldToRole(IRelationshipRole role, IMFElement unidadDestino, ISubUnit field, IMFElement link) {
		IMFElement linkParameter;
		String nameRole = Utilities.getAttribute(role, "name");
		String idRole = Utilities.getAttribute(role, "id");
		IAttribute atributo = this.relshipsSelected.get(role);
		IEntity entidadParent = (IEntity) atributo.getParentElement();
		String nombreEntidad = Utilities.getAttribute(entidadParent, "name");

		List<IAttribute> listaAt = entidadParent.getAllAttributeList();

		// Recorremos los atributos buscando el keyCondition
		for (int i = 0; i < listaAt.size(); i++) {
			if (Utilities.getAttribute(listaAt.get(i), "key").equals("true")) {
				atributo = listaAt.get(i);
				break;
			}
		}
		// Al igual que en los metodos anteriores se crean los campos necesarios
		// (id,name,source,target)
		String idAtributo = Utilities.getAttribute(atributo, "id");
		String nombreAtributo = Utilities.getAttribute(atributo, "name");
		linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());

		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "name", nameRole + "_" + nombreEntidad + "." + nombreAtributo + "(" + nameRole + ")",
				link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(field.getIdsByFinalId().toString()), link.getRootElement())
				.execute();
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(unidadDestino.getIdsByFinalId().toString()) + "." + idRole + "."
				+ idAtributo, link.getRootElement()).execute();

		return linkParameter;
	}

	/**
	 * Nombre: buscarRelation Funcion: Busca una relacion por su nombre
	 * 
	 * @param nombreCampo
	 *            : nombre por el que buscar
	 * @return la relacion si se encuentra, null en caso contrario
	 */
	private IRelationshipRole buscarRelation(String nombreCampo) {

		List<IRelationship> listaRelation = this.entity.getIncomingRelationshipList();
		listaRelation.addAll(this.entity.getOutgoingRelationshipList());
		IRelationship relation;
		IRelationshipRole role;
		for (Iterator<IRelationship> iter = listaRelation.iterator(); iter.hasNext();) {
			relation = iter.next();
			role = relation.getRelationshipRole1();
			if (Utilities.getAttribute(role, "name").equals(nombreCampo)) {
				return role;
			}
			role = relation.getRelationshipRole2();
			if (Utilities.getAttribute(role, "name").equals(nombreCampo)) {
				return role;
			}
		}
		return null;
	}

}
