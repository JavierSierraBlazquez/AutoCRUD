package org.homeria.webratioassistant.elements;

import java.util.Iterator;
import java.util.List;

import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.commons.internal.mf.MFElement;
import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.operations.SetAttributeMFOperation;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.ILinkParameter;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;

public abstract class Link extends WebRatioElement {

	protected String sourceId;
	protected String targetId;

	public Link(String id, String name, String sourceId, String destinyId) {
		super(id, name);
		this.sourceId = sourceId;
		this.targetId = destinyId;
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
	 * Nombre: putMessageOnMultiMessageUnit Funcion: añade un mensaje a un link OK o linkKO que van dirigidos a una multiMessageUnit
	 * 
	 * @param link
	 *            : link al que añadir el mensaje
	 * @param destino
	 *            : multiMessageUnit que mostrará el mensaje
	 * @param mensaje
	 *            : mensaje a mostrar
	 */
	protected void putMessageOnMultiMessageUnit(IMFElement link, IMFElement destino, String mensaje) {
		ILinkParameter linkParameter = Utilities.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());

		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "name", mensaje + "_" + "Shown Messages", link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "sourceValue", mensaje, link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(destino.getIdsByFinalId().toString()) + ".shownMessages",
				link.getRootElement()).execute();
		((MFElement) link).addChild(linkParameter, null);
	}

	/**
	 * Nombre: cleanIds Funcion: Funcion auxiliar, limpia una cadena eliminando el primer y ultimo caracter
	 * 
	 * @param cadena
	 *            : string que se quiere tratar
	 * @return: cadena resultante
	 */
	protected String cleanIds(String cadena) {
		return cadena.substring(1, cadena.length() - 1);
	}

	/**
	 * Nombre: buscarRelation Funcion: Busca una relacion por su nombre
	 * 
	 * @param nombreCampo
	 *            : nombre por el que buscar
	 * @return la relacion si se encuentra, null en caso contrario
	 */
	protected IRelationshipRole buscarRelation(String nombreCampo, IEntity entity) {

		List<IRelationship> listaRelation = entity.getIncomingRelationshipList();
		listaRelation.addAll(entity.getOutgoingRelationshipList());
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
