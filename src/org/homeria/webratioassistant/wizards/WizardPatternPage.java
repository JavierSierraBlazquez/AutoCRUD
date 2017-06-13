package org.homeria.webratioassistant.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.homeria.webratioassistant.parser.PatternParser;
import org.homeria.webratioassistant.plugin.MyIEntityComparator;
import org.homeria.webratioassistant.plugin.ObjStViewArea;
import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.plugin.Utilities;

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
	private Composite innerRightComposite;
	private ScrolledComposite scrolledComposite;

	private Group entityGroup;
	private Group patternGroup;
	private Group svAreaGroup;
	private Group relationsGroup;

	private Table tableRelations;

	private Tree arbolSvAreas;

	private List<CCombo> listCombosRelations;

	private List<String> patternFileList;
	private List<ISiteView> listaSiteViews;
	private List<IAttribute> listaAtributosEntidad;
	private List<IAttribute> listaAtributosSinDerivados;
	private List<IEntity> entityList;
	private List<IRelationshipRole> relatedEntities;
	private List<Group> rightGroupsList;

	private Queue<WebRatioElement> pages;
	private List<Unit> units;
	private List<Link> links;

	private IEntity entitySelected;

	PatternParser xmlParser;
	private Map<String, IAttribute> atributosRelacion;

	public WizardPatternPage() {
		super("WizardPattern");

		this.patternFileList = new ArrayList<String>();
		this.listaSiteViews = new ArrayList<ISiteView>();
		this.entityList = new ArrayList<IEntity>();
		this.listaAtributosEntidad = new ArrayList<IAttribute>();
		this.listaAtributosSinDerivados = new ArrayList<IAttribute>();
		this.rightGroupsList = new ArrayList<Group>();
		this.listCombosRelations = new ArrayList<CCombo>();
		this.atributosRelacion = new HashMap<String, IAttribute>();

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
		return this.getSvAreasChecked().size() > 0 && this.patternCombo.getSelectionIndex() != -1;
	}

	@Override
	public void createControl(Composite parent) {
		this.containerComposite = new Composite(parent, SWT.NULL);
		FormLayout thisLayout = new FormLayout();
		this.containerComposite.setLayout(thisLayout);
		this.containerComposite.layout();
		this.setControl(this.containerComposite);

		this.listaSiteViews = ProjectParameters.getWebModel().getSiteViewList();

		this.crearCompositeIzquierdo();

		// Inicializo el composite derecho donde van a ir los elementos
		// dinámicos (dependiendo el patrón seleccionado)
		this.rightComposite = new Composite(this.containerComposite, SWT.NONE);
		FillLayout rightCompositeLayout = new FillLayout(SWT.HORIZONTAL);
		this.rightComposite.setLayout(rightCompositeLayout);

		FormData fData = new FormData();
		fData.top = new FormAttachment(0);
		fData.left = new FormAttachment(this.leftComposite);
		fData.right = new FormAttachment(100);
		fData.bottom = new FormAttachment(100);
		this.rightComposite.setLayoutData(fData);

		this.scrolledComposite = new ScrolledComposite(this.rightComposite, SWT.H_SCROLL);
		this.scrolledComposite.setExpandVertical(true);
		this.scrolledComposite.setExpandHorizontal(true);
		this.scrolledComposite.setAlwaysShowScrollBars(true);
		this.scrolledComposite.setMinWidth(900);

		this.innerRightComposite = new Composite(this.scrolledComposite, SWT.NONE);
		this.innerRightComposite.setLayout(new FillLayout());

		this.scrolledComposite.setContent(this.innerRightComposite);
		try {
			this.dispose();
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void crearCompositeIzquierdo() {
		// Creando el composite izquierdo y su contenido
		this.leftComposite = new Composite(this.containerComposite, SWT.NONE);
		FillLayout leftCompositeLayout = new FillLayout(SWT.VERTICAL);
		leftCompositeLayout.marginHeight = 5;
		leftCompositeLayout.marginWidth = 5;
		leftCompositeLayout.spacing = 10;
		this.leftComposite.setLayout(leftCompositeLayout);

		FormData fData = new FormData();
		fData.top = new FormAttachment(0);
		fData.left = new FormAttachment(0);
		fData.right = new FormAttachment(20); // Locks on 20% of the view
		fData.bottom = new FormAttachment(100);
		this.leftComposite.setLayoutData(fData);

		// ---- Grupos del composite izquierdo y su contenido ----

		// * Entidad *
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
				WizardPatternPage.this.entitySelectionListener();
			}
		});

		// Obtengo la lista de entidades y relleno el Combo
		try {
			ProjectParameters.init();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		this.entityList = ProjectParameters.getDataModel().getEntityList();
		this.entityList = ProjectParameters.getDataModel().getAllEntityList();
		Collections.sort(this.entityList, new MyIEntityComparator());

		IMFElement imfe;
		Iterator<IEntity> iter = this.entityList.iterator();
		while (iter.hasNext()) {
			imfe = iter.next();
			this.entityCombo.add(Utilities.getAttribute(imfe, "name"));
		}

		// * Patrón *
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
				WizardPatternPage.this.patternSelectionListener();
			}
		});

		// Obtengo la lista de patrones y relleno el Combo
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
		this.svAreaGroup = new Group(this.leftComposite, SWT.NONE);
		FillLayout svAreaGroupLayout = new FillLayout(SWT.HORIZONTAL);
		this.svAreaGroup.setLayout(svAreaGroupLayout);
		this.svAreaGroup.setText("SiteViews-Areas");
		this.svAreaGroup.setVisible(true);

		this.arbolSvAreas = new Tree(this.svAreaGroup, SWT.MULTI | SWT.CHECK | SWT.BORDER);

		this.arbolSvAreas.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) { // Si el motivo de la seleccion ha sido el check
				if (event.detail == SWT.CHECK) {
					WizardPatternPage.this.getWizard().getContainer().updateButtons();
				}
			}
		});
		this.inicializarListaYarbol();
	}

	private void patternSelectionListener() {
		// Actualizo el estado del botón Finish
		this.getWizard().getContainer().updateButtons();

		// elimino los elementos gráficos que se hayan generado por una
		// selección previa:
		for (Group group : this.rightGroupsList)
			group.dispose();

		this.listCombosRelations.clear();
		String patternFileSelected = this.patternFileList.get(this.patternCombo.getSelectionIndex());
		this.relatedEntities = this.getRelationshipRoles(this.entitySelected);

		// Obtener elementos del XML (salvo las relaciones que se necesita que el usuario elija primero)
		this.xmlParser = new PatternParser(this.PATTERNS_DIR + patternFileSelected, this.entitySelected);
		this.xmlParser.parsePagesSection();
		this.xmlParser.parseOpUnitsSection();
		this.xmlParser.parseLinksSection();

		this.pages = this.xmlParser.getPages();
		this.units = this.xmlParser.getUnits();
		this.links = this.xmlParser.getLinks();

		// Recorro una vez primeramente para colocar el grupo Relations en primer lugar
		for (Unit unit : this.units) {

			if (unit instanceof CreateUnit || unit instanceof UpdateUnit) {

				this.relationsGroup = new Group(this.innerRightComposite, SWT.NONE);
				FillLayout relationsGroupLayout = new FillLayout(SWT.HORIZONTAL);
				relationsGroupLayout.marginHeight = 5;
				this.relationsGroup.setLayout(relationsGroupLayout);
				this.relationsGroup.setText("Relations");

				this.rightGroupsList.add(this.relationsGroup);

				this.tableRelations = new Table(this.relationsGroup, SWT.CHECK | SWT.V_SCROLL);
				this.relationsGroup.addPaintListener(new PaintListener() {
					public void paintControl(PaintEvent evt) {
						WizardPatternPage.this.relationsGroupPaintControl(evt);
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
				Group group = new Group(this.innerRightComposite, SWT.NONE);
				GridLayout groupLayout = new GridLayout();
				groupLayout.numColumns = 1;
				group.setLayout(groupLayout);
				group.setText(unit.getName());

				this.rightGroupsList.add(group);

				// Table
				final Table table = new Table(group, SWT.CHECK | SWT.V_SCROLL);

				GridData gridDataIndex = new GridData(SWT.FILL, SWT.FILL, true, true);
				table.setLayoutData(gridDataIndex);

				// boton select all y deselect all
				Button buttonSelectPower = new Button(group, SWT.PUSH);
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
				group.setVisible(true);
				this.addAttributesToTable(table);

				if (unit instanceof PowerIndexUnit)
					((PowerIndexUnit) unit).setTable(table);
				else if (unit instanceof DataUnit)
					((DataUnit) unit).setTable(table);
			}
		}
		this.containerComposite.layout(true, true);
	}

	private void entitySelectionListener() {
		this.entitySelected = this.entityList.get(this.entityCombo.getSelectionIndex());

		// De aqui se obtienen los atributos de la entidad
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

	private void inicializarListaYarbol() {
		List<ISiteView> listaSiteViewsPreviaPage = ProjectParameters.getWebModel().getSiteViewList();

		// Inicializa elementos del arbol para volver a version de siteView-areas creados
		List<ObjStViewArea> listaSiteViewArea = new ArrayList();

		this.arbolSvAreas.removeAll();
		this.arbolSvAreas.clearAll(Boolean.TRUE);

		if (null != listaSiteViewsPreviaPage && listaSiteViewsPreviaPage.size() > 0) {
			for (Iterator iterator = listaSiteViewsPreviaPage.iterator(); iterator.hasNext();) {
				ISiteView siteView = (ISiteView) iterator.next();
				if (null != siteView) {
					ObjStViewArea objStView = new ObjStViewArea();
					objStView.setNombre(Utilities.getAttribute(siteView, "name") + " (" + siteView.getFinalId() + ")");
					objStView.setTipo("STVIEW");
					listaSiteViewArea.add(objStView);

					TreeItem itemSiteView = new TreeItem(this.arbolSvAreas, 0);

					itemSiteView.setText(Utilities.getAttribute(siteView, "name") + " (" + siteView.getFinalId() + ")");
					itemSiteView.setData(objStView);

					if (null != siteView.getAreaList() && siteView.getAreaList().size() > 0) {
						this.montarArbolAreas(itemSiteView, objStView, siteView.getAreaList());
					}
				}
			}
		}

		this.arbolSvAreas.redraw();
		ProjectParameters.setlistaSiteViewArea(listaSiteViewArea);
	}

	/**
	 * Para recorrer una lista de Areas y formar los nodos del padre
	 * 
	 * Nombre: montarArbol Funcion:
	 * 
	 * @param objPadre
	 *            --> con esto voy ir aumentando el arbol
	 * @param listaAreasPadre
	 *            ---> cone esto voy a ir recorriendo la lista de Areas de cada SiteView, y areas de areas..
	 */
	private void montarArbolAreas(TreeItem itemPadreArbol, ObjStViewArea objPadreArbol, List<IArea> listaAreasPadreRecorrer) {

		List listaAreaHijo = null;
		if (null != listaAreasPadreRecorrer && listaAreasPadreRecorrer.size() > 0) {
			listaAreaHijo = new ArrayList();
			objPadreArbol.setListHijos(listaAreaHijo);
			for (Iterator iterator = listaAreasPadreRecorrer.iterator(); iterator.hasNext();) {
				IArea area = (IArea) iterator.next();

				if (null != area) {
					ObjStViewArea objArea1 = new ObjStViewArea();
					objArea1.setNombre(Utilities.getAttribute(area, "name") + " (" + area.getFinalId() + ")");
					objArea1.setTipo("AREA");
					listaAreaHijo.add(objArea1);

					TreeItem itemHijo = new TreeItem(itemPadreArbol, 0);
					itemHijo.setText(Utilities.getAttribute(area, "name") + " (" + area.getFinalId() + ")");
					itemHijo.setData(objArea1);

					this.arbolSvAreas.select(itemHijo);

					this.montarArbolAreas(itemHijo, objArea1, area.getAreaList());
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

	private void relationsGroupPaintControl(PaintEvent evt) {
		int tamanio = (this.relationsGroup.getSize().x - 10) / 2;
		TableColumn[] columns = this.tableRelations.getColumns();
		for (int i = 0; i < 2; i++) {
			columns[i].setWidth(tamanio);
		}
	}

	private void addRelationRolesToTable(Table tabla, List<CCombo> list) {
		for (int i = 0; i < 2; i++) {
			TableColumn column = new TableColumn(tabla, SWT.NONE);
			column.setWidth(100);
		}
		this.relatedEntities = this.getRelationshipRoles(this.entitySelected);
		for (int i = 0; i < this.relatedEntities.size(); i++) {
			new TableItem(tabla, SWT.NONE);
		}
		TableItem[] items = tabla.getItems();

		for (int i = 0; i < items.length; i++) {
			TableEditor editor = new TableEditor(tabla);
			Text text = new Text(tabla, SWT.NONE);
			text.setText(Utilities.getAttribute(this.relatedEntities.get(i), "name"));
			editor.grabHorizontal = true;
			editor.setEditor(text, items[i], 0);
			editor = new TableEditor(tabla);
			CCombo combo = new CCombo(tabla, SWT.NONE);
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
			this.atributosRelacion.put(texto, atributo);
		}

		return combo;
	}

	public List<IMFElement> getSvAreasChecked() {
		// obtener solamente los checkeados

		TreeItem[] arrSiteViewSelected = this.arbolSvAreas.getItems();

		List<IMFElement> lista = new ArrayList<IMFElement>();
		if (null != arrSiteViewSelected && arrSiteViewSelected.length > 0) {
			for (int i = 0; i < arrSiteViewSelected.length; i++) {
				if (arrSiteViewSelected[i].getChecked()) {
					for (int j = 0; j < this.listaSiteViews.size(); j++) {
						ISiteView siteView = this.listaSiteViews.get(j);

						String valorCompleto = Utilities.getAttribute(siteView, "name") + " (" + siteView.getFinalId() + ")";
						String valorNameMasEspacio = Utilities.getAttribute(siteView, "name") + " ";
						if (arrSiteViewSelected[i].getText().compareTo(valorCompleto) == 0
								|| valorNameMasEspacio.compareTo(arrSiteViewSelected[i].getText() + " ") == 0) {
							lista.add(this.listaSiteViews.get(j));
						}
					}
				}
			}
		}
		lista.addAll(this.getAreas());

		return lista;
	}

	private List<IArea> getAreas() {

		List<IArea> lista = new ArrayList<IArea>();
		Collection<TreeItem> retColItemSelEhijos = new ArrayList<TreeItem>();

		this.obtenerHijosCheckeados(retColItemSelEhijos, this.arbolSvAreas.getItems());

		if (null != retColItemSelEhijos) {
			for (Iterator iterator = retColItemSelEhijos.iterator(); iterator.hasNext();) {
				TreeItem treeItem = (TreeItem) iterator.next();

				IArea area = this.buscarElementoArea(((ObjStViewArea) treeItem.getData()).getNombre());
				if (null != area) {
					lista.add(area);
				}
			}
		}
		return lista;

	}

	public IArea buscarElementoAreaRecursivo(List<IArea> listArea, String nombre) {

		if (null != listArea && listArea.size() > 0) {
			for (Iterator iterator = listArea.iterator(); iterator.hasNext();) {
				IArea area = (IArea) iterator.next();

				String valorCompleto = Utilities.getAttribute(area, "name") + " (" + area.getFinalId() + ")";
				// 2 (sv11)
				if (nombre.compareTo(valorCompleto) == 0 || valorCompleto.startsWith(nombre + " ")) {
					// if(nombre.contains(valor)){
					return area;
				} else {
					if (null != area.getAreaList() && area.getAreaList().size() > 0) {
						IArea areabuscar = this.buscarElementoAreaRecursivo(area.getAreaList(), nombre);
						if (null != areabuscar) {
							return areabuscar;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * Nombre: buscarElementoArea Funcion:
	 * 
	 * @param nombre
	 * @return
	 */

	public IArea buscarElementoArea(String nombre) {

		IArea areaEnc = null;
		for (int j = 0; j < this.listaSiteViews.size(); j++) {
			ISiteView siteView = this.listaSiteViews.get(j);

			if (null != siteView.getAreaList() && siteView.getAreaList().size() > 0) {

				areaEnc = this.buscarElementoAreaRecursivo(siteView.getAreaList(), nombre);
				if (null != areaEnc) {
					return areaEnc;
				}
			}

		}
		return null;
	}

	/**
	 * 
	 * Nombre: obtenerHijosCheckeados Funcion:
	 * 
	 * @param retColItemSelEhijos
	 * @param arrItemRecorrer
	 */
	private void obtenerHijosCheckeados(Collection<TreeItem> retColItemSelEhijos, TreeItem[] arrItemRecorrer) {

		if (null != arrItemRecorrer && arrItemRecorrer.length > 0) {

			for (int i = 0; i < arrItemRecorrer.length; i++) {
				// primero selecciona que el nodo este checkeado
				if (null != arrItemRecorrer[i]) {
					if (null != arrItemRecorrer[i].getData() && ((ObjStViewArea) arrItemRecorrer[i].getData()).getTipo().equals("STVIEW")
							&& null != arrItemRecorrer[i].getItems()) {
						this.obtenerHijosCheckeados(retColItemSelEhijos, arrItemRecorrer[i].getItems());
					} else {

						// segundo comprueba si es de tipo area y no tiene hijos
						if (null != arrItemRecorrer[i].getData() && ((ObjStViewArea) arrItemRecorrer[i].getData()).getTipo().equals("AREA")
								&& null == arrItemRecorrer[i].getItems() && arrItemRecorrer[i].getChecked()) {

							retColItemSelEhijos.add(arrItemRecorrer[i]);

						}

						// tercero comprueba si es de tipo area y ninguno de sus
						// hijos siguientes tiene checkeado
						if (null != arrItemRecorrer[i].getData() && ((ObjStViewArea) arrItemRecorrer[i].getData()).getTipo().equals("AREA")
								&& null != arrItemRecorrer[i].getItems() && arrItemRecorrer[i].getChecked()) {

							int contador = 0;
							for (int j = 0; j < arrItemRecorrer[i].getItems().length; j++) {
								if (null != arrItemRecorrer[i].getItems()[j] && arrItemRecorrer[i].getItems()[j].getChecked()) {
									contador++;
									this.obtenerHijosCheckeados(retColItemSelEhijos, arrItemRecorrer[i].getItems());
									break;
								}
							}

							if (contador == 0) {
								retColItemSelEhijos.add(arrItemRecorrer[i]);
							}

						}
					}

				}

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
					mapaRelaciones.put(this.relatedEntities.get(i), this.atributosRelacion.get(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapaRelaciones;
	}

	// Ejecutado desde WizardCRUD para parsear las relaciones
	public void finalizePage() {
		// Actualizo los cambios realizados en las listas del xmlParser
		this.xmlParser.setPages(this.pages);
		this.xmlParser.setUnits(this.units);
		this.xmlParser.setLinks(this.links);

		// Ahora parseo las relaciones, y las nuevas unidades se añaden a las anteriores que han sido cambiadas.
		this.xmlParser.parseRelationsSection(this.getRelationshipsSelected().keySet());

		// Me traigo los elementos con todos los cambios nuevos.
		this.pages = this.xmlParser.getPages();
		this.units = this.xmlParser.getUnits();
		this.links = this.xmlParser.getLinks();
	}
}
