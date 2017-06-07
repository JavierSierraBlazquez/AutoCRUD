package org.homeria.webratioassistant.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.homeria.webratioassistant.elements.ConnectUnit;
import org.homeria.webratioassistant.elements.CreateUnit;
import org.homeria.webratioassistant.elements.DataFlow;
import org.homeria.webratioassistant.elements.DataUnit;
import org.homeria.webratioassistant.elements.DeleteUnit;
import org.homeria.webratioassistant.elements.DisconnectUnit;
import org.homeria.webratioassistant.elements.ElementType;
import org.homeria.webratioassistant.elements.EntryUnit;
import org.homeria.webratioassistant.elements.IsNotNullUnit;
import org.homeria.webratioassistant.elements.KOLink;
import org.homeria.webratioassistant.elements.Link;
import org.homeria.webratioassistant.elements.MultiMessageUnit;
import org.homeria.webratioassistant.elements.NoOpContentUnit;
import org.homeria.webratioassistant.elements.NormalNavigationFlow;
import org.homeria.webratioassistant.elements.OKLink;
import org.homeria.webratioassistant.elements.Page;
import org.homeria.webratioassistant.elements.PowerIndexUnit;
import org.homeria.webratioassistant.elements.ReconnectUnit;
import org.homeria.webratioassistant.elements.SelectorUnit;
import org.homeria.webratioassistant.elements.Unit;
import org.homeria.webratioassistant.elements.UpdateUnit;
import org.homeria.webratioassistant.elements.WebRatioElement;
import org.homeria.webratioassistant.elements.XOR;
import org.homeria.webratioassistant.plugin.Utilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;

public class PatternParser {
	private static int SEPARACION_UNIDADES = 120;

	private Document doc;
	private File fXmlFile;
	private DocumentBuilder dBuilder;

	private IEntity entity;

	private Queue<WebRatioElement> pages;
	private List<Unit> units;
	private List<Link> links;

	public PatternParser(String path, IEntity entity) {
		this.pages = new LinkedList<WebRatioElement>();
		this.units = new ArrayList<Unit>();
		this.links = new ArrayList<Link>();
		this.entity = entity;

		this.fXmlFile = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			this.dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		this.generateDoc();
	}

	public Queue<WebRatioElement> getPages() {
		return this.pages;
	}

	public void setPages(Queue<WebRatioElement> pages) {
		this.pages = pages;
	}

	public List<Unit> getUnits() {
		return this.units;
	}

	public void setUnits(List<Unit> units) {
		this.units = units;
	}

	public List<Link> getLinks() {
		return this.links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	private void generateDoc() {
		try {
			this.doc = this.dBuilder.parse(this.fXmlFile);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parsePagesSection() {
		// Procesamos todas las páginas:
		NodeList pagesNodeList = this.doc.getElementsByTagName("Pages").item(0).getChildNodes();
		for (int iPage = 0; iPage < pagesNodeList.getLength(); iPage++) {
			Node nodePage = pagesNodeList.item(iPage);
			if (nodePage instanceof Element) {

				Element page = (Element) nodePage;
				// null because is the first page. Its parent is the siteView selected in the UI
				this.parsePage(page, null);
			}
		}
	}

	private void parsePage(Element page, String parentId) {
		if (page.getTagName().equals(ElementType.PAGE))
			this.pages.add(new Page(page.getAttribute("id"), page.getAttribute("name"), parentId, page.getAttribute("default"), page
					.getAttribute("landmark"), page.getAttribute("x"), page.getAttribute("y")));

		else if (page.getTagName().equals(ElementType.XOR_PAGE))
			this.pages.add(new XOR(page.getAttribute("id"), page.getAttribute("name"), parentId, page.getAttribute("x"), page
					.getAttribute("y")));

		// Proceso los elementos dentro de cada página:
		NodeList pageChild = page.getChildNodes();
		for (int iUnit = 0; iUnit < pageChild.getLength(); iUnit++) {
			Node nodeElement = pageChild.item(iUnit);
			if (nodeElement instanceof Element) {

				Element element = (Element) nodeElement;
				if (element.getTagName().equals(ElementType.PAGE) || element.getTagName().equals(ElementType.XOR_PAGE))
					this.parsePage(element, page.getAttribute("id"));
				else {
					element.setAttribute("parentId", page.getAttribute("id"));
					// Creo la unit
					this.createElement(element, this.entity);
				}
			}
		}

	}

	public void parseOpUnitsSection() {
		NodeList opuChilds = this.doc.getElementsByTagName("OperationUnits").item(0).getChildNodes();

		for (int i = 0; i < opuChilds.getLength(); i++) {
			Node node = opuChilds.item(i);
			if (node instanceof Element) {
				Element opUnit = (Element) node;
				this.createElement(opUnit, this.entity);
			}
		}
	}

	public void parseLinksSection() {
		NodeList linkChilds = this.doc.getElementsByTagName("Links").item(0).getChildNodes();

		for (int i = 0; i < linkChilds.getLength(); i++) {
			Node node = linkChilds.item(i);
			if (node instanceof Element) {
				Element link = (Element) node;
				this.createElement(link, this.entity);
			}
		}

	}

	public void parseRelationsSection(Set<IRelationshipRole> relationshipRolesSelected) {
		// forEachRelation
		int countRelation = 0;
		int countNM = 0;
		boolean someNMrelation = false;

		for (IRelationshipRole role : relationshipRolesSelected) {

			// Dentro del bucle, asi en cada pasada se cogen los elementos originales y no los modificados por la iteración anterior
			this.generateDoc();
			NodeList relationsNodeList = this.doc.getElementsByTagName("forEachRelation").item(0).getChildNodes();

			for (int i = 0; i < relationsNodeList.getLength(); i++) {
				Node node = relationsNodeList.item(i);
				if (node instanceof Element) {
					Element element = (Element) node;

					if (element.getTagName().equals("IfNMRelation")) {
						if (null != this.isNtoN(role, this.entity)) {
							someNMrelation = true;
							this.NMrelationSection(element.getChildNodes(), role, countNM++);
						}

					} else {
						this.replaceMarkerWithNum(element, countRelation);
						this.createElement(element, role);
					}
				}
			}
			countRelation++;
		}

		NodeList elementNodeList;

		if (someNMrelation) {

			elementNodeList = this.doc.getElementsByTagName("IfSomeNMRelation").item(0).getChildNodes();
			for (int i = 0; i < elementNodeList.getLength(); i++) {
				Node node = elementNodeList.item(i);
				if (node instanceof Element) {
					Element element = (Element) node;

					this.replaceMarkerWithNum(element, countNM - 1);
					this.createElement(element, this.entity);
				}
			}
		} else {

			elementNodeList = this.doc.getElementsByTagName("IfNotSomeNMRelation").item(0).getChildNodes();
			for (int i = 0; i < elementNodeList.getLength(); i++) {
				Node node = elementNodeList.item(i);
				if (node instanceof Element) {
					Element element = (Element) node;

					this.replaceMarkerWithNum(element, countRelation - 1);
					this.createElement(element, this.entity);
				}
			}
		}

	}

	private void NMrelationSection(NodeList nmElements, IRelationshipRole role, int count) {
		for (int i = 0; i < nmElements.getLength(); i++) {

			Node node = nmElements.item(i);
			if (node instanceof Element) {
				Element nmElement = (Element) node;

				if (nmElement.getTagName().equals("IfFirstRelation")) {

					if (count == 0) {
						NodeList firstRelElements = nmElement.getChildNodes();

						for (int j = 0; j < firstRelElements.getLength(); j++) {
							Node node2 = firstRelElements.item(j);
							if (node2 instanceof Element) {
								Element firstRelElement = (Element) node2;

								this.replaceMarkerWithNum(firstRelElement, count);

								// Solo va a crearse una vez, dependiendo del tipo que sea:
								if (!this.createElement(firstRelElement, role))
									this.createElement(firstRelElement, this.entity);
							}
						}
					}

				} else if (nmElement.getTagName().equals("IfNotFirstRelation")) {

					if (count > 0) {
						NodeList notFirstRelElements = nmElement.getChildNodes();

						for (int j = 0; j < notFirstRelElements.getLength(); j++) {
							Node node2 = notFirstRelElements.item(j);
							if (node2 instanceof Element) {
								Element notFirstRelElement = (Element) node2;

								this.replaceMarkerWithNum(notFirstRelElement, count);
								// Solo va a crearse una vez, dependiendo del tipo que sea:
								if (!this.createElement(notFirstRelElement, role))
									this.createElement(notFirstRelElement, this.entity);
							}
						}
					}

				} else {

					this.replaceMarkerWithNum(nmElement, count);

					// Solo va a crearse una vez, dependiendo del tipo que sea:
					if (!this.createElement(nmElement, role))
						this.createElement(nmElement, this.entity);
				}
			}
		}

	}

	// sustituye la almohadilla por el parámetro y opera si fuese necesario #-n #-n
	// en los atributos id, sourceId y targetId
	private void replaceMarkerWithNum(Element element, int count) {
		if (element.getAttribute("id").contains("#") || element.getAttribute("id").contains("%"))
			element.setAttribute("id", this.replaceMarkerWithNum2(element.getAttribute("id"), count));

		if (element.getAttribute("name").contains("#") || element.getAttribute("name").contains("%"))
			element.setAttribute("name", this.replaceMarkerWithNum2(element.getAttribute("name"), count));

		if (element.getAttribute("sourceId").contains("#") || element.getAttribute("sourceId").contains("%"))
			element.setAttribute("sourceId", this.replaceMarkerWithNum2(element.getAttribute("sourceId"), count));

		if (element.getAttribute("targetId").contains("#") || element.getAttribute("targetId").contains("%"))
			element.setAttribute("targetId", this.replaceMarkerWithNum2(element.getAttribute("targetId"), count));

		if (element.getAttribute("x").contains("#") || element.getAttribute("x").contains("%"))
			element.setAttribute("x", this.replaceCoords(element.getAttribute("x"), count));

		if (element.getAttribute("y").contains("#") || element.getAttribute("y").contains("%"))
			element.setAttribute("y", this.replaceCoords(element.getAttribute("y"), count));
	}

	// str formato: cadena# || cadena#-n || cadena#+n
	private String replaceMarkerWithNum2(String str, int count) {
		String marker = "";
		if (str.contains("#"))
			marker = "#";
		else if (str.contains("%"))
			marker = "%";

		String s2[] = str.split(marker);
		if (s2.length > 1) {
			// tanto si es + como - cojo el número a la derecha
			if (s2[1].contains("-")) {
				count -= Integer.valueOf(s2[1].split("-")[1]);
			} else if (s2[1].contains("+")) {
				count += Integer.valueOf(s2[1].split("\\+")[1]);
			}
		}
		s2[0] += String.valueOf(count);
		return s2[0];
	}

	// str formato: 200# || 200% RESULTADO: 200*count
	private String replaceCoords(String str, int count) {
		String marker = "";
		if (str.contains("#"))
			marker = "#";
		else if (str.contains("%"))
			marker = "%";

		String s2[] = str.split(marker);
		int value = Integer.valueOf(s2[0]);
		value += SEPARACION_UNIDADES * count;
		s2[0] = String.valueOf(value);

		return s2[0];
	}

	private void createElement(Element xmlUnit, IEntity entity) {
		String nodeName = xmlUnit.getNodeName();

		if (nodeName.equals(ElementType.POWER_INDEX_UNIT)) {
			this.units.add(new PowerIndexUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("parentId"),
					xmlUnit.getAttribute("x"), xmlUnit.getAttribute("y"), entity));

		} else if (nodeName.equals(ElementType.DATA_UNIT)) {
			this.units.add(new DataUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("parentId"), xmlUnit
					.getAttribute("x"), xmlUnit.getAttribute("y"), entity));

		} else if (nodeName.equals(ElementType.MULTI_MESSAGE_UNIT)) {
			this.units.add(new MultiMessageUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("parentId"),
					xmlUnit.getAttribute("x"), xmlUnit.getAttribute("y"), entity));

		} else if (nodeName.equals(ElementType.ENTRY_UNIT)) {
			this.units.add(new EntryUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("parentId"),
					xmlUnit.getAttribute("type"), xmlUnit.getAttribute("x"), xmlUnit.getAttribute("y"), entity));

		} else if (nodeName.equals(ElementType.SELECTOR_UNIT)) {
			this.units.add(new SelectorUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("parentId"),
					xmlUnit.getAttribute("type"), xmlUnit.getAttribute("x"), xmlUnit.getAttribute("y"), entity));

		} else if (nodeName.equals(ElementType.NO_OP_CONTENT_UNIT)) {
			this.units.add(new NoOpContentUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("parentId"),
					xmlUnit.getAttribute("x"), xmlUnit.getAttribute("y"), entity));

		} else if (nodeName.equals(ElementType.IS_NOT_NULL_UNIT)) {
			this.units.add(new IsNotNullUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("parentId"),
					xmlUnit.getAttribute("x"), xmlUnit.getAttribute("y"), null));

		} else if (nodeName.equals(ElementType.CREATE_UNIT)) {
			this.units.add(new CreateUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("x"), xmlUnit
					.getAttribute("y"), entity));

		} else if (nodeName.equals(ElementType.DELETE_UNIT)) {
			this.units.add(new DeleteUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("x"), xmlUnit
					.getAttribute("y"), entity));

		} else if (nodeName.equals(ElementType.UPDATE_UNIT)) {
			this.units.add(new UpdateUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("x"), xmlUnit
					.getAttribute("y"), entity));

		} else if (nodeName.equals(ElementType.NORMAL_NAVIGATION_FLOW)) {
			this.links.add(new NormalNavigationFlow(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit
					.getAttribute("sourceId"), xmlUnit.getAttribute("targetId"), xmlUnit.getAttribute("type"), xmlUnit
					.getAttribute("validate"), entity));

		} else if (nodeName.equals(ElementType.DATA_FLOW)) {
			this.links.add(new DataFlow(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("sourceId"), xmlUnit
					.getAttribute("targetId"), xmlUnit.getAttribute("type"), entity));

		} else if (nodeName.equals(ElementType.OK_LINK)) {
			this.links.add(new OKLink(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("sourceId"), xmlUnit
					.getAttribute("targetId"), xmlUnit.getAttribute("type"), xmlUnit.getAttribute("message")));

		} else if (nodeName.equals(ElementType.KO_LINK)) {
			this.links.add(new KOLink(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("sourceId"), xmlUnit
					.getAttribute("targetId"), xmlUnit.getAttribute("type"), xmlUnit.getAttribute("message")));

		}

	}

	private boolean createElement(Element xmlUnit, IRelationshipRole role) {
		String nodeName = xmlUnit.getNodeName();
		boolean created = false;

		if (nodeName.equals(ElementType.DATA_FLOW)) {
			created = this.links.add(new DataFlow(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit
					.getAttribute("sourceId"), xmlUnit.getAttribute("targetId"), xmlUnit.getAttribute("type"), this.entity, role));

		} else if (nodeName.equals(ElementType.CONNECT_UNIT)) {
			created = this.units.add(new ConnectUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("x"),
					xmlUnit.getAttribute("y"), this.entity, role));

		} else if (nodeName.equals(ElementType.DISCONNECT_UNIT)) {
			created = this.units.add(new DisconnectUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("x"),
					xmlUnit.getAttribute("y"), this.entity, role));

		} else if (nodeName.equals(ElementType.RECONNECT_UNIT)) {
			created = this.units.add(new ReconnectUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("x"),
					xmlUnit.getAttribute("y"), this.entity, role));

		} else if (nodeName.equals(ElementType.SELECTOR_UNIT)) {
			created = this.units.add(new SelectorUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit
					.getAttribute("parentId"), xmlUnit.getAttribute("type"), xmlUnit.getAttribute("x"), xmlUnit.getAttribute("y"), this
					.getTargetEntity(role), role));
		}

		return created;
	}

	private IEntity getTargetEntity(IRelationshipRole role) {
		IRelationship relation = (IRelationship) role.getParentElement();
		if (relation.getTargetEntity() == this.entity) {
			return relation.getSourceEntity();
		} else
			return relation.getTargetEntity();

	}

	/**
	 * Nombre: isNtoN Funcion: Comprueba si la relacion es NaN
	 * 
	 * @param role
	 *            : relacion en la que comprobar la cardinalidad
	 * @return: la relacion en caso se existir o null en caso contrario
	 */
	private IRelationship isNtoN(IRelationshipRole role, IEntity entity) {
		IEntity entidad1 = entity;
		List<IRelationship> lista = entidad1.getOutgoingRelationshipList();
		lista.addAll(entidad1.getIncomingRelationshipList());
		IRelationship relacion = (IRelationship) role.getParentElement();
		IRelationshipRole role1;
		IRelationshipRole role2;
		String maxCard1;
		String maxCard2;
		role1 = relacion.getRelationshipRole1();
		role2 = relacion.getRelationshipRole2();
		maxCard1 = Utilities.getAttribute(role1, "maxCard");
		maxCard2 = Utilities.getAttribute(role2, "maxCard");
		// Si ambos cardinales son N se retorna la relación
		if ((maxCard1.equals("N")) && (maxCard2.equals("N")))
			return relacion;
		return null;

	}
}
