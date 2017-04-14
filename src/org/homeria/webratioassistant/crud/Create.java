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
public class Create extends CRUD {
	/**
	 * Create.java: Clase que se encarga de generar los elementos encargados de
	 * realizar la operaci�n de Crear/Create. Es necesario especificar los
	 * siteviews donde se deber� colocar, adem�s de los atributos y relaciones
	 * que se deberan mostrar en la unidad EntryUnit.
	 */
	public Create(IMFElement entity, List<ISiteView> siteViews, List<IArea> areas, Map<IRelationshipRole, IAttribute> relation,
			List<IAttribute> atributos,int generationDelay) {
		super(siteViews, areas, entity, atributos, ((IEntity) entity).getAllAttributeList(), relation, generationDelay);

	}

	/**
	 * Nombre: ejecutar Funcion: Ejecuta las instrucciones para crear una p�gina
	 * que permite la creaci�n de datos en una entidad.
	 * 
	 * @param subProgressMonitor
	 */
	public void ejecutar(SubProgressMonitor subProgressMonitor) {
		int unidad = 1;
		int totalWork = this.getListaSiteViews().size() * (7 + (2 * this.getRelaciones().size())) * unidad;
		subProgressMonitor.beginTask("Create", totalWork);
		try {
			ISiteView siteView;

			// Funcionamiento base:
			// si tiene un area seleccionada lo hago en ese area
			// si tiene dos areas seleccionados lo hago dentro de esos areas
			// sino tiene area seleccionada: lo hago para es

			for (Iterator<ISiteView> iteradorSiteView = this.getListaSiteViews().iterator(); iteradorSiteView.hasNext();) {

				siteView = iteradorSiteView.next();

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

				if (null == listaAreaEnc) {
					Utilities.switchSiteView(siteView);
					createCrearElementos(subProgressMonitor, unidad, siteView);
					subProgressMonitor.worked(unidad);Thread.sleep(this.generationDelay);
				} else {
					for (Iterator iterator = listaAreaEnc.iterator(); iterator.hasNext();) {
						IArea iArea = (IArea) iterator.next();
						Utilities.switchSiteView(siteView);
						createCrearElementos(subProgressMonitor, unidad, iArea);
						subProgressMonitor.worked(unidad);Thread.sleep(this.generationDelay);

					}
				}
				subProgressMonitor.worked(unidad);Thread.sleep(this.generationDelay);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			subProgressMonitor.done();
		}
	}

	/**
	 * 
	 * Nombre: createCrearElementos Funcion:
	 * 
	 * @param subProgressMonitor
	 * @param unidad
	 * @param elementIMFE
	 * @throws InterruptedException 
	 */
	private void createCrearElementos(SubProgressMonitor subProgressMonitor, int unidad, IMFElement elementIMFE) throws InterruptedException {
		IPage pagina;
		IOperationUnit createUnit;
		IContentUnit entryUnit;
		IContentUnit selectorUnit;
		IContentUnit multiMessageUnit;
		IEntity entidadPreload;
		IMFElement link;
		Point posicion;
		int x;
		int y;
		int posx;
		int posy;
		posicion = Utilities.buscarHueco();
		x = posicion.x;
		y = posicion.y;

		// Buscamos otra vez primer hueco libre
		posicion = Utilities.buscarHueco();
		x = posicion.x;
		y = posicion.y;

		// Crear Pagina del CRUD AllInOne
		pagina = (IPage) this.addPagina(elementIMFE, "Create", x, y);
		subProgressMonitor.worked(unidad);Thread.sleep(this.generationDelay);

		posx = posy = 5;

		if (this.getRelaciones().size() > 0)
			posx = posx + Utilities.anchoUnidad;

		// A�adir el formulario y poner los campos
		entryUnit = (IContentUnit) this.addUnidad(pagina, "EntryUnit", posx, posy, "Form", true, null);
		this.setFields(entryUnit, false, true);
		subProgressMonitor.worked(unidad);Thread.sleep(this.generationDelay);

		// Read
		posy = posy + Utilities.altoUnidad;

		// A�adir multiMessage para los mensajes de las unidades
		multiMessageUnit = (IContentUnit) this.addUnidad(pagina, "MultiMessageUnit", posx, posy, "Message", false, null);
		subProgressMonitor.worked(unidad);Thread.sleep(this.generationDelay);

		// A�adir Selector para precarga formulario (Create)
		// Solo son validas las relaciones NaN, las dem�s las
		// carga la selectorEntidad directamente.
		IRelationship relation;
		IRelationshipRole role;
		posx = 5;
		posy = 5;

		for (Iterator<IRelationshipRole> iteradorRole = this.getRelaciones().keySet().iterator(); iteradorRole.hasNext();) {
			role = iteradorRole.next();
			entidadPreload = this.getTargetEntity(role);

			selectorUnit = (IContentUnit) this.addUnidad(pagina, "SelectorUnit", posx, posy, "Entity", false, entidadPreload);
			subProgressMonitor.worked(unidad);Thread.sleep(this.generationDelay);
			posy = posy + Utilities.anchoUnidad;
			link = this.addTransportLink(selectorUnit, entryUnit, "Load");
			this.setAutomaticCoupling(link);
			this.putPreload(entryUnit, role, link);
			subProgressMonitor.worked(unidad);Thread.sleep(this.generationDelay);
		}

		posicion = Utilities.buscarHueco();
		x = posicion.x;
		y = posicion.y;

		createUnit = (IOperationUnit) this.addUnidad(elementIMFE, "CreateUnit", x, y, "Create", true, null);
		link = this.addNormalLink(entryUnit, createUnit, "Cargar");
		this.setAutomaticCoupling(link);
		this.guessCouplingEntryToCreateModify(entryUnit, createUnit, link);
		subProgressMonitor.worked(unidad);Thread.sleep(this.generationDelay);

		IEntity entidad;
		IOperationUnit connectUnit;
		String nombreRole, idRole;
		IMFElement anteriorCreate = createUnit;
		IMFElement firstCreate = null;

		// KO Links de create
		link = this.addKOLink(anteriorCreate, multiMessageUnit);
		this.setAutomaticCoupling(link);

		this.putMessageOnMultiMessageUnit(link, multiMessageUnit, "Falied creating data in " + this.getNombreEntity());
		subProgressMonitor.worked(unidad);Thread.sleep(this.generationDelay);

		for (Iterator<IRelationshipRole> iteradorRole = this.getRelaciones().keySet().iterator(); iteradorRole.hasNext();) {
			role = iteradorRole.next();
			entidad = this.getTargetEntity(role);
			relation = this.isNtoN(role);
			if (relation != null) {

				y = y + Utilities.altoUnidad;
				nombreRole = Utilities.getAttribute(role, "name");
				idRole = Utilities.getAttribute(role, "id");
				connectUnit = (IOperationUnit) this.addUnidad(elementIMFE, "ConnectUnit", x, y, nombreRole, false, null);
				Utilities.setAttribute(connectUnit, "relationship", idRole);
				subProgressMonitor.worked(unidad);Thread.sleep(this.generationDelay);

				this.addOKLink(anteriorCreate, connectUnit);
				link = this.addTransportLink(entryUnit, connectUnit, "Load");
				this.setAutomaticCoupling(link);
				this.guessCouplingEntryToConnect(entryUnit, connectUnit, entidad, role, link);
				anteriorCreate = connectUnit;
				link = this.addKOLink(anteriorCreate, multiMessageUnit);
				this.setAutomaticCoupling(link);

				this.putMessageOnMultiMessageUnit(link, multiMessageUnit, "Error creating data");
				subProgressMonitor.worked(unidad);
				Thread.sleep(this.generationDelay);
				if (firstCreate == null)
					firstCreate = connectUnit;
			}
		}

		// A�adir OKLink y KOLink de la create
		link = this.addOKLink(anteriorCreate, multiMessageUnit);
		this.setAutomaticCoupling(link);
		this.putMessageOnMultiMessageUnit(link, multiMessageUnit, "The data has been create");
	}
}
