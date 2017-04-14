/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.crud;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IAlternative;
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IContentUnit;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IOperationUnit;
import com.webratio.ide.model.IPage;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISiteView;

/**
 * @author Carlos Aguado Fuentes
 * @version 1.0
 * @class Create
 */
public class AllInOne extends CRUD {
	private List<IAttribute> listaAtributosDetail;

	/**
	 * Create.java: Clase que se encarga de generar los elementos encargados de
	 * realizar la operaci�n de Crear/Create. Es necesario especificar los
	 * siteviews donde se deber� colocar, adem�s de los atributos y relaciones
	 * que se deberan mostrar en la unidad EntryUnit.
	 */

	// se añade la lista de Detail y generationDelay
	public AllInOne(IMFElement entity, List<ISiteView> siteViews, List<IArea> areas, Map<IRelationshipRole, IAttribute> relation,
			List<IAttribute> atributosIndex, List<IAttribute> atributosData, List<IAttribute> atributosDetail, int generationDelay) {
		super(siteViews, areas, entity, atributosIndex, atributosData, relation, generationDelay);
		this.listaAtributosDetail = atributosDetail;
	}

	/**
	 * 
	 * Nombre: ejecutar Funcion:
	 * 
	 * @param subProgressMonitor
	 */
	public void ejecutar(SubProgressMonitor subProgressMonitor) {
		int unidad = 1;
		// Tama�o total de ejecuciones para la barra de progreso
		int totalWork = this.getListaSiteViews().size() * (30);
		// Creamos tarea "Create" para que aparezca en la barra de progreso
		subProgressMonitor.beginTask("Create", totalWork);
		try {
			// Variables locales
			ISiteView siteView;

			// Se ejecuta para todos los siteviews
			for (Iterator<ISiteView> iteradorSiteView = this.getListaSiteViews().iterator(); iteradorSiteView.hasNext();) {
				siteView = iteradorSiteView.next();

				// Activamos el siteView seleccionado en el editor de webratio
				// Utilities.switchSiteView(siteView);

				List<IArea> listaAreaEnc = null;
				// metodo que diga si en la lista de areas de entrada hay alguno
				// que corresponde al siteView del iterator
				if (null != siteView.getAreaList() && siteView.getAreaList().size() > 0 && null != this.getListaAreas()
						&& this.getListaAreas().size() > 0) {

					for (Iterator iterator = this.getListaAreas().iterator(); iterator.hasNext();) {
						IArea iAreaSelected = (IArea) iterator.next();

						String[] partesNombreID = iAreaSelected.getRootXPath().split("'"); // id('stv')
						String idPadre = partesNombreID[1];
						if (idPadre.compareTo(siteView.getFinalId()) == 0) {
							if (null == listaAreaEnc) {
								listaAreaEnc = new ArrayList<IArea>();
							}
							listaAreaEnc.add(iAreaSelected);
						}
					}
				}

				// crear en siteView
				if (null == listaAreaEnc) {
					Utilities.switchSiteView(siteView);
					allInOneCrearElementos(subProgressMonitor, unidad, siteView);
					subProgressMonitor.worked(unidad);
					Thread.sleep(this.generationDelay);
				} else {
					for (Iterator iterator = listaAreaEnc.iterator(); iterator.hasNext();) {
						IArea iArea = (IArea) iterator.next();
						Utilities.switchSiteView(siteView);
						allInOneCrearElementos(subProgressMonitor, unidad, iArea);
						subProgressMonitor.worked(unidad);
						Thread.sleep(this.generationDelay);

					}
				}

				subProgressMonitor.worked(unidad);
				Thread.sleep(this.generationDelay);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			subProgressMonitor.done();
		}
	}

	/**
	 * 
	 * Nombre: allInOneCrearElementos Funcion:
	 * 
	 * @param subProgressMonitor
	 * @param unidad
	 * @param elementIMFE
	 * @throws InterruptedException
	 */
	private void allInOneCrearElementos(SubProgressMonitor subProgressMonitor, int unidad, IMFElement elementIMFE)
			throws InterruptedException {
		IPage pagina;
		IPage paginaDefault;
		IPage paginaForm;
		IPage paginaDetails;
		IAlternative alternative;
		IOperationUnit createUnit;
		IOperationUnit modifyUnit;
		IOperationUnit isNotNullUnit;
		IOperationUnit deleteUnit;
		IContentUnit entryUnit;
		IContentUnit selectorUnit;
		IContentUnit selectorEntidad;
		IContentUnit multiMessageUnit;
		IContentUnit noOpContentUnit;
		IContentUnit dataUnit;
		IContentUnit powerIndexUnit;
		IEntity entidadPreload;
		IMFElement link;
		IEntity entidad;
		IOperationUnit connectUnit;
		IOperationUnit disconnectUnit;
		String nombreRole;
		String idRole;
		IMFElement anteriorCreate;
		IMFElement anteriorModify;
		IMFElement firstCreate;
		Point posicion;
		int x;
		int y;
		int posx;
		int posy;

		// Buscamos la posici�n x,y libre
		posicion = Utilities.buscarHueco();
		x = posicion.x;
		y = posicion.y;
		firstCreate = null;

		// Crear deleteUnit
		deleteUnit = (IOperationUnit) this.addUnidad(elementIMFE, "DeleteUnit", x, y, "Delete", true, null);
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);
		// Buscamos otra vez primer hueco libre
		posicion = Utilities.buscarHueco();
		x = posicion.x;
		y = posicion.y;

		// Crear Pagina del CRUD AllInOne
		pagina = (IPage) this.addPagina(elementIMFE, "CRUD", x, y);
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// A�adir pagina alternativa
		posx = posy = 5;
		alternative = (IAlternative) this.addArea(pagina, posx, posy, "Alternative");
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// New
		// Obtenemos la pagina default
		paginaDefault = alternative.getPageList().get(0);
		Utilities.setAttribute(paginaDefault, "name", "New " + Utilities.getAttribute(this.getEntity(), "name"));

		// No Op Content Unit
		// A�adimos una noOpContentUnit para crear un enlace
		noOpContentUnit = (IContentUnit) this.addUnidad(paginaDefault, "NoOpContentUnit", 5, 5, "New ", false, null);
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// A�adir multiMessage para los mensajes de las unidades
		multiMessageUnit = (IContentUnit) this.addUnidad(paginaDefault, "MultiMessageUnit", (posx + Utilities.anchoUnidad), 5, "Message",
				false, null);
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// A�adimos la powerIndex e indicamos los atributos visibles
		posy = posy + Utilities.altoUnidad;
		powerIndexUnit = (IContentUnit) this.addUnidad(paginaDefault, "PowerIndexUnit", posx, posy, "Index", true, null);

		// A�adimos los atributos que queremos visibles en la powerIndexUnit
		this.addAtributosIndex(powerIndexUnit);
		// TODO CAGUADOF: a�adir que sea sorteable ascending por el oid
		// SortAttribute
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// A�adimos la pagina donde ir� el formulario
		paginaDetails = (IPage) this.addPagina(alternative, "Details", 300, 5);
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// A�adimos la dataUnit e indicamos todos los atributos visibles
		dataUnit = (IContentUnit) this.addUnidad(paginaDetails, "DataUnit", 5, 5, "Data", true, null);
		// A�adimos todos los atributos para mostrarlos en la dataUnit
		this.addAtributosData(dataUnit, this.listaAtributosDetail);

		// A�adimos un link normal entre la dataUnit
		// y la pagina para limpiar el contenido
		this.addNormalLink((IMFElement) dataUnit, multiMessageUnit, "Close");

		// Enlaces
		link = this.addOKLink(deleteUnit, multiMessageUnit);
		this.setAutomaticCoupling(link);
		this.putMessageOnMultiMessageUnit(link, multiMessageUnit, "The data from " + this.getNombreEntity() + " has been delete.");
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		link = this.addKOLink(deleteUnit, multiMessageUnit);
		this.setAutomaticCoupling(link);

		this.putMessageOnMultiMessageUnit(link, multiMessageUnit, "Error deleting data from " + this.getNombreEntity());
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// PAGINA FORMULARIO
		// A�adimos la pagina donde ir� el formulario
		paginaForm = (IPage) this.addPagina(alternative, "Form", 5, 300);

		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// A�adir el formulario y poner los campos
		entryUnit = (IContentUnit) this.addUnidad(paginaForm, "EntryUnit", 200, 200, "Form", true, null);
		this.setFields(entryUnit, true, true);
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// Enlace Cancel desde entrey a MultiMesage: sin validaci�n
		link = this.addNormalLink(entryUnit, multiMessageUnit, "Cancel");
		Utilities.setAttribute(link, "validate", "false");

		// A�adir el selector de entidad y asignarle una keyCondition
		posx = posy = 5;
		selectorEntidad = (IContentUnit) this.addUnidad(paginaForm, "SelectorUnit", posx, posy, "Selector", true, null);
		IMFElement condicion = this.addKeyCondition(selectorEntidad);
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// A�adir enlace al formulario para nuevo
		// usuario desde la noOpContentUnit
		link = this.addNormalLink((IMFElement) noOpContentUnit, (IMFElement) selectorEntidad,
				"New " + Utilities.getAttribute(this.getEntity(), "name"));

		// Hay que conseguir hacer el mapeo a oid 0 de la otra
		this.setAutomaticCoupling(link);
		// 0 a KeyCondition
		// <LinkParameter id="par21" name="0_KeyCondition3 [oid]"
		// sourceValue="0" target="kcond3.att2"/> -->oid
		guessCouplingFieldAttConValor(this.getEntity(), (IMFElement) selectorEntidad, link, String.valueOf(0));

		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// A�adimos un link normal entre la powerIndex
		// y la dataUnit para mostrar el contenido
		this.addNormalLink((IMFElement) powerIndexUnit, (IMFElement) dataUnit, "View");
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// A�adir el enlace de Modificar entre la
		// powerIndexUnit y selectorEntidad
		this.addNormalLink((IMFElement) powerIndexUnit, (IMFElement) selectorEntidad, "Modify");
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// A�adimos un link normal entre la powerIndex
		// y la deleteUnit para borrar la tupla
		this.addNormalLink((IMFElement) powerIndexUnit, (IMFElement) deleteUnit, "Delete");
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// A�adir link de transporte entre la selectorUnit
		// y el formulario. Hacer un guessCoupling
		link = this.addTransportLink(selectorEntidad, entryUnit, "Load");
		this.setAutomaticCoupling(link);
		this.guessCouplingUnitToEntry(selectorEntidad, this.getEntity(), entryUnit, link);// ,
																							// preload);
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// A�adir Selector para precarga formulario (Update)
		// Solo son validas las relaciones NaN, las dem�s las
		// carga la selectorEntidad directamente.
		posx = 200;
		posy = 5;
		IMFElement roleCondition;
		IRelationship relation;
		IRelationshipRole role;
		for (Iterator<IRelationshipRole> iteradorRole = this.getRelaciones().keySet().iterator(); iteradorRole.hasNext();) {
			role = iteradorRole.next();
			entidadPreload = this.getTargetEntity(role);
			relation = this.isNtoN(role);
			if (relation != null) {
				idRole = Utilities.getAttribute(role, "id");
				selectorUnit = (IContentUnit) this.addUnidad(paginaForm, "SelectorUnit", posx, posy, "Entity", false, entidadPreload);
				roleCondition = this.addRelationShipRoleCondition(selectorUnit);

				this.addRoleCondition((IMFElement) roleCondition, idRole);
				// A�adir link y hacer guessCoupling
				link = this.addTransportLink(selectorEntidad, selectorUnit, "Load");
				link = this.addTransportLink(selectorUnit, entryUnit, "Load");
				this.setAutomaticCoupling(link);
				this.guessCouplingUnitToEntry(selectorUnit, entidadPreload, entryUnit, link, role);
				posx = posx + Utilities.anchoUnidad;
			}
		}
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// A�adir las selectorUnit que se encarga de rellenar
		// los campos multiSelectionField y selectionField
		posx = 5;
		posy = Utilities.altoUnidad;

		// Para todas las relaciones se crea una selector unit
		// y se enlace con un link de transporte a la entry Unit
		// para mostrar los datos de las relaciones NaN
		for (Iterator<IRelationshipRole> iteradorRole = this.getRelaciones().keySet().iterator(); iteradorRole.hasNext();) {
			role = iteradorRole.next();
			entidadPreload = this.getTargetEntity(role);

			selectorUnit = (IContentUnit) this.addUnidad(paginaForm, "SelectorUnit", posx, posy, "Entity", false, entidadPreload);
			posy = posy + Utilities.altoUnidad;
			link = this.addTransportLink(selectorUnit, entryUnit, "Load");

			this.setAutomaticCoupling(link);
			this.putPreload(entryUnit, role, link);

		}
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		posicion = Utilities.buscarHueco();
		x = posicion.x;
		y = posicion.y + 500;

		// Links de la isNotNullUnit
		isNotNullUnit = (IOperationUnit) this.addUnidad(elementIMFE, "IsNotNullUnit", x, y, "New", false, null);
		link = this.addNormalLink(entryUnit, isNotNullUnit, "Accept");
		this.putCoupling(this.getOidField(), isNotNullUnit, "isnotnull", link);
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// createUnit y hacer coupling con los datos de la entryUnit
		// pero con un link de transporte
		createUnit = (IOperationUnit) this.addUnidad(elementIMFE, "CreateUnit", x, y - Utilities.altoUnidad, "Create", true, null);
		link = this.addTransportLink(entryUnit, createUnit, "Load");
		this.setAutomaticCoupling(link);
		this.guessCouplingEntryToCreateModify(entryUnit, createUnit, link);
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// modifyUnit igual que la create
		modifyUnit = (IOperationUnit) this.addUnidad(elementIMFE, "ModifyUnit", x, y + Utilities.altoUnidad, "Modify", true, null);
		link = this.addTransportLink(entryUnit, modifyUnit, "Load");

		this.setAutomaticCoupling(link);
		this.guessCouplingEntryToCreateModify(entryUnit, modifyUnit, link);
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		anteriorCreate = createUnit;
		anteriorModify = modifyUnit;

		// KO Links de create y modify
		link = this.addKOLink(anteriorCreate, multiMessageUnit);
		this.setAutomaticCoupling(link);

		this.putMessageOnMultiMessageUnit(link, multiMessageUnit, "Error creating data in" + this.getNombreEntity());
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		link = this.addKOLink(anteriorModify, multiMessageUnit);
		this.setAutomaticCoupling(link);

		this.putMessageOnMultiMessageUnit(link, multiMessageUnit, "Error modify data in " + this.getNombreEntity());
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);
		// Para todas las relaciones creamos las connectUnit y
		// disconnectUnit que se usaran para crear/modificar
		// los datos
		for (Iterator<IRelationshipRole> iteradorRole = this.getRelaciones().keySet().iterator(); iteradorRole.hasNext();) {
			// Obtenemos la role
			role = iteradorRole.next();
			// y la entidad destino
			entidad = this.getTargetEntity(role);
			// comprobamos que la relacion sea NaN
			relation = this.isNtoN(role);
			if (relation != null) {
				// Si es una relacion NaN necesitamos las connectUnit
				// y disconnectUnit, as� que buscamos hueco para colocar
				// las unidades
				posicion = Utilities.buscarHueco();
				x = posicion.x;

				nombreRole = Utilities.getAttribute(role, "name");
				idRole = Utilities.getAttribute(role, "id");
				connectUnit = (IOperationUnit) this.addUnidad(elementIMFE, "ConnectUnit", x, y - Utilities.altoUnidad, nombreRole, false,
						null);
				Utilities.setAttribute(connectUnit, "relationship", idRole);
				this.addOKLink(anteriorCreate, connectUnit);
				Thread.sleep(this.generationDelay);
				
				link = this.addTransportLink(entryUnit, connectUnit, "Load");
				this.setAutomaticCoupling(link);
				this.guessCouplingEntryToConnect(entryUnit, connectUnit, entidad, role, link);
				anteriorCreate = connectUnit;
				Thread.sleep(this.generationDelay);

				link = this.addKOLink(anteriorCreate, multiMessageUnit);
				this.setAutomaticCoupling(link);
				Thread.sleep(this.generationDelay);

				this.putMessageOnMultiMessageUnit(link, multiMessageUnit, "Error creating/modifying data in " + this.getNombreEntity());
				if (firstCreate == null)
					firstCreate = connectUnit;

				this.addTransportLink(modifyUnit, anteriorCreate, "Load");
				Thread.sleep(this.generationDelay);

				disconnectUnit = (IOperationUnit) this.addUnidad(elementIMFE, "DisconnectUnit", x, y + Utilities.altoUnidad, nombreRole,
						false, null);
				Utilities.setAttribute(disconnectUnit, "relationship", idRole);
				this.convertKeyConditionToRoleCondition(disconnectUnit, idRole);
				Thread.sleep(this.generationDelay);

				this.addOKLink(anteriorModify, disconnectUnit);
				anteriorModify = disconnectUnit;
				link = this.addKOLink(anteriorModify, multiMessageUnit);
				this.setAutomaticCoupling(link);

				this.putMessageOnMultiMessageUnit(link, multiMessageUnit, "Error modifying data in " + this.getNombreEntity());
			}
		}
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// KOLink para Nuevo elemento y OKLink para modificar

		link = this.addKOLink(isNotNullUnit, createUnit);
		// Quitar coupling automatico
		this.setAutomaticCoupling(link);
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);
		link = this.addOKLink(isNotNullUnit, modifyUnit);
		// Quitar coupling automatico
		this.setAutomaticCoupling(link);
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		// A�adir OKLink y KOLink de las create Y Modify
		link = this.addOKLink(anteriorCreate, multiMessageUnit);
		this.setAutomaticCoupling(link);

		this.putMessageOnMultiMessageUnit(link, multiMessageUnit, "The data from " + this.getNombreEntity() + " has been create/modify");
		subProgressMonitor.worked(unidad);
		Thread.sleep(this.generationDelay);

		if (firstCreate == null) {
			link = this.addOKLink(anteriorModify, multiMessageUnit);
			this.setAutomaticCoupling(link);

			this.putMessageOnMultiMessageUnit(link, multiMessageUnit, "The data from " + this.getNombreEntity() + " has been create/modify");

		} else {
			link = this.addOKLink(anteriorModify, firstCreate);
			this.setAutomaticCoupling(link);
		}
	}
}
