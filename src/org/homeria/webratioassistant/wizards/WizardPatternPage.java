package org.homeria.webratioassistant.wizards;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.homeria.webratioassistant.elements.CreateUnit;
import org.homeria.webratioassistant.elements.DataUnit;
import org.homeria.webratioassistant.elements.Link;
import org.homeria.webratioassistant.elements.PowerIndexUnit;
import org.homeria.webratioassistant.elements.Unit;
import org.homeria.webratioassistant.elements.UpdateUnit;
import org.homeria.webratioassistant.elements.WebRatioElement;
import org.homeria.webratioassistant.exceptions.ExceptionHandler;
import org.homeria.webratioassistant.exceptions.NoPatternFileFoundException;
import org.homeria.webratioassistant.exceptions.NoPatternsFolderFoundException;
import org.homeria.webratioassistant.parser.PatternParser;
import org.homeria.webratioassistant.webratio.MyIEntityComparator;
import org.homeria.webratioassistant.webratio.ProjectParameters;
import org.homeria.webratioassistant.webratio.Utilities;
import org.xml.sax.SAXException;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISiteView;

public class WizardPatternPage extends WizardPage {
	private String PATTERNS_DIR;
	private Combo entityCombo;
	private Combo patternCombo;

	private Composite containerComposite;
	private Composite leftComposite;
	private Composite rightComposite;
	private Composite relationsComposite;

	private Group entityGroup;
	private Group patternGroup;
	private Group svAreasGroup;

	private Table tableRelations;

	private TabFolder tabFolder;

	private Tree svAreasTree;

	private List<CCombo> listCombosRelations;

	private List<String> patternFileList;
	private List<ISiteView> listaSiteViews;
	private List<IAttribute> listaAtributosEntidad;
	private List<IAttribute> listaAtributosSinDerivados;
	private List<IEntity> entityList;
	private List<IRelationshipRole> relatedEntities;

	private Queue<WebRatioElement> pages;
	private List<Unit> units;
	private List<Link> links;

	private IEntity entitySelected;

	PatternParser xmlParser;
	private Map<String, IAttribute> relationAttributes;

	public WizardPatternPage() {
		super("WizardPattern");
		this.setTitle("Automatic pattern generation");

		this.patternFileList = new ArrayList<String>();
		this.listaSiteViews = new ArrayList<ISiteView>();
		this.entityList = new ArrayList<IEntity>();
		this.listaAtributosEntidad = new ArrayList<IAttribute>();
		this.listaAtributosSinDerivados = new ArrayList<IAttribute>();
		this.listCombosRelations = new ArrayList<CCombo>();
		this.relationAttributes = new HashMap<String, IAttribute>();

		this.PATTERNS_DIR = Utilities.getPatternsPath();
	}

	public Queue<WebRatioElement> getPages() {
		return this.pages;
	}

	public List<Unit> getUnits() {
		return this.units;
	}

	public List<Link> getLinks() {
		return this.links;
	}

	public IEntity getEntitySelected() {
		return this.entitySelected;
	}

	public boolean canFinish() {
		return this.getSvAreasSelected().size() > 0 && this.patternCombo.getSelectionIndex() != -1;
	}

	@Override
	public void createControl(Composite parent) {
		try {
			File folder = new File(this.PATTERNS_DIR);
			if (!folder.exists())
				throw new NoPatternsFolderFoundException(this.PATTERNS_DIR);

			File[] listOfFiles = folder.listFiles();
			if (listOfFiles.length == 0)
				throw new NoPatternFileFoundException(this.PATTERNS_DIR);
			else
				PatternParser.checkPatternsIdAreUnique(listOfFiles);

			if (!Utilities.isPluginClosed()) {
				this.containerComposite = new Composite(parent, SWT.NULL);
				FormLayout thisLayout = new FormLayout();
				this.containerComposite.setLayout(thisLayout);
				this.containerComposite.layout();
				this.setControl(this.containerComposite);

				this.listaSiteViews = ProjectParameters.getWebModel().getSiteViewList();

				this.createLeftComposite();
				this.createRightComposite();

				this.entityCombo.select(0);
				this.entitySelectionListener();
				this.patternCombo.select(0);
				this.patternSelectionListener();
			}

		} catch (Exception e) {
			ExceptionHandler.handle(e);
		}
	}

	private void createLeftComposite() {
		// Creating left composite and its content
		this.leftComposite = new Composite(this.containerComposite, SWT.NONE);
		GridLayout leftCompositeLayout = new GridLayout(1, false);
		leftCompositeLayout.marginWidth = 20;
		leftCompositeLayout.verticalSpacing = 30;
		this.leftComposite.setLayout(leftCompositeLayout);

		FormData fData = new FormData();
		fData.top = new FormAttachment(0);
		fData.left = new FormAttachment(0);
		fData.right = new FormAttachment(40); // Locks on 40% of the view
		fData.bottom = new FormAttachment(100);
		this.leftComposite.setLayoutData(fData);

		// ---- Left composite groups and its content ----

		// * Entity *
		this.entityGroup = new Group(this.leftComposite, SWT.NONE);
		FillLayout entityGroupLayout = new FillLayout(SWT.VERTICAL);
		this.entityGroup.setLayout(entityGroupLayout);
		this.entityGroup.setText("Select Entity");
		this.entityGroup.setVisible(true);

		this.entityCombo = new Combo(this.entityGroup, SWT.NONE);
		this.entityCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				super.widgetSelected(evt);
				try {
					WizardPatternPage.this.entitySelectionListener();
				} catch (Exception e) {
					ExceptionHandler.handle(e);
				}
			}
		});

		// Obtengo la lista de entidades y relleno el Combo
		try {
			ProjectParameters.init();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		this.entityList = ProjectParameters.getDataModel().getAllEntityList();
		Collections.sort(this.entityList, new MyIEntityComparator());

		IMFElement imfe;
		Iterator<IEntity> iter = this.entityList.iterator();
		while (iter.hasNext()) {
			imfe = iter.next();
			this.entityCombo.add(Utilities.getAttribute(imfe, "name"));
		}

		// * Pattern *
		this.patternGroup = new Group(this.leftComposite, SWT.NONE);
		FillLayout patternGroupLayout = new FillLayout(SWT.VERTICAL);
		this.patternGroup.setLayout(patternGroupLayout);
		this.patternGroup.setText("Select Pattern");
		this.patternGroup.setVisible(true);

		this.patternCombo = new Combo(this.patternGroup, SWT.NONE);
		this.patternCombo.setEnabled(false);
		this.patternCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				try {
					WizardPatternPage.this.patternSelectionListener();
				} catch (Exception e) {
					ExceptionHandler.handle(e);
				}
			}
		});

		// Get patterns list and fill the Combo
		File folder = new File(this.PATTERNS_DIR);

		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(".xml"))
				this.patternFileList.add(listOfFiles[i].getName());
		}

		Collections.sort(this.patternFileList);
		for (String patternFile : this.patternFileList) {
			this.patternCombo.add(patternFile.replace(".xml", ""));
		}

		// * Sv/Area *
		this.svAreasGroup = new Group(this.leftComposite, SWT.NONE);
		this.svAreasGroup.setLayout(new FillLayout(SWT.HORIZONTAL));
		this.svAreasGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.svAreasGroup.setText("Select SiteViews/Areas");
		this.svAreasGroup.setVisible(true);

		this.svAreasTree = new Tree(this.svAreasGroup, SWT.MULTI | SWT.CHECK | SWT.BORDER);
		this.svAreasTree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) { // Si el motivo de la seleccion ha sido el check
				if (event.detail == SWT.CHECK) {
					WizardPatternPage.this.getWizard().getContainer().updateButtons();
				}
			}
		});
		this.buildSvAreasTree(this.svAreasTree);
	}

	private void createRightComposite() {
		// Inicializo el composite derecho donde van a ir los elementos
		// dinámicos (dependiendo el patrón seleccionado)
		this.rightComposite = new Composite(this.containerComposite, SWT.NONE);
		FillLayout rightCompositeLayout = new FillLayout(SWT.HORIZONTAL);
		rightCompositeLayout.marginHeight = 7;
		rightCompositeLayout.marginWidth = 20;
		this.rightComposite.setLayout(rightCompositeLayout);

		FormData fData = new FormData();
		fData.top = new FormAttachment(0);
		fData.left = new FormAttachment(this.leftComposite);
		fData.right = new FormAttachment(100);
		fData.bottom = new FormAttachment(100);
		this.rightComposite.setLayoutData(fData);
		this.tabFolder = new TabFolder(this.rightComposite, SWT.NONE);
	}

	private void patternSelectionListener() throws SAXException, IOException, ParserConfigurationException {
		// Actualizo el estado del botón Finish
		this.getWizard().getContainer().updateButtons();

		// elimino los elementos gráficos que se hayan generado por una
		// selección previa:
		for (TabItem tab : this.tabFolder.getItems()) {
			tab.dispose();
		}

		this.listCombosRelations.clear();
		String patternFileSelected = this.patternFileList.get(this.patternCombo.getSelectionIndex());
		this.relatedEntities = this.getRelationshipRoles(this.entitySelected);

		// Obtener elementos del XML (salvo las relaciones que se necesita que el usuario elija primero)
		this.xmlParser = new PatternParser(this.PATTERNS_DIR + patternFileSelected, this.entitySelected);
		this.xmlParser.parsePagesSection();
		this.xmlParser.parseOutsideUnitsSection();
		this.xmlParser.parseLinksSection();

		this.pages = this.xmlParser.getPages();
		this.units = this.xmlParser.getUnits();
		this.links = this.xmlParser.getLinks();

		// Recorro una vez primeramente para colocar el grupo Relations en primer lugar
		for (Unit unit : this.units) {

			if (unit instanceof CreateUnit || unit instanceof UpdateUnit) {
				TabItem tabItem = new TabItem(this.tabFolder, SWT.NONE);
				tabItem.setText("Relations");

				this.relationsComposite = new Composite(this.tabFolder, SWT.NONE);
				tabItem.setControl(this.relationsComposite);
				this.relationsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

				this.tableRelations = new Table(this.relationsComposite, SWT.CHECK | SWT.V_SCROLL);
				this.relationsComposite.addPaintListener(new PaintListener() {
					public void paintControl(PaintEvent evt) {
						WizardPatternPage.this.relationsCompositePaintControl(evt);
					}
				});

				this.addRelationRolesToTable(this.tableRelations, this.listCombosRelations);

				// Sólo va a haber una tabla para elegir las relaciones:
				break;
			}
		}

		// crear los grupos>tables>atributos
		for (Unit unit : this.units) {
			if (unit instanceof PowerIndexUnit || unit instanceof DataUnit) {
				// Se crea una table con los atributos de la entidad
				// Grupo
				TabItem tabItem = new TabItem(this.tabFolder, SWT.NONE);
				tabItem.setText(unit.getName());

				Composite composite = new Composite(this.tabFolder, SWT.NONE);
				tabItem.setControl(composite);
				GridLayout compLayout = new GridLayout();
				compLayout.numColumns = 1;
				composite.setLayout(compLayout);

				// Table
				final Table table = new Table(composite, SWT.CHECK | SWT.V_SCROLL);

				GridData gridDataIndex = new GridData(SWT.FILL, SWT.FILL, true, true);
				table.setLayoutData(gridDataIndex);

				// boton select all y deselect all
				Button buttonSelectPower = new Button(composite, SWT.PUSH);
				buttonSelectPower.setText("(Select/Deselect) All");
				GridData gridDataButtonSelectPower = new GridData(GridData.FILL, GridData.CENTER, false, false);
				gridDataButtonSelectPower.horizontalSpan = 1;
				buttonSelectPower.setLayoutData(gridDataButtonSelectPower);
				buttonSelectPower.setSelection(Boolean.FALSE);

				buttonSelectPower.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						if (null != table && null != table.getItems() && table.getItems().length > 0) {
							Boolean hayCheckeados = Boolean.FALSE;

							for (int i = 0; i < table.getItems().length; i++) {
								if (table.getItems()[i].getChecked()) {
									hayCheckeados = Boolean.TRUE;
								}
							}

							if (hayCheckeados) {
								// si hay elementos seleccionados: deselecciono all
								for (int i = 0; i < table.getItems().length; i++) {
									table.getItems()[i].setChecked(false);

								}
							} else {
								// si no hay elementos seleccionados: selecciono all
								for (int i = 0; i < table.getItems().length; i++) {
									table.getItems()[i].setChecked(true);
								}
							}
						}
					}
				});
				composite.setVisible(true);
				this.addAttributesToTable(table);

				if (unit instanceof PowerIndexUnit)
					((PowerIndexUnit) unit).setTable(table);

				else if (unit instanceof DataUnit)
					((DataUnit) unit).setTable(table);
			}
		}
		this.containerComposite.layout(true, true);
	}

	private void entitySelectionListener() throws SAXException, IOException, ParserConfigurationException {
		this.entitySelected = this.entityList.get(this.entityCombo.getSelectionIndex());

		this.listaAtributosEntidad = this.entitySelected.getAllAttributeList();
		Iterator<IAttribute> iteratorAtributos = this.listaAtributosEntidad.iterator();
		IAttribute atributo;
		while (iteratorAtributos.hasNext()) {
			atributo = iteratorAtributos.next();
			if (Utilities.getAttribute(atributo, "derivationQuery").equals("") && !Utilities.getAttribute(atributo, "key").equals("true")) {
				this.listaAtributosSinDerivados.add(atributo);
			}
		}

		if (this.patternCombo.getSelectionIndex() != -1) {
			this.patternSelectionListener();
		} else {
			this.patternCombo.setEnabled(true);
		}
	}

	private void buildSvAreasTree(Tree svAreasTree) {
		List<ISiteView> listaSiteViews = ProjectParameters.getWebModel().getSiteViewList();

		if (null != listaSiteViews) {
			for (ISiteView siteView : listaSiteViews) {
				if (null != siteView) {
					TreeItem treeItem = new TreeItem(svAreasTree, 0);

					treeItem.setText(Utilities.getDisplayName(siteView));
					treeItem.setData(siteView);

					this.buildAreasTree(treeItem, siteView.getAreaList());
				}
			}
		}
	}

	private void buildAreasTree(TreeItem parent, List<IArea> areaList) {
		if (null != areaList && areaList.size() > 0) {

			for (IArea area : areaList) {
				if (null != area) {

					TreeItem child = new TreeItem(parent, 0);
					child.setText(Utilities.getDisplayName(area));
					child.setData(area);

					this.buildAreasTree(child, area.getAreaList());
				}
			}
		}
	}

	private void addAttributesToTable(Table tabla) {
		Iterator<IAttribute> iteratorAttribute;
		// TODO pattern remodelation (update)
		/*
		 * if (tabla == this.tableRelFormUpdate) iteratorAttribute =
		 * this.listaAtributosSinDerivados.iterator(); else
		 */
		iteratorAttribute = this.listaAtributosEntidad.iterator();
		IAttribute atributo;
		while (iteratorAttribute.hasNext()) {
			atributo = iteratorAttribute.next();
			new TableItem(tabla, SWT.NONE).setText(Utilities.getAttribute(atributo, "name") + " (" + atributo.getFinalId() + ")");
		}
	}

	private void relationsCompositePaintControl(PaintEvent evt) {
		int size = (this.relationsComposite.getSize().x - 10) / 2;
		TableColumn[] columns = this.tableRelations.getColumns();
		for (int i = 0; i < 2; i++) {
			columns[i].setWidth(size);
		}
	}

	private void addRelationRolesToTable(Table table, List<CCombo> list) {
		for (int i = 0; i < 2; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setWidth(150);
		}
		this.relatedEntities = this.getRelationshipRoles(this.entitySelected);
		for (int i = 0; i < this.relatedEntities.size(); i++) {
			new TableItem(table, SWT.NONE);
		}
		TableItem[] items = table.getItems();

		for (int i = 0; i < items.length; i++) {
			TableEditor editor = new TableEditor(table);
			Text text = new Text(table, SWT.NONE);
			text.setText(Utilities.getAttribute(this.relatedEntities.get(i), "name"));
			editor.grabHorizontal = true;
			editor.setEditor(text, items[i], 0);
			editor = new TableEditor(table);
			CCombo combo = new CCombo(table, SWT.NONE);
			combo = this.addAtributesToCombo(combo, this.relatedEntities.get(i), editor);
			combo.select(0);
			// se a�ade posicion que ocupa el combo, sera igual a la del editor asociado a dicho combo
			combo.setData(new Integer(i));
			combo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					CCombo combo = (CCombo) evt.getSource();
					Table table = (Table) combo.getParent();

					table.getItems()[(Integer) combo.getData()].setChecked(true);
				}
			});
			list.add(combo);
			editor.grabHorizontal = true;
			editor.setEditor(combo, items[i], 1);
		}
		// table.pack();
	}

	private List<IRelationshipRole> getRelationshipRoles(IEntity entidad) {
		List<IRelationship> lista = entidad.getOutgoingRelationshipList();
		lista.addAll(entidad.getIncomingRelationshipList());
		Iterator<IRelationship> iteratorRelacion = lista.iterator();
		IRelationship relacion;
		IRelationshipRole role1, role2;
		String maxCard;
		List<IRelationshipRole> relatedEnt = new ArrayList<IRelationshipRole>();

		while (iteratorRelacion.hasNext()) {
			relacion = iteratorRelacion.next();
			if (relacion.getSourceEntity() == entidad) {
				role1 = relacion.getRelationshipRole1();
				maxCard = Utilities.getAttribute(role1, "maxCard");
				if (maxCard.equals("1")) {
					relatedEnt.add(role1);
				} else {
					role2 = relacion.getRelationshipRole2();
					maxCard = Utilities.getAttribute(role2, "maxCard");
					if (maxCard.equals("N")) {
						relatedEnt.add(role1);
					}
				}
			} else {
				role1 = relacion.getRelationshipRole2();
				maxCard = Utilities.getAttribute(role1, "maxCard");
				if (maxCard.equals("1")) {
					relatedEnt.add(role1);
				} else {
					role2 = relacion.getRelationshipRole1();
					maxCard = Utilities.getAttribute(role2, "maxCard");
					if (maxCard.equals("N")) {
						relatedEnt.add(role1);
					}
				}
			}
		}

		return relatedEnt;
	}

	private CCombo addAtributesToCombo(CCombo combo, IRelationshipRole role, TableEditor editor) {
		IEntity entidad;
		IRelationship relation = (IRelationship) role.getParentElement();
		if (relation.getTargetEntity() == this.entitySelected) {
			entidad = relation.getSourceEntity();
		} else
			entidad = relation.getTargetEntity();

		List<IAttribute> atributos = entidad.getAllAttributeList();

		String texto;
		for (IAttribute atributo : atributos) {
			texto = Utilities.getAttribute(atributo, "name") + " (" + Utilities.getAttribute(role, "name") + ")";
			combo.add(Utilities.getAttribute(atributo, "name"));
			this.relationAttributes.put(texto, atributo);
		}

		return combo;
	}

	public List<IMFElement> getSvAreasSelected() {

		List<IMFElement> list = new ArrayList<IMFElement>();
		TreeItem[] svAreasArray = this.svAreasTree.getItems();
		TreeItem treeItem;
		if (null != svAreasArray && svAreasArray.length > 0) {
			for (int i = 0; i < svAreasArray.length; i++) {
				treeItem = svAreasArray[i];
				if (treeItem.getChecked()) {
					list.add((IMFElement) treeItem.getData());
				}
				this.getAreas(treeItem, list);
			}
		}

		return list;
	}

	private void getAreas(TreeItem parent, List<IMFElement> list) {

		TreeItem[] childsArray = parent.getItems();
		if (null != childsArray && childsArray.length > 0) {

			TreeItem child;
			for (int i = 0; i < childsArray.length; i++) {
				child = childsArray[i];
				if (child.getChecked()) {
					list.add((IMFElement) child.getData());
				}
				this.getAreas(child, list);
			}
		}
	}

	public Map<IRelationshipRole, IAttribute> getRelationshipsSelected() {
		Map<IRelationshipRole, IAttribute> mapaRelaciones = new HashMap<IRelationshipRole, IAttribute>();
		String key;
		try {
			for (int i = 0; i < this.listCombosRelations.size(); i++) {
				if (this.tableRelations.getItems()[i].getChecked()) {
					// this.tableIndexCreate.getItems()[i].checked
					key = this.listCombosRelations.get(i).getItem(this.listCombosRelations.get(i).getSelectionIndex()) + " ("
							+ Utilities.getAttribute(this.relatedEntities.get(i), "name") + ")";
					mapaRelaciones.put(this.relatedEntities.get(i), this.relationAttributes.get(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapaRelaciones;
	}

	// Ejecutado desde WizardCRUD para parsear las relaciones
	public void finalizePage() throws SAXException, IOException {
		// Actualizo los cambios realizados en las listas del xmlParser
		this.xmlParser.setPages(this.pages);
		this.xmlParser.setUnits(this.units);
		this.xmlParser.setLinks(this.links);

		// Ahora parseo las relaciones, y las nuevas unidades se añaden a las anteriores que han sido cambiadas.
		this.xmlParser.parseRelations(this.getRelationshipsSelected().keySet());

		// Me traigo los elementos con todos los cambios nuevos.
		this.pages = this.xmlParser.getPages();
		this.units = this.xmlParser.getUnits();
		this.links = this.xmlParser.getLinks();

		// Need to extract the table attributes checked before the widget disposes
		for (Unit unit : this.units) {
			if (unit instanceof PowerIndexUnit)
				((PowerIndexUnit) unit).extractTableAttributes();

			else if (unit instanceof DataUnit)
				((DataUnit) unit).extractTableAttributes();
		}
	}
}
