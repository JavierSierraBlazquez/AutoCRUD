package org.homeria.webratioassistant.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.homeria.webratioassistant.plugin.MyIEntityComparator;
import org.homeria.webratioassistant.plugin.ObjStViewArea;
import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.plugin.Utilities;
import org.homeria.webratioassistant.temporal.ContentUnit;
import org.homeria.webratioassistant.temporal.Link;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.ISiteView;

public class WizardPatternPage extends WizardPage {
	// TODO: localización de la carpeta patterns
	private final String PATTERNS_DIR = "patterns";
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

	private Tree arbolSvAreas;

	private List<String> patternFileList;
	private List<ISiteView> listaSiteViews;
	private List<IEntity> entityList;
	private List<IAttribute> listaAtributosEntidad;
	private List<IAttribute> listaAtributosSinDerivados;
	private List<ContentUnit> contentUnits;
	private List<Link> links;
	private List<Group> rightGroupsList;

	private IEntity entitySelected;
	private String patternFileSelected;

	protected WizardPatternPage() {
		super("WizardPattern");

		this.patternFileList = new ArrayList<String>();
		this.listaSiteViews = new ArrayList<ISiteView>();
		this.entityList = new ArrayList<IEntity>();
		this.listaAtributosEntidad = new ArrayList<IAttribute>();
		this.listaAtributosSinDerivados = new ArrayList<IAttribute>();
		this.rightGroupsList = new ArrayList<Group>();
	}

	@Override
	public void createControl(Composite parent) {
		this.containerComposite = new Composite(parent, SWT.NULL);
		FormLayout thisLayout = new FormLayout();
		this.containerComposite.setLayout(thisLayout);
		this.containerComposite.layout();
		setControl(this.containerComposite);

		// declaracionEstructuras(entidad);
		// this.initRelationShips();

		this.listaSiteViews = ProjectParameters.getWebModel().getSiteViewList();

		crearCompositeIzquierdo();

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
				entitySelectionListener();
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
				patternSelectionListener();
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
		/*
		 * this.arbolSvAreas.addListener(SWT.Selection, new Listener() { public
		 * void handleEvent(Event event) { // Si el motivo de la seleccion ha
		 * sido el check if (event.detail == SWT.CHECK) { TreeItem item =
		 * (TreeItem) event.item; boolean checked = item.getChecked(); if
		 * (!checked) { checkItems(item, checked); }
		 * checkPath(item.getParentItem(), checked, false);
		 * getWizard().getContainer().updateButtons(); } } });
		 */
		inicializarListaYarbol();
	}

	private void patternSelectionListener() {

		// elimino los elementos gráficos que se hayan generado por una
		// selección previa:
		for (Group group : this.rightGroupsList)
			group.dispose();

		this.patternFileSelected = this.patternFileList.get(this.patternCombo.getSelectionIndex());

		// Obtener elementos del XML
		this.contentUnits = new ArrayList<ContentUnit>();
		this.links = new ArrayList<Link>();
		Utilities.parseXML(this.PATTERNS_DIR + "/" + this.patternFileSelected, this.contentUnits, this.links);

		// crear los grupos>tables>atributos
		for (ContentUnit contentUnit : this.contentUnits) {
			// Grupo
			Group group = new Group(this.innerRightComposite, SWT.NONE);
			GridLayout groupLayout = new GridLayout();
			groupLayout.numColumns = 1;
			group.setLayout(groupLayout);
			group.setText(contentUnit.getName());

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
							// tableIndexRead.deselectAll();
							for (int i = 0; i < table.getItems().length; i++) {
								table.getItems()[i].setChecked(false);

							}
						} else {
							// si no hay elementos seleccionados: selecciono all
							// tableIndexRead.selectAll();
							for (int i = 0; i < table.getItems().length; i++) {
								table.getItems()[i].setChecked(true);
							}
						}
					}
				}
			});
			group.setVisible(true);
			this.addAttributesToTable(table);
			contentUnit.setTable(table);
		}

		this.containerComposite.layout(true, true);
	}

	private void entitySelectionListener() {
		this.patternCombo.setEnabled(true);
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
	}

	private void inicializarListaYarbol() {
		List<ISiteView> listaSiteViewsPreviaPage = ProjectParameters.getWebModel().getSiteViewList();

		// Inicializa elementos del arbol para volver a version de siteView-
		// areas creados
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

					if (null != siteView.getAreaList() && siteView.getAreaList().size() > 0) {
						montarArbolAreas(itemSiteView, objStView, siteView.getAreaList());
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
	 *            ---> cone esto voy a ir recorriendo la lista de Areas de cada
	 *            SiteView, y areas de areas..
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

					this.arbolSvAreas.select(itemHijo);

					montarArbolAreas(itemHijo, objArea1, area.getAreaList());
				}
			}

		}
	}

	private void addAttributesToTable(Table tabla) {
		Iterator<IAttribute> iteratorAttribute;
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

}
