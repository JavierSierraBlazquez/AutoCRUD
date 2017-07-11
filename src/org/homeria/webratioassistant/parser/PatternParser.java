package org.homeria.webratioassistant.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.homeria.webratioassistant.exceptions.CantOpenFileException;
import org.homeria.webratioassistant.exceptions.CantParseXmlFileException;
import org.homeria.webratioassistant.exceptions.ExceptionHandler;
import org.homeria.webratioassistant.exceptions.IdNotUniqueException;
import org.homeria.webratioassistant.exceptions.MissingSectionException;
import org.homeria.webratioassistant.exceptions.NoIdException;
import org.homeria.webratioassistant.exceptions.NoSourceIdException;
import org.homeria.webratioassistant.exceptions.NoTargetIdException;
import org.homeria.webratioassistant.registry.Registry;
import org.homeria.webratioassistant.webratio.Utilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;

public class PatternParser {
	private static int UNIT_GAP = 120;

	private static String PAGES_SECTION = "PAGES SECTION";
	private static String OPUNITS_SECTION = "OPERATION UNITS SECTION";
	private static String LINKS_SECTION = "LINKS SECTION";
	private static String RELATIONS_SECTION = "RELATIONS SECTION";
	private static String NMRELATIONS_SECTION = "N:M RELATIONS SECTION";
	private static String IFFIRSTRELATION_SECTION = "IfFirstRelation into N:M RELATIONS SECTION";
	private static String IFNOTFIRSTRELATION_SECTION = "IfNotFirstRelation into N:M RELATIONS SECTION";
	private static String IFSOMENMRELATION_SECTION = "IfSomeNMRelation after N:M RELATIONS SECTION";
	private static String IFNOTSOMENMRELATION_SECTION = "IfNotSomeNMRelation after N:M RELATIONS SECTION";

	private Document doc;
	private File fXmlFile;
	private DocumentBuilder dBuilder;

	private IEntity entity;

	private Queue<WebRatioElement> pages;
	private List<Unit> units;
	private List<Link> links;

	private List<String> idPool;

	public PatternParser(String path, IEntity entity) throws CantOpenFileException, CantParseXmlFileException {
		this.pages = new LinkedList<WebRatioElement>();
		this.units = new ArrayList<Unit>();
		this.links = new ArrayList<Link>();
		this.entity = entity;
		this.idPool = new ArrayList<String>();

		this.fXmlFile = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			this.dBuilder = dbFactory.newDocumentBuilder();
			this.generateDoc();

			String id = this.doc.getDocumentElement().getAttribute("id");
			String name = this.doc.getDocumentElement().getAttribute("name");
			Registry.getInstance().setPatternData(id, name);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void checkPatternsIdAreUnique(File[] files) {
		List<String> patternsIdsList = new ArrayList<String>();
		Document doc;
		DocumentBuilder dBuilder;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			dBuilder = dbFactory.newDocumentBuilder();

			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile() && files[i].getName().contains(".xml")) {
					try {

						doc = dBuilder.parse(files[i]);
						String patternId = doc.getDocumentElement().getAttribute("id");

						if (null == patternId || patternId.isEmpty())
							throw new NoIdException("Root element of " + files[i].getAbsolutePath());

						if (patternsIdsList.contains(patternId))
							throw new IdNotUniqueException(patternId, "Root element of " + files[i].getAbsolutePath());
						else
							patternsIdsList.add(patternId);

					} catch (NoIdException e) {
						ExceptionHandler.handle(e);
					} catch (IdNotUniqueException e) {
						ExceptionHandler.handle(e);
					} catch (IOException e) {
						throw new CantOpenFileException(files[i].getAbsolutePath());
					} catch (SAXException e) {
						throw new CantParseXmlFileException(files[i].getAbsolutePath());
					}
				}
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private void generateDoc() throws CantOpenFileException, CantParseXmlFileException {
		try {
			this.doc = this.dBuilder.parse(this.fXmlFile);
		} catch (IOException e) {
			throw new CantOpenFileException(this.fXmlFile.getAbsolutePath());
		} catch (SAXException e) {
			throw new CantParseXmlFileException(this.fXmlFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void parsePagesSection() throws NoIdException, IdNotUniqueException, NoSourceIdException, NoTargetIdException,
			MissingSectionException {
		// Procesamos todas las páginas:
		NodeList pagesNodeList;
		try {
			pagesNodeList = this.doc.getElementsByTagName("Pages").item(0).getChildNodes();
		} catch (Exception e) {
			throw new MissingSectionException(PAGES_SECTION, e);
		}

		for (int iPage = 0; iPage < pagesNodeList.getLength(); iPage++) {
			Node nodePage = pagesNodeList.item(iPage);
			if (nodePage instanceof Element) {

				Element page = (Element) nodePage;
				// null because is the first page. Its parent is the siteView selected in the UI
				this.parsePage(page, null);
			}
		}
	}

	private void parsePage(Element page, String parentId) throws NoIdException, IdNotUniqueException, NoSourceIdException,
			NoTargetIdException {
		this.validateAttributes(page, PAGES_SECTION);
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
					this.validateAttributes(element, PAGES_SECTION);
					element.setAttribute("parentId", page.getAttribute("id"));
					// Creo la unit
					this.createElement(element, this.entity);
				}
			}
		}

	}

	public void parseOpUnitsSection() throws NoIdException, IdNotUniqueException, NoSourceIdException, NoTargetIdException,
			MissingSectionException {
		NodeList opuChilds;

		try {
			opuChilds = this.doc.getElementsByTagName("OperationUnits").item(0).getChildNodes();
		} catch (Exception e) {
			throw new MissingSectionException(OPUNITS_SECTION, e);
		}

		for (int i = 0; i < opuChilds.getLength(); i++) {
			Node node = opuChilds.item(i);
			if (node instanceof Element) {
				Element opUnit = (Element) node;

				this.validateAttributes(opUnit, OPUNITS_SECTION);
				this.createElement(opUnit, this.entity);
			}
		}
	}

	public void parseLinksSection() throws NoIdException, IdNotUniqueException, NoSourceIdException, NoTargetIdException,
			MissingSectionException {
		NodeList linkChilds;
		try {
			linkChilds = this.doc.getElementsByTagName("Links").item(0).getChildNodes();
		} catch (Exception e) {
			throw new MissingSectionException(LINKS_SECTION, e);
		}

		for (int i = 0; i < linkChilds.getLength(); i++) {
			Node node = linkChilds.item(i);
			if (node instanceof Element) {
				Element link = (Element) node;

				this.validateAttributes(link, LINKS_SECTION);
				this.createElement(link, this.entity);
			}
		}

	}

	public void parseRelationsSection(Set<IRelationshipRole> relationshipRolesSelected) throws CantOpenFileException,
			CantParseXmlFileException, NoIdException, IdNotUniqueException, NoSourceIdException, NoTargetIdException,
			MissingSectionException {
		// forEachRelation
		int countRelation = 0;
		int countNM = 0;
		boolean someNMrelation = false;

		for (IRelationshipRole role : relationshipRolesSelected) {

			// Dentro del bucle, asi en cada pasada se cogen los elementos originales y no los modificados por la iteración anterior
			this.generateDoc();
			NodeList relationsNodeList;
			try {
				relationsNodeList = this.doc.getElementsByTagName("forEachRelation").item(0).getChildNodes();
			} catch (Exception e) {
				throw new MissingSectionException(RELATIONS_SECTION, e);
			}

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
						this.validateAttributes(element, RELATIONS_SECTION);
						this.createElement(element, role);
					}
				}
			}
			countRelation++;
		}

		NodeList elementNodeList;

		if (someNMrelation) {
			try {
				elementNodeList = this.doc.getElementsByTagName("IfSomeNMRelation").item(0).getChildNodes();
			} catch (Exception e) {
				throw new MissingSectionException(IFSOMENMRELATION_SECTION, e);
			}
			for (int i = 0; i < elementNodeList.getLength(); i++) {
				Node node = elementNodeList.item(i);
				if (node instanceof Element) {
					Element element = (Element) node;

					this.replaceMarkerWithNum(element, countNM - 1);
					this.validateAttributes(element, IFSOMENMRELATION_SECTION);
					this.createElement(element, this.entity);
				}
			}
		} else {
			try {
				elementNodeList = this.doc.getElementsByTagName("IfNotSomeNMRelation").item(0).getChildNodes();
			} catch (Exception e) {
				throw new MissingSectionException(IFNOTSOMENMRELATION_SECTION, e);
			}
			for (int i = 0; i < elementNodeList.getLength(); i++) {
				Node node = elementNodeList.item(i);
				if (node instanceof Element) {
					Element element = (Element) node;

					this.replaceMarkerWithNum(element, countRelation - 1);
					this.validateAttributes(element, IFNOTSOMENMRELATION_SECTION);
					this.createElement(element, this.entity);
				}
			}
		}

	}

	private void NMrelationSection(NodeList nmElements, IRelationshipRole role, int count) throws NoIdException, IdNotUniqueException,
			NoSourceIdException, NoTargetIdException {
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
								this.validateAttributes(firstRelElement, IFFIRSTRELATION_SECTION);
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
								this.validateAttributes(notFirstRelElement, IFNOTFIRSTRELATION_SECTION);
								// Solo va a crearse una vez, dependiendo del tipo que sea:
								if (!this.createElement(notFirstRelElement, role))
									this.createElement(notFirstRelElement, this.entity);
							}
						}
					}

				} else {

					this.replaceMarkerWithNum(nmElement, count);
					this.validateAttributes(nmElement, NMRELATIONS_SECTION);
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
		value += UNIT_GAP * count;
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
					xmlUnit.getAttribute("x"), xmlUnit.getAttribute("y")));

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
			this.units.add(new IsNotNullUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"), xmlUnit.getAttribute("x"), xmlUnit
					.getAttribute("y"), null));

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
			created = this.units.add(new DisconnectUnit(xmlUnit.getAttribute("id"), xmlUnit.getAttribute("name"),
					xmlUnit.getAttribute("x"), xmlUnit.getAttribute("y"), this.entity, role));

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

	private void validateAttributes(Element element, String section) throws NoIdException, IdNotUniqueException, NoSourceIdException,
			NoTargetIdException {
		// ID VALIDATION SECTION
		String id = element.getAttribute("id");
		if (id.isEmpty())
			throw new NoIdException(section);

		if (this.idPool.contains(id))
			throw new IdNotUniqueException(id, section);
		this.idPool.add(id);

		// COORDINATES AND SOURCE/TARGET (LINK) VALIDATION SECTION
		List<String> linkTypes = new ArrayList<String>();
		linkTypes
				.addAll(Arrays.asList(ElementType.NORMAL_NAVIGATION_FLOW, ElementType.DATA_FLOW, ElementType.OK_LINK, ElementType.KO_LINK));

		String tagName = element.getTagName();
		if (linkTypes.contains(element.getTagName())) {
			// is a Link element
			if (element.getAttribute("sourceId").isEmpty())
				throw new NoSourceIdException(tagName, section);
			if (element.getAttribute("targetId").isEmpty())
				throw new NoTargetIdException(tagName, section);
		} else {
			// is not a Link element
			// FIXME quitar?
		}

	}
}
