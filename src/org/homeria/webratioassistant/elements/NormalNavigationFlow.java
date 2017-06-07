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
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.IUnit;

public class NormalNavigationFlow extends Link {
	private IEntity entity;
	private Map<IRelationshipRole, IAttribute> relshipsSelected;
	private boolean validate;

	public NormalNavigationFlow(String id, String name, String sourceId, String destinyId, String type, String validate, IEntity entity) {
		super(id, name, sourceId, destinyId,type);
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

		Evento evento = new EventoNuevoLink(this.name, source, target, "normal");
		IMFElement link = evento.ejecutar();

		if (this.type.equals(ElementType.FLOW_ENTRY_TO_CREATE) || this.type.equals(ElementType.FLOW_ENTRY_TO_UPDATE)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingEntryToCreateModify(source, target, link, this.entity, this.relshipsSelected);

		} else if (this.type.equals(ElementType.NORMALFLOW_FIXED_VALUE)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingFixedValue(this.entity, target, link, String.valueOf(0));
		} else if (this.type.equals(ElementType.NORMALFLOW_IS_NOT_NULL)) {
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
	private void guessCouplingFixedValue(IEntity entidadOrigen, IMFElement destino, IMFElement link, String valor) {

		// Uso para keyCondition

		// <Link id="ln3" name="New Town" to="seu1" type="normal" validate="true">
		// <LinkParameter id="par21" name="0_KeyCondition3 [oid]" sourceValue="0" target="kcond3.att2"/>
		// </Link>

		// Variables
		// ISubUnit field;
		IAttribute atributo;
		String nombreCampo;
		// String tipoCampo;
		IMFElement linkParameter;
		String keyAtributo;

		try {
			// Obtener lista Atributos de la entidad origen
			List<IAttribute> listaAtributos = entidadOrigen.getAllAttributeList();

			// Generar mapas para las listas de campos y atributos
			Map<String, IAttribute> mapaAtributos = new HashMap<String, IAttribute>();

			// Iniciar los hashMa
			for (Iterator<IAttribute> iter = listaAtributos.iterator(); iter.hasNext();) {
				atributo = iter.next();

				keyAtributo = Utilities.getAttribute(atributo, "key");
				if (keyAtributo.equals("true")) {
					mapaAtributos.put(Utilities.getAttribute(atributo, "name"), atributo);
				}
			}

			if (null != mapaAtributos && mapaAtributos.size() > 0) {
				// Recorremos todos los campos
				for (Iterator<String> iter = mapaAtributos.keySet().iterator(); iter.hasNext();) {
					nombreCampo = iter.next();
					atributo = mapaAtributos.get(nombreCampo);
					// Si nos retorna un atributo es un coupling por atributo
					if (atributo != null) {
						keyAtributo = Utilities.getAttribute(atributo, "key");
						if (keyAtributo.equals("true")) {
							linkParameter = this.createParameterField2AttValor(atributo, destino, valor, link, true);
							((MFElement) link).addChild(linkParameter, null);
						}
					}
				}
			}
		} catch (Exception e) {
			// captura excepcion ya que esto solo es para a�adir un linkParameter a un parameter
		}
	}

	/**
	 * 
	 * Nombre: createParameterField2AttCarlos Funcion:
	 * 
	 * @param atributo
	 * @param unidadDestino
	 * @param valor
	 * @param link
	 * @param key
	 * @return
	 */
	private IMFElement createParameterField2AttValor(IAttribute atributo, IMFElement unidadDestino, String valor, IMFElement link,
			boolean key) {

		IMFElement linkParameter;
		String idAtributo = Utilities.getAttribute(atributo, "id");

		linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();
		// Si no es de tipo key (para relacionar el oid) se hace igual que la
		// funcion anterior.
		if (key) {
			// Si se relaciona con un oid se necesita la keyCondition del
			// elemento
			// para crear el parametro del link, además de cambiar el formato
			// del nombre
			IMFElement keyCondition = unidadDestino.selectSingleElement("Selector").selectSingleElement("KeyCondition");
			String nameKey = Utilities.getAttribute(keyCondition, "name");

			new SetAttributeMFOperation(linkParameter, "name", valor + "_" + nameKey + " [oid]", link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "sourceValue", valor, link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(keyCondition.getIdsByFinalId().toString()) + "."
					+ idAtributo, link.getRootElement()).execute();

		}
		return linkParameter;
	}

	/**
	 * Nombre: putCoupling Funcion: Pone un coupling entre dos unidades, especialmente se usa para las unidades isNotNullUnit
	 * 
	 * @param oidField
	 *            : campo del formulario/entidad que se usa para el copling
	 * @param destino
	 *            : unidad de destino (isNotNullUnit)
	 * @param tipo
	 *            : tipo de coupling (por ejemplo isnotnull)
	 * @param link
	 *            : link en el que se crea el coupling
	 */
	private void putCoupling(IMFElement oidField, IUnit destino, String tipo, IMFElement link) {
		// Se elimina el automaticCoupling del link, para hacerlo manual
		Utilities.setAttribute(link, "automaticCoupling", null);
		String nombre = Utilities.getAttribute(oidField, "name").toLowerCase();

		ILinkParameter linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "name", nombre + "_" + nombre, link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(oidField.getIdsByFinalId().toString()), link.getRootElement())
				.execute();
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(destino.getIdsByFinalId().toString()) + "." + tipo,
				link.getRootElement()).execute();

		((MFElement) link).addChild(linkParameter, null);
	}
}
