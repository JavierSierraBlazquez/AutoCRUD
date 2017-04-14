/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */

package org.homeria.webratioassistant.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.homeria.webratioassistant.plugin.ObjStViewArea;
import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISiteView;

/**
 * @author Carlos Aguado Fuentes
 * @class WizardCRUDPage
 */
/**
 * WizardCRUDPage: Clase que genera los elementos visuales que se muestran en la
 * p�gina que permite seleccionar las opciones para generar el CRUD
 */
public class WizardCRUDPage extends WizardPage {
	private Map<String, IAttribute> atributosRelacion;
	private List<TableItem> checkDataRead;
	private List<TableItem> checkIndexDelete;
	private List<TableItem> checkIndexRead;
	private List<TableItem> checkIndexUpdate;
	private ArrayList<CCombo> checkOpcionesDelete;
	private List<CCombo> checkOpcionesRead;
	private ArrayList<CCombo> checkOpcionesUpdate;
	private List<TableItem> checkShowUpdate;

	private IEntity entidad = null;
	// private ArrayList<IEntity> entidadesRelacionadas;
	private ArrayList<IRelationshipRole> entidadesRelacionadas;

	private Composite containerComposite;
	private Composite leftComposite;
	private Composite rightComposite;
	private Composite allComposite;
	private Composite crudComposite;
	private Composite allLeftComposite;
	private Composite allRightComposite;

	private Group operationsGroup;
	private Group sPageOpGroup;
	private Group mPageOpGroup;
	private Group svAreaGroup;
	private Group pIndexAllGroup;
	private Group pIndexReadGroup;
	private Group pIndexUpdateGroup;
	private Group pIndexDeleteGroup;
	private Group relationsAllGroup;
	private Group relationsCreateGroup;
	private Group relationsFromGroup;
	private Group createGroup;
	private Group readGroup;
	private Group updateGroup;
	private Group deleteGroup;

	private Button allCheck;
	private Button cCheck;
	private Button rCheck;
	private Button uCheck;
	private Button dCheck;

	private List<IAttribute> listaAtributosEntidad;
	private List<IAttribute> listaAtributosSinDerivados;
	private List<CCombo> listaCombosCreate;
	private List<CCombo> listaCombosUpdate;
	private List<ISiteView> listaSiteViews;
	private WizardSelectEntityPage pageSelectEntity;
	private TabFolder tabFolder1;
	private TabItem tabItem1;
	private TabItem tabItem2;
	private TabItem tabItem3;
	private TabItem tabItem4;
	private Table tableDataRead;
	private Table tableDataAllInOne;
	private Table tableDetailAllInOne;
	private Table tableRelationsCreate;
	private Table tableIndexDelete;
	private Table tableIndexRead;
	private Table tableRelFormUpdate;
	private Table tableOpcionesDelete;
	private Table tableOpcionesRead;
	private Table tableOpcionesUpdate;
	private Table tableIndexUpdate;

	private Tree arbolSvAreas;

	private TabItem tabItem0;
	private Composite composite5;
	private Group group11;
	private Table tableIndexAllInOne;
	private Table tableRelationsAll;
	private List<TableItem> checkFormAllInOne;
	private List<CCombo> listaCombosAllInOne;
	private Group dataUnitGroup;
	private List<TableItem> checkDataAllInOne;

	/**
	 * usado en WizardCRUD.doFinish() para saber qué patrones se deben generar
	 */
	private List<Utilities.Operations> operationsChecked;
	private Group detailAllGroup;
	private Group detailReadGroup;
	private List<TableItem> attDetailListAllInOne;

	public WizardCRUDPage(IEntity entity) {
		super("wizardCRUDPage");
		setTitle("Webratio CRUD");
		setDescription("Configure options.");

		declaracionEstructuras(entity);
		this.crudComposite = null;
	}

	private void declaracionEstructuras(IEntity entity) {
		this.entidad = entity;
		this.checkIndexRead = new ArrayList<TableItem>();
		this.checkIndexUpdate = new ArrayList<TableItem>();
		this.checkShowUpdate = new ArrayList<TableItem>();
		this.checkIndexDelete = new ArrayList<TableItem>();
		this.checkDataRead = new ArrayList<TableItem>();
		this.atributosRelacion = new HashMap<String, IAttribute>();
		this.listaCombosCreate = new ArrayList<CCombo>();
		this.listaCombosUpdate = new ArrayList<CCombo>();
		this.checkOpcionesRead = new ArrayList<CCombo>();
		this.checkOpcionesUpdate = new ArrayList<CCombo>();
		this.checkOpcionesDelete = new ArrayList<CCombo>();
		this.listaAtributosEntidad = new ArrayList<IAttribute>();
		this.listaAtributosSinDerivados = new ArrayList<IAttribute>();
		this.checkFormAllInOne = new ArrayList<TableItem>();
		this.checkDataAllInOne = new ArrayList<TableItem>();
		this.attDetailListAllInOne = new ArrayList<TableItem>();
		this.listaCombosAllInOne = new ArrayList<CCombo>();
		this.operationsChecked = new ArrayList<Utilities.Operations>();
	}

	private void crearUI() {
		this.crearCompositeIzquierdo();
		this.crearCompositeDerecho();

		// Se crean al seleccionar los checks
		// this.crearAllInOneUI();
		// this.crearCrudUI();

		this.containerComposite.layout();
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
		leftComposite.setLayoutData(fData);

		// Grupos del composite izquierdo y su contenido
		this.operationsGroup = new Group(this.leftComposite, SWT.NONE);
		FillLayout operationsGroupLayout = new FillLayout(SWT.VERTICAL);
		this.operationsGroup.setLayout(operationsGroupLayout);
		this.operationsGroup.setText("Operations");
		this.operationsGroup.setVisible(true);

		this.sPageOpGroup = new Group(this.operationsGroup, SWT.NONE);
		FillLayout sPageOpGroupLayout = new FillLayout(SWT.HORIZONTAL);
		this.sPageOpGroup.setLayout(sPageOpGroupLayout);
		this.sPageOpGroup.setText("   Single Page");
		this.sPageOpGroup.setVisible(true);

		this.allCheck = new Button(this.sPageOpGroup, SWT.CHECK);
		this.allCheck.setText("All in one");
		this.allCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				allCheckSelectionAction();
			};
		});

		this.mPageOpGroup = new Group(this.operationsGroup, SWT.NONE);
		FillLayout mPageOpGroupLayout = new FillLayout(SWT.HORIZONTAL);
		this.mPageOpGroup.setLayout(mPageOpGroupLayout);
		this.mPageOpGroup.setText("   Multiple Pages");
		this.mPageOpGroup.setVisible(true);

		this.cCheck = new Button(this.mPageOpGroup, SWT.CHECK);
		this.cCheck.setText("C");
		this.cCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				cCheckSelectionAction();
			}
		});

		this.rCheck = new Button(this.mPageOpGroup, SWT.CHECK);
		this.rCheck.setText("R");
		this.rCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				rCheckSelectionAction();
			}
		});

		this.uCheck = new Button(this.mPageOpGroup, SWT.CHECK);
		this.uCheck.setText("U");
		this.uCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				uCheckSelectionAction();
			}
		});

		this.dCheck = new Button(this.mPageOpGroup, SWT.CHECK);
		this.dCheck.setText("D");
		this.dCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				dCheckSelectionAction();
			}
		});

		this.svAreaGroup = new Group(this.leftComposite, SWT.NONE);
		FillLayout svAreaGroupLayout = new FillLayout(SWT.HORIZONTAL);
		this.svAreaGroup.setLayout(svAreaGroupLayout);
		this.svAreaGroup.setText("SiteViews-Areas");
		this.svAreaGroup.setVisible(true);

		this.arbolSvAreas = new Tree(this.svAreaGroup, SWT.MULTI | SWT.CHECK | SWT.BORDER);
		arbolSvAreas.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// S el motivo de la seleccion ha sido el check
				if (event.detail == SWT.CHECK) {
					TreeItem item = (TreeItem) event.item;
					boolean checked = item.getChecked();
					if (!checked) {
						checkItems(item, checked);
					}
					checkPath(item.getParentItem(), checked, false);
					getWizard().getContainer().updateButtons();
				}
			}
		});
		inicializarListaYarbol();
	}

	private void crearCompositeDerecho() {
		// Creando el composite derecho y su contenido
		this.rightComposite = new Composite(this.containerComposite, SWT.NONE);
		FillLayout rightCompositeLayout = new FillLayout(SWT.HORIZONTAL);
		this.rightComposite.setLayout(rightCompositeLayout);

		FormData fData = new FormData();
		fData.top = new FormAttachment(0);
		fData.left = new FormAttachment(this.leftComposite);
		fData.right = new FormAttachment(100);
		fData.bottom = new FormAttachment(100);
		rightComposite.setLayoutData(fData);

	}

	private void crearAllInOneUI() {
		this.allComposite = new Composite(this.rightComposite, SWT.NONE);
		FillLayout allCompositeLayout = new FillLayout(SWT.HORIZONTAL);
		this.allComposite.setLayout(allCompositeLayout);

		this.allLeftComposite = new Composite(this.allComposite, SWT.NONE);
		FillLayout pIndexDetailCompositeLayout = new FillLayout(SWT.VERTICAL);
		this.allLeftComposite.setLayout(pIndexDetailCompositeLayout);

		// POWER INDEX UNIT
		this.pIndexAllGroup = new Group(this.allLeftComposite, SWT.NONE);
		GridLayout pIndexAllGroupLayout = new GridLayout();
		pIndexAllGroupLayout.numColumns = 1;
		this.pIndexAllGroup.setLayout(pIndexAllGroupLayout);

		this.pIndexAllGroup.setText("Power Index Unit");

		this.tableIndexAllInOne = new Table(this.pIndexAllGroup, SWT.CHECK | SWT.V_SCROLL);

		GridData gridDataIndex = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableIndexAllInOne.setLayoutData(gridDataIndex);

		// boton select all y deselect all
		Button buttonSelectPower = new Button(this.pIndexAllGroup, SWT.PUSH);
		buttonSelectPower.setText("(Select/Deselect) All");
		GridData gridDataButtonSelectPower = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gridDataButtonSelectPower.horizontalSpan = 1;
		buttonSelectPower.setLayoutData(gridDataButtonSelectPower);
		buttonSelectPower.setSelection(Boolean.FALSE);

		buttonSelectPower.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (null != tableIndexAllInOne && null != tableIndexAllInOne.getItems() && tableIndexAllInOne.getItems().length > 0) {
					Boolean hayCheckeados = Boolean.FALSE;

					for (int i = 0; i < tableIndexAllInOne.getItems().length; i++) {
						if (tableIndexAllInOne.getItems()[i].getChecked()) {
							hayCheckeados = Boolean.TRUE;
						}
					}

					if (hayCheckeados) {
						// si hay elementos seleccionados: deselecciono all
						// tableIndexRead.deselectAll();
						for (int i = 0; i < tableIndexAllInOne.getItems().length; i++) {
							tableIndexAllInOne.getItems()[i].setChecked(false);

						}
					} else {
						// si no hay elementos seleccionados: selecciono all
						// tableIndexRead.selectAll();
						for (int i = 0; i < tableIndexAllInOne.getItems().length; i++) {
							tableIndexAllInOne.getItems()[i].setChecked(true);
						}
					}
				}
			}
		});
		this.pIndexAllGroup.setVisible(true);
		this.addAttributes(this.tableIndexAllInOne, this.checkFormAllInOne, Boolean.FALSE);

		// DETAIL UNIT

		this.detailAllGroup = new Group(this.allLeftComposite, SWT.NONE);
		GridLayout detailAllGroupLayout = new GridLayout();
		detailAllGroupLayout.numColumns = 1;
		this.detailAllGroup.setLayout(detailAllGroupLayout);

		this.detailAllGroup.setText("Detail Unit");

		this.tableDetailAllInOne = new Table(this.detailAllGroup, SWT.CHECK | SWT.V_SCROLL);

		GridData gridDataDetail = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableDetailAllInOne.setLayoutData(gridDataDetail);

		// boton select all y deselect all
		Button buttonSelectDetail = new Button(this.detailAllGroup, SWT.PUSH);
		buttonSelectDetail.setText("(Select/Deselect) All");
		GridData gridDataButtonSelectDetail = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gridDataButtonSelectDetail.horizontalSpan = 1;
		buttonSelectDetail.setLayoutData(gridDataButtonSelectDetail);
		buttonSelectDetail.setSelection(Boolean.FALSE);

		buttonSelectDetail.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (null != tableDetailAllInOne && null != tableDetailAllInOne.getItems() && tableDetailAllInOne.getItems().length > 0) {
					Boolean hayCheckeados = Boolean.FALSE;

					for (int i = 0; i < tableDetailAllInOne.getItems().length; i++) {
						if (tableDetailAllInOne.getItems()[i].getChecked()) {
							hayCheckeados = Boolean.TRUE;
						}
					}

					if (hayCheckeados) {
						// si hay elementos seleccionados: deselecciono all
						// tableIndexRead.deselectAll();
						for (int i = 0; i < tableDetailAllInOne.getItems().length; i++) {
							tableDetailAllInOne.getItems()[i].setChecked(false);

						}
					} else {
						// si no hay elementos seleccionados: selecciono all
						// tableIndexRead.selectAll();
						for (int i = 0; i < tableDetailAllInOne.getItems().length; i++) {
							tableDetailAllInOne.getItems()[i].setChecked(true);
						}
					}
				}
			}
		});
		this.detailAllGroup.setVisible(true);
		this.addAttributes(this.tableDetailAllInOne, this.attDetailListAllInOne, Boolean.FALSE);

		// Composite derecho donde se situan los groups de relations y data unit
		this.allRightComposite = new Composite(this.allComposite, SWT.NONE);
		FillLayout relationsDataCompositeLayout = new FillLayout(SWT.VERTICAL);
		this.allRightComposite.setLayout(relationsDataCompositeLayout);

		// Group de Relations
		this.relationsAllGroup = new Group(this.allRightComposite, SWT.NONE);
		FillLayout relationsAllGroupLayout = new FillLayout(SWT.HORIZONTAL);
		this.relationsAllGroup.setLayout(relationsAllGroupLayout);
		this.relationsAllGroup.setText("Relations");
		this.tableRelationsAll = new Table(this.relationsAllGroup, SWT.CHECK | SWT.V_SCROLL);

		relationsAllGroup.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent evt) {
				relationsAllGroupPaintControl(evt);
			}
		});

		this.addRelationships(this.tableRelationsAll, this.listaCombosAllInOne);

		// Group de Data Unit
		this.dataUnitGroup = new Group(this.allRightComposite, SWT.NONE);
		GridLayout groupArbolLayoutData = new GridLayout();
		groupArbolLayoutData.numColumns = 1;
		this.dataUnitGroup.setLayout(groupArbolLayoutData);
		this.dataUnitGroup.setText("Data Unit");

		this.tableDataAllInOne = new Table(this.dataUnitGroup, SWT.CHECK | SWT.V_SCROLL);

		// FillLayout tableDataAllInOneLayout = new FillLayout(SWT.HORIZONTAL);
		GridData tableDataAllInOneLayout = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableDataAllInOne.setLayoutData(tableDataAllInOneLayout);

		// boton select/deselect all
		Button buttonSelectData = new Button(this.dataUnitGroup, SWT.PUSH);
		buttonSelectData.setText("(Select/Deselect) All");
		GridData gridDatabuttonSelectData = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gridDatabuttonSelectData.horizontalSpan = 3;
		buttonSelectData.setLayoutData(gridDatabuttonSelectData);
		buttonSelectData.setSelection(Boolean.FALSE);

		buttonSelectData.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (null != tableDataAllInOne && null != tableDataAllInOne.getItems() && tableDataAllInOne.getItems().length > 0) {
					Boolean hayCheckeados = Boolean.FALSE;

					for (int i = 0; i < tableDataAllInOne.getItems().length; i++) {
						if (tableDataAllInOne.getItems()[i].getChecked()) {
							hayCheckeados = Boolean.TRUE;
						}
					}

					if (hayCheckeados) {
						// si hay elementos seleccionados: deselecciono all
						// tableIndexRead.deselectAll();
						for (int i = 0; i < tableDataAllInOne.getItems().length; i++) {
							tableDataAllInOne.getItems()[i].setChecked(false);

						}
					} else {
						// si no hay elementos seleccionados: selecciono all
						// tableIndexRead.selectAll();
						for (int i = 0; i < tableDataAllInOne.getItems().length; i++) {
							tableDataAllInOne.getItems()[i].setChecked(true);
						}
					}
				}
			}
		});
		this.dataUnitGroup.setVisible(true);

		this.addAttributes(this.tableDataAllInOne, this.checkDataAllInOne, Boolean.TRUE);

		this.allLeftComposite.setVisible(false);
		this.allRightComposite.setVisible(false);
	}

	private void crearCrudUI() {
		this.crudComposite = new Composite(this.rightComposite, SWT.NONE);
		FillLayout crudCompositeLayout = new FillLayout(SWT.HORIZONTAL);
		this.crudComposite.setLayout(crudCompositeLayout);

		this.crearCreateUI();
		this.crearReadUI();
		this.crearUpdateUI();
		this.crearDeleteUI();
	}

	/**
	 * 
	 * Nombre: crearCrear Funcion:
	 */
	private void crearCreateUI() {
		this.createGroup = new Group(this.crudComposite, SWT.NONE);
		FillLayout createGroupLayout = new FillLayout(SWT.HORIZONTAL);
		this.createGroup.setLayout(createGroupLayout);
		this.createGroup.setText("Create");
		this.createGroup.setVisible(false);

		this.relationsCreateGroup = new Group(this.createGroup, SWT.NONE);
		FillLayout relationsCreateGroupLayout = new FillLayout(SWT.HORIZONTAL);
		relationsCreateGroupLayout.marginHeight = 5;
		this.relationsCreateGroup.setLayout(relationsCreateGroupLayout);
		this.relationsCreateGroup.setText("Relations");
		this.tableRelationsCreate = new Table(this.relationsCreateGroup, SWT.CHECK | SWT.V_SCROLL);
		relationsCreateGroup.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent evt) {
				relationsCreateGroupPaintControl(evt);
			}
		});

		this.addRelationships(this.tableRelationsCreate, this.listaCombosCreate);
	}

	/**
	 * 
	 * Nombre: crearObtener Funcion:
	 */
	private void crearReadUI() {

		this.readGroup = new Group(this.crudComposite, SWT.NONE);
		FillLayout readGroupLayout = new FillLayout(SWT.VERTICAL);
		this.readGroup.setLayout(readGroupLayout);
		this.readGroup.setText("Read");
		this.readGroup.setVisible(false);

		this.pIndexReadGroup = new Group(this.readGroup, SWT.NONE);
		GridLayout pIndexReadGroupLayout = new GridLayout();
		pIndexReadGroupLayout.numColumns = 1;
		this.pIndexReadGroup.setLayout(pIndexReadGroupLayout);
		this.pIndexReadGroup.setText("Power Index Unit");

		this.tableIndexRead = new Table(this.pIndexReadGroup, SWT.CHECK | SWT.V_SCROLL);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableIndexRead.setLayoutData(gridData);

		Button buttonSelectPower = new Button(this.pIndexReadGroup, SWT.PUSH);
		buttonSelectPower.setText("(Select/Deselect) All");
		GridData gridDataButtonSelectPower = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gridDataButtonSelectPower.horizontalSpan = 3;
		buttonSelectPower.setLayoutData(gridDataButtonSelectPower);
		buttonSelectPower.setSelection(Boolean.FALSE);

		buttonSelectPower.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (null != tableIndexRead && null != tableIndexRead.getItems() && tableIndexRead.getItems().length > 0) {
					Boolean hayCheckeados = Boolean.FALSE;

					for (int i = 0; i < tableIndexRead.getItems().length; i++) {
						if (tableIndexRead.getItems()[i].getChecked()) {
							hayCheckeados = Boolean.TRUE;
						}
					}

					if (hayCheckeados) {
						// si hay elementos seleccionados: deselecciono all
						// tableIndexRead.deselectAll();
						for (int i = 0; i < tableIndexRead.getItems().length; i++) {
							tableIndexRead.getItems()[i].setChecked(false);

						}
					} else {
						// si no hay elementos seleccionados: selecciono all
						// tableIndexRead.selectAll();
						for (int i = 0; i < tableIndexRead.getItems().length; i++) {
							tableIndexRead.getItems()[i].setChecked(true);
						}
					}
				}
			}
		});
		this.pIndexReadGroup.setVisible(true);

		this.addAttributes(this.tableIndexRead, this.checkIndexRead, Boolean.FALSE);

		// DETAIL UNIT
		this.detailReadGroup = new Group(this.readGroup, SWT.NONE);
		GridLayout detailReadGroupLayout = new GridLayout();
		detailReadGroupLayout.numColumns = 1;
		this.detailReadGroup.setLayout(detailReadGroupLayout);

		this.detailReadGroup.setText("Detail Unit");

		this.tableDataRead = new Table(this.detailReadGroup, SWT.CHECK | SWT.V_SCROLL);

		GridData gridDataDetail = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableDataRead.setLayoutData(gridDataDetail);

		// boton select all y deselect all
		Button buttonSelectDetail = new Button(this.detailReadGroup, SWT.PUSH);
		buttonSelectDetail.setText("(Select/Deselect) All");
		GridData gridDataButtonSelectDetail = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gridDataButtonSelectDetail.horizontalSpan = 1;
		buttonSelectDetail.setLayoutData(gridDataButtonSelectDetail);
		buttonSelectDetail.setSelection(Boolean.FALSE);

		buttonSelectDetail.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (null != tableDataRead && null != tableDataRead.getItems() && tableDataRead.getItems().length > 0) {
					Boolean hayCheckeados = Boolean.FALSE;

					for (int i = 0; i < tableDataRead.getItems().length; i++) {
						if (tableDataRead.getItems()[i].getChecked()) {
							hayCheckeados = Boolean.TRUE;
						}
					}

					if (hayCheckeados) {
						// si hay elementos seleccionados: deselecciono all
						// tableIndexRead.deselectAll();
						for (int i = 0; i < tableDataRead.getItems().length; i++) {
							tableDataRead.getItems()[i].setChecked(false);

						}
					} else {
						// si no hay elementos seleccionados: selecciono all
						// tableIndexRead.selectAll();
						for (int i = 0; i < tableDataRead.getItems().length; i++) {
							tableDataRead.getItems()[i].setChecked(true);
						}
					}
				}
			}
		});
		this.detailReadGroup.setVisible(true);

		this.addAttributes(this.tableDataRead, this.checkDataRead, Boolean.FALSE);

	}

	/**
	 * 
	 * Nombre: crearActualizar Funcion:
	 */
	private void crearUpdateUI() {

		this.updateGroup = new Group(this.crudComposite, SWT.NONE);
		FillLayout updateGroupLayout = new FillLayout(SWT.VERTICAL);
		this.updateGroup.setLayout(updateGroupLayout);
		this.updateGroup.setText("Update");
		this.updateGroup.setVisible(false);

		this.pIndexUpdateGroup = new Group(this.updateGroup, SWT.NONE);
		GridLayout pIndexUpdateGroupLayout = new GridLayout();
		pIndexUpdateGroupLayout.numColumns = 1;
		this.pIndexUpdateGroup.setLayout(pIndexUpdateGroupLayout);
		this.pIndexUpdateGroup.setText("Power Index Unit");

		this.tableIndexUpdate = new Table(this.pIndexUpdateGroup, SWT.CHECK | SWT.V_SCROLL);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableIndexUpdate.setLayoutData(gridData);

		// boton select all y deselect all
		Button buttonSelectPower = new Button(this.pIndexUpdateGroup, SWT.PUSH);
		buttonSelectPower.setText("(Select/Deselect) All");
		GridData gridDataButtonSelectPower = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gridDataButtonSelectPower.horizontalSpan = 3;
		buttonSelectPower.setLayoutData(gridDataButtonSelectPower);
		buttonSelectPower.setSelection(Boolean.FALSE);

		buttonSelectPower.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (null != tableIndexRead && null != tableIndexUpdate.getItems() && tableIndexUpdate.getItems().length > 0) {
					Boolean hayCheckeados = Boolean.FALSE;

					for (int i = 0; i < tableIndexUpdate.getItems().length; i++) {
						if (tableIndexUpdate.getItems()[i].getChecked()) {
							hayCheckeados = Boolean.TRUE;
						}
					}

					if (hayCheckeados) {
						// si hay elementos seleccionados: deselecciono all
						// tableIndexRead.deselectAll();
						for (int i = 0; i < tableIndexUpdate.getItems().length; i++) {
							tableIndexUpdate.getItems()[i].setChecked(false);

						}
					} else {
						// si no hay elementos seleccionados: selecciono all
						// tableIndexRead.selectAll();
						for (int i = 0; i < tableIndexUpdate.getItems().length; i++) {
							tableIndexUpdate.getItems()[i].setChecked(true);
						}
					}
				}
			}
		});
		this.pIndexUpdateGroup.setVisible(true);

		// fin boton select all y deselect all

		this.relationsFromGroup = new Group(this.updateGroup, SWT.NONE);
		FillLayout relationsFromGroupLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
		this.relationsFromGroup.setLayout(relationsFromGroupLayout);
		this.relationsFromGroup.setText("Relations and forms");
		this.tableRelFormUpdate = new Table(this.relationsFromGroup, SWT.CHECK | SWT.V_SCROLL);

		relationsFromGroup.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent evt) {
				relationsFromGroupPaintControl(evt);
			}
		});

		this.addAttributes(this.tableRelFormUpdate, this.checkIndexUpdate, Boolean.FALSE);
		this.addAttributes(this.tableIndexUpdate, this.checkShowUpdate, Boolean.FALSE);
		this.addRelationships(this.tableRelFormUpdate, this.listaCombosUpdate);
	}

	/**
	 * 
	 * Nombre: crearBorrado Funcion:
	 */
	private void crearDeleteUI() {

		this.deleteGroup = new Group(this.crudComposite, SWT.NONE);
		FillLayout deleteGroupLayout = new FillLayout(SWT.HORIZONTAL);
		this.deleteGroup.setLayout(deleteGroupLayout);
		this.deleteGroup.setText("Delete");
		this.deleteGroup.setVisible(false);

		this.pIndexDeleteGroup = new Group(this.deleteGroup, SWT.NONE);
		GridLayout groupArbolLayout = new GridLayout();
		groupArbolLayout.numColumns = 1;
		this.pIndexDeleteGroup.setLayout(groupArbolLayout);
		this.pIndexDeleteGroup.setText("Power Index Unit");

		this.tableIndexDelete = new Table(this.pIndexDeleteGroup, SWT.CHECK | SWT.V_SCROLL);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableIndexDelete.setLayoutData(gridData);

		// boton select all y deselect all
		Button buttonSelectPowerIndexBorrado = new Button(this.pIndexDeleteGroup, SWT.PUSH);
		buttonSelectPowerIndexBorrado.setText("(Select/Deselect) All");
		GridData gridDataButtonSelectPower = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gridDataButtonSelectPower.horizontalSpan = 3;
		buttonSelectPowerIndexBorrado.setLayoutData(gridDataButtonSelectPower);
		buttonSelectPowerIndexBorrado.setSelection(Boolean.FALSE);

		buttonSelectPowerIndexBorrado.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (null != tableIndexDelete && null != tableIndexDelete.getItems() && tableIndexDelete.getItems().length > 0) {
					Boolean hayCheckeados = Boolean.FALSE;

					for (int i = 0; i < tableIndexDelete.getItems().length; i++) {
						if (tableIndexDelete.getItems()[i].getChecked()) {
							hayCheckeados = Boolean.TRUE;
						}
					}

					if (hayCheckeados) {
						// si hay elementos seleccionados: deselecciono
						// all
						// tableIndexRead.deselectAll();
						for (int i = 0; i < tableIndexDelete.getItems().length; i++) {
							tableIndexDelete.getItems()[i].setChecked(false);

						}
					} else {
						// si no hay elementos seleccionados: selecciono
						// all
						// tableIndexRead.selectAll();
						for (int i = 0; i < tableIndexDelete.getItems().length; i++) {
							tableIndexDelete.getItems()[i].setChecked(true);
						}
					}
				}
			}
		});
		this.pIndexDeleteGroup.setVisible(true);

		// fin boton select all y deselect all

		this.addAttributes(this.tableIndexDelete, this.checkIndexDelete, Boolean.FALSE);
	}

	private void allCheckSelectionAction() {
		if (this.allCheck.getSelection()) {
			this.crearAllInOneUI();
			this.cCheck.setEnabled(false);
			this.rCheck.setEnabled(false);
			this.uCheck.setEnabled(false);
			this.dCheck.setEnabled(false);
			this.mPageOpGroup.setEnabled(false);
			this.allLeftComposite.setVisible(true);
			this.allRightComposite.setVisible(true);

			this.operationsChecked.add(Utilities.Operations.ALLINONE);

		} else {
			this.cCheck.setEnabled(true);
			this.rCheck.setEnabled(true);
			this.uCheck.setEnabled(true);
			this.dCheck.setEnabled(true);
			this.mPageOpGroup.setEnabled(true);
			this.allLeftComposite.setVisible(false);
			this.allRightComposite.setVisible(false);

			this.checkFormAllInOne.clear();
			this.listaCombosAllInOne.clear();
			this.checkDataAllInOne.clear();

			this.operationsChecked.remove(Utilities.Operations.ALLINONE);

			this.allComposite.dispose();
		}
		getWizard().getContainer().updateButtons();
		this.containerComposite.layout(true, true);
	}

	private void cCheckSelectionAction() {

		if (cCheck.getSelection()) {
			if (this.crudComposite == null)
				crearCrudUI();
			createGroup.setVisible(true);
			this.operationsChecked.add(Utilities.Operations.CREATE);
		} else {
			createGroup.setVisible(false);
			this.operationsChecked.remove(Utilities.Operations.CREATE);
			this.listaCombosCreate.clear();
		}
		getWizard().getContainer().updateButtons();
		activaDesactivaChecks();
	}

	private void rCheckSelectionAction() {

		if (rCheck.getSelection()) {
			if (this.crudComposite == null)
				crearCrudUI();
			readGroup.setVisible(true);
			this.operationsChecked.add(Utilities.Operations.READ);
		} else {
			readGroup.setVisible(false);
			this.operationsChecked.remove(Utilities.Operations.READ);
			this.checkIndexRead.clear();

		}
		getWizard().getContainer().updateButtons();
		activaDesactivaChecks();
	}

	private void uCheckSelectionAction() {

		if (uCheck.getSelection()) {
			if (this.crudComposite == null)
				crearCrudUI();
			updateGroup.setVisible(true);
			this.operationsChecked.add(Utilities.Operations.UPDATE);
		} else {
			updateGroup.setVisible(false);
			this.operationsChecked.remove(Utilities.Operations.UPDATE);
			this.checkIndexUpdate.clear();
			this.checkShowUpdate.clear();
			this.listaCombosUpdate.clear();
		}
		getWizard().getContainer().updateButtons();
		activaDesactivaChecks();
	}

	private void dCheckSelectionAction() {
		if (dCheck.getSelection()) {
			if (this.crudComposite == null)
				crearCrudUI();
			deleteGroup.setVisible(true);
			this.operationsChecked.add(Utilities.Operations.DELETE);
		} else {
			deleteGroup.setVisible(false);
			this.operationsChecked.remove(Utilities.Operations.DELETE);
			this.checkIndexDelete.clear();
		}
		activaDesactivaChecks();
		getWizard().getContainer().updateButtons();
	}

	private void activaDesactivaChecks() {
		if (cCheck.getSelection() || rCheck.getSelection() || uCheck.getSelection() || dCheck.getSelection()) {
			allCheck.setSelection(false);
			allCheck.setEnabled(false);
			sPageOpGroup.setEnabled(false);
		} else {
			sPageOpGroup.setEnabled(true);
			allCheck.setEnabled(true);
			this.crudComposite.dispose();
			this.crudComposite = null;
		}
		this.containerComposite.layout(true, true);
	}

	public List<Utilities.Operations> getOperationsChecked() {
		return this.operationsChecked;
	}

	/**
	 * 
	 * Nombre: addAtributesToCombo Funcion:
	 * 
	 * @param combo
	 * @param role
	 * @param editor
	 * @return
	 */
	private CCombo addAtributesToCombo(CCombo combo, IRelationshipRole role, TableEditor editor) {
		IEntity entidad;
		IRelationship relation = (IRelationship) role.getParentElement();
		if (relation.getTargetEntity() == this.entidad) {
			entidad = relation.getSourceEntity();
		} else
			entidad = relation.getTargetEntity();

		List<IAttribute> atributos = entidad.getAllAttributeList();
		Iterator<IAttribute> iteratorAtributos = atributos.iterator();
		IAttribute atributo;
		String texto;

		while (iteratorAtributos.hasNext()) {
			atributo = iteratorAtributos.next();
			texto = Utilities.getAttribute(atributo, "name") + " (" + Utilities.getAttribute(role, "name") + ")";
			combo.add(Utilities.getAttribute(atributo, "name"));
			this.atributosRelacion.put(texto, atributo);
		}
		return combo;
	}

	/**
	 * 
	 */
	// (Detalle)
	private void addAttributes(Table tabla, List<TableItem> list, Boolean esDataUnit) {
		Iterator<IAttribute> iteratorAttribute;
		if (tabla == this.tableRelFormUpdate)
			iteratorAttribute = this.listaAtributosSinDerivados.iterator();
		else
			iteratorAttribute = this.listaAtributosEntidad.iterator();
		IAttribute atributo;
		while (iteratorAttribute.hasNext()) {
			atributo = iteratorAttribute.next();
			// Se elimina oid de los Data Unit
			if (!(esDataUnit && Utilities.getAttribute(atributo, "key").equals("true") && ("oid".equals(Utilities.getAttribute(atributo,
					"name")) || "OID".equals(Utilities.getAttribute(atributo, "name"))))) {
				list.add(new TableItem(tabla, SWT.NONE));
				list.get(list.size() - 1).setText(Utilities.getAttribute(atributo, "name") + " (" + atributo.getFinalId() + ")");
			}

		}
	}

	/**
	 * 
	 * Nombre: addRelationships Funcion:
	 * 
	 * @param tabla
	 * @param list
	 */
	private void addRelationships(Table tabla, List<CCombo> list) {
		for (int i = 0; i < 2; i++) {
			TableColumn column = new TableColumn(tabla, SWT.NONE);
			column.setWidth(100);
		}
		for (int i = 0; i < entidadesRelacionadas.size(); i++) {
			new TableItem(tabla, SWT.NONE);
		}
		TableItem[] items = tabla.getItems();

		int numAtributos = this.entidad.getAttributeList().size();

		if (tabla == this.tableRelFormUpdate)
			numAtributos = this.listaAtributosSinDerivados.size();

		if (tabla == this.tableRelationsCreate || tabla == this.tableRelationsAll)
			numAtributos = 0;
		for (int i = numAtributos; i < items.length; i++) {
			TableEditor editor = new TableEditor(tabla);
			Text text = new Text(tabla, SWT.NONE);
			text.setText(Utilities.getAttribute(this.entidadesRelacionadas.get(i - numAtributos), "name"));
			editor.grabHorizontal = true;
			editor.setEditor(text, items[i], 0);
			editor = new TableEditor(tabla);
			CCombo combo = new CCombo(tabla, SWT.NONE);
			combo = this.addAtributesToCombo(combo, this.entidadesRelacionadas.get(i - numAtributos), editor);
			combo.select(0);
			Integer posicion = new Integer(i);// se a�ade posicion que ocupa
												// el combo, sera igual a la del
												// editor asociado a dicho combo
			combo.setData(posicion);// se a�ade posicion que ocupa el combo,
									// sera igual a la del editor asociado a
									// dicho combo
			combo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					cCombo1WidgetSelected(evt);
				}
			});
			list.add(combo);
			editor.grabHorizontal = true;
			editor.setEditor(combo, items[i], 1);
		}
	}

	/**
	 * 
	 * Nombre: cCombo1WidgetSelected Funcion:
	 * 
	 * @param evt
	 */
	private void cCombo1WidgetSelected(SelectionEvent evt) {
		try {
			CCombo c = (CCombo) evt.getSource();
			Table t = (Table) c.getParent();

			t.getItems()[(Integer) c.getData()].setChecked(Boolean.TRUE);

			if (t == this.tableRelationsCreate) {
				for (int i = 0; i < this.listaCombosCreate.size(); i++) {
					/*
					 * this.listaCombosUpdate.get(i).select(
					 * this.listaCombosCreate.get(i).getSelectionIndex());
					 * this.listaCombosAllInOne.get(i).select(
					 * this.listaCombosCreate.get(i).getSelectionIndex());
					 */
				}
			}

			if (t == this.tableRelFormUpdate) {
				for (int i = 0; i < this.listaCombosCreate.size(); i++) {
					/*
					 * this.listaCombosCreate.get(i).select(
					 * this.listaCombosUpdate.get(i).getSelectionIndex());
					 * this.listaCombosAllInOne.get(i).select(
					 * this.listaCombosUpdate.get(i).getSelectionIndex());
					 */

				}
			}
			if (t == this.tableRelationsAll) {

				for (int i = 0; i < this.listaCombosCreate.size(); i++) {
					/*
					 * this.listaCombosCreate.get(i) .select(
					 * this.listaCombosAllInOne.get(i) .getSelectionIndex());
					 * this.listaCombosUpdate.get(i) .select(
					 * this.listaCombosAllInOne.get(i) .getSelectionIndex());
					 */
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	public void createControl(Composite parent) {
		this.containerComposite = new Composite(parent, SWT.NULL);
		FormLayout thisLayout = new FormLayout();
		this.containerComposite.setLayout(thisLayout);
		this.containerComposite.layout();
		setControl(this.containerComposite);

		if (this.entidad != null)
			this.initialize();
	}

	/**
	 * 
	 * Nombre: getAttributesDataCreate Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesDataCreate() {
		return this.listaAtributosSinDerivados;
	}

	/**
	 * 
	 * Nombre: getAttributesDataRead Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesDataRead() {

		// crear hash de atributos
		HashMap<String, IAttribute> hashLista = new HashMap<String, IAttribute>();
		// cargar hash
		for (int i = 0; i < this.listaAtributosEntidad.size(); i++) {
			if (!hashLista.containsKey(Utilities.getAttribute(this.listaAtributosEntidad.get(i), "name"))) {
				hashLista.put(Utilities.getAttribute(this.listaAtributosEntidad.get(i), "name"), this.listaAtributosEntidad.get(i));
			}
		}

		List<IAttribute> lista = new ArrayList<IAttribute>();
		if (null != this.tableDataRead && null != this.tableDataRead.getItems() && this.tableDataRead.getItems().length > 0) {
			for (int i = 0; i < this.tableDataRead.getItems().length; i++) {
				if (this.tableDataRead.getItem(i).getChecked()) {
					String name = (null == this.tableDataRead.getItem(i).getText() ? null : this.tableDataRead.getItem(i).getText()
							.split(" \\(")[0]);
					if (null != name && hashLista.containsKey(name)) {
						lista.add(hashLista.get(name));
					}
				}
			}
		}

		return lista;
	}

	/**
	 * 
	 * Nombre: getAttributesDataAllInOne Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesDataAllInOne() {

		// crear hash de atributos
		HashMap<String, IAttribute> hashLista = new HashMap<String, IAttribute>();
		// cargar hash
		for (int i = 0; i < this.listaAtributosEntidad.size(); i++) {
			if (!hashLista.containsKey(Utilities.getAttribute(this.listaAtributosEntidad.get(i), "name"))) {
				hashLista.put(Utilities.getAttribute(this.listaAtributosEntidad.get(i), "name"), this.listaAtributosEntidad.get(i));
			}
		}

		List<IAttribute> lista = new ArrayList<IAttribute>();
		if (null != this.tableDataAllInOne && null != this.tableDataAllInOne.getItems() && this.tableDataAllInOne.getItems().length > 0) {
			for (int i = 0; i < this.tableDataAllInOne.getItems().length; i++) {
				if (this.tableDataAllInOne.getItem(i).getChecked()) {
					// el text esta compuesto por name + espacio + ( + id +):
					// obtener el name
					String name = (null == this.tableDataAllInOne.getItem(i).getText() ? null : this.tableDataAllInOne.getItem(i).getText()
							.split(" \\(")[0]);
					if (null != name && hashLista.containsKey(name)) {
						lista.add(hashLista.get(name));
					}
				}
			}
		}

		return lista;
	}

	public List<IAttribute> getAttributesDetailAllInOne() {

		// crear hash de atributos
		HashMap<String, IAttribute> hashLista = new HashMap<String, IAttribute>();
		// cargar hash
		for (int i = 0; i < this.listaAtributosEntidad.size(); i++) {
			if (!hashLista.containsKey(Utilities.getAttribute(this.listaAtributosEntidad.get(i), "name"))) {
				hashLista.put(Utilities.getAttribute(this.listaAtributosEntidad.get(i), "name"), this.listaAtributosEntidad.get(i));
			}
		}

		List<IAttribute> lista = new ArrayList<IAttribute>();
		if (null != this.tableDetailAllInOne && null != this.tableDetailAllInOne.getItems()
				&& this.tableDetailAllInOne.getItems().length > 0) {
			for (int i = 0; i < this.tableDetailAllInOne.getItems().length; i++) {
				if (this.tableDetailAllInOne.getItem(i).getChecked()) {
					// el text esta compuesto por name + espacio + ( + id +):
					// obtener el name
					String name = (null == this.tableDetailAllInOne.getItem(i).getText() ? null : this.tableDetailAllInOne.getItem(i)
							.getText().split(" \\(")[0]);
					if (null != name && hashLista.containsKey(name)) {
						lista.add(hashLista.get(name));
					}
				}
			}
		}

		return lista;
	}

	/**
	 * 
	 * Nombre: getAttributesIndexDelete Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesIndexDelete() {
		List<IAttribute> lista = new ArrayList<IAttribute>();
		for (int i = 0; i < this.listaAtributosEntidad.size(); i++) {
			if (this.tableIndexDelete.getItem(i).getChecked())
				lista.add(this.listaAtributosEntidad.get(i));
		}
		return lista;
	}

	/**
	 * 
	 * Nombre: getAttributesIndexRead Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesIndexRead() {
		List<IAttribute> lista = new ArrayList<IAttribute>();
		for (int i = 0; i < this.listaAtributosEntidad.size(); i++) {
			if (this.tableIndexRead.getItem(i).getChecked())
				lista.add(this.listaAtributosEntidad.get(i));
		}
		return lista;
	}

	/**
	 * 
	 * Nombre: getAttributesShowUpdate Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesShowUpdate() {
		List<IAttribute> lista = new ArrayList<IAttribute>();
		for (int i = 0; i < this.listaAtributosEntidad.size(); i++) {
			if (this.tableIndexUpdate.getItem(i).getChecked())
				lista.add(this.listaAtributosEntidad.get(i));
		}
		return lista;
	}

	/**
	 * 
	 * Nombre: getAttributesUpdate Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesUpdate() {
		List<IAttribute> lista = new ArrayList<IAttribute>();
		for (int i = 0; i < this.listaAtributosSinDerivados.size(); i++) {
			if (this.tableRelFormUpdate.getItem(i).getChecked())
				lista.add(this.listaAtributosSinDerivados.get(i));
		}
		return lista;
	}

	/**
	 * 
	 * Nombre: getBuscadorDelete Funcion:
	 * 
	 * @return
	 */
	public IAttribute getBuscadorDelete() {
		if (this.tableOpcionesDelete.getItem(0).getChecked())
			return this.listaAtributosEntidad.get(this.checkOpcionesDelete.get(0).getSelectionIndex());
		else
			return null;
	}

	/**
	 * 
	 * Nombre: getBuscadorRead Funcion:
	 * 
	 * @return
	 */
	public IAttribute getBuscadorRead() {
		if (this.tableOpcionesRead.getItem(0).getChecked())
			return this.listaAtributosEntidad.get(this.checkOpcionesRead.get(0).getSelectionIndex());
		else
			return null;
	}

	/**
	 * 
	 * Nombre: getBuscadorUpdate Funcion:
	 * 
	 * @return
	 */
	public IAttribute getBuscadorUpdate() {
		if (this.tableOpcionesUpdate.getItem(0).getChecked())
			return this.listaAtributosEntidad.get(this.checkOpcionesUpdate.get(0).getSelectionIndex());
		else
			return null;
	}

	/*
	 * @Override public IWizardPage getPreviousPage() { return null; }
	 */

	/**
	 * 
	 */
	public Map<IRelationshipRole, IAttribute> getRelationShipsCreate() {
		Map<IRelationshipRole, IAttribute> mapaRelaciones = new HashMap<IRelationshipRole, IAttribute>();
		try {
			String key;
			for (int i = 0; i < this.listaCombosCreate.size(); i++) {
				if (this.tableRelationsCreate.getItems()[i].getChecked()) {
					// this.tableIndexCreate.getItems()[i].checked
					key = this.listaCombosCreate.get(i).getItem(this.listaCombosCreate.get(i).getSelectionIndex()) + " ("
							+ Utilities.getAttribute(this.entidadesRelacionadas.get(i), "name") + ")";
					mapaRelaciones.put(this.entidadesRelacionadas.get(i), this.atributosRelacion.get(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapaRelaciones;
	}

	/**
	 * 
	 * Nombre: getRelationShipsAllInOne Funcion:
	 * 
	 * @return
	 */
	public Map<IRelationshipRole, IAttribute> getRelationShipsAllInOne() {
		Map<IRelationshipRole, IAttribute> mapaRelaciones = new HashMap<IRelationshipRole, IAttribute>();
		try {
			String key;
			for (int i = 0; i < this.listaCombosAllInOne.size(); i++) {
				// RELATION 1.15: solo meter la de aquellos que se
				// hayan seleccionado
				if (this.tableRelationsAll.getItems()[i].getChecked()) {
					key = this.listaCombosAllInOne.get(i).getItem(this.listaCombosAllInOne.get(i).getSelectionIndex()) + " ("
							+ Utilities.getAttribute(this.entidadesRelacionadas.get(i), "name") + ")";
					mapaRelaciones.put(this.entidadesRelacionadas.get(i), this.atributosRelacion.get(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapaRelaciones;
	}

	/**
	 * 
	 * Nombre: getRelationShipsUpdate Funcion:
	 * 
	 * @return
	 */
	public Map<IRelationshipRole, IAttribute> getRelationShipsUpdate() {

		Map<IRelationshipRole, IAttribute> mapaRelaciones = new HashMap<IRelationshipRole, IAttribute>();
		try {
			String key;
			for (int i = 0; i < this.listaCombosUpdate.size(); i++) {
				if (this.tableRelFormUpdate.getItem(this.listaAtributosSinDerivados.size() + i).getChecked()) {
					key = this.listaCombosUpdate.get(i).getItem(this.listaCombosUpdate.get(i).getSelectionIndex()) + " ("
							+ Utilities.getAttribute(this.entidadesRelacionadas.get(i), "name") + ")";
					mapaRelaciones.put(this.entidadesRelacionadas.get(i), this.atributosRelacion.get(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapaRelaciones;
	}

	/**
	 * 
	 * Nombre: getSiteViews Funcion:
	 * 
	 * @param tipoOperacion
	 * @return
	 */
	public List<ISiteView> getSiteViewsChecked() {
		// obtener solamente los checkeados
		TreeItem[] arrSiteViewSelected = this.arbolSvAreas.getItems();

		List<ISiteView> lista = new ArrayList<ISiteView>();
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

		return lista;
	}

	/**
	 * 
	 * 
	 * Nombre: buscarElementoSiteView Funcion:
	 * 
	 * @param nombre
	 * @return
	 */
	public ISiteView buscarElementoSiteView(String nombre) {

		for (int j = 0; j < this.listaSiteViews.size(); j++) {
			ISiteView siteView = this.listaSiteViews.get(j);
			String valorCompleto = Utilities.getAttribute(siteView, "name") + " (" + siteView.getFinalId() + ")";
			// 2 (sv11)
			if (nombre.compareTo(valorCompleto) == 0 || valorCompleto.startsWith(nombre + " ")) {
				return siteView;
			}
		}

		return null;
	}

	/**
	 * 
	 */
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
						IArea areabuscar = buscarElementoAreaRecursivo(area.getAreaList(), nombre);
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

				areaEnc = buscarElementoAreaRecursivo(siteView.getAreaList(), nombre);
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
				if (null != arrItemRecorrer[i] && arrItemRecorrer[i].getChecked()) {
					if (null != arrItemRecorrer[i].getData() && ((ObjStViewArea) arrItemRecorrer[i].getData()).getTipo().equals("STVIEW")
							&& null != arrItemRecorrer[i].getItems()) {
						obtenerHijosCheckeados(retColItemSelEhijos, arrItemRecorrer[i].getItems());
					} else {

						// segundo comprueba si es de tipo area y no tiene hijos
						if (null != arrItemRecorrer[i].getData() && ((ObjStViewArea) arrItemRecorrer[i].getData()).getTipo().equals("AREA")
								&& null == arrItemRecorrer[i].getItems()) {

							retColItemSelEhijos.add(arrItemRecorrer[i]);

						}

						// tercero comprueba si es de tipo area y ninguno de sus
						// hijos siguientes tiene checkeado
						if (null != arrItemRecorrer[i].getData() && ((ObjStViewArea) arrItemRecorrer[i].getData()).getTipo().equals("AREA")
								&& null != arrItemRecorrer[i].getItems()) {

							int contador = 0;
							for (int j = 0; j < arrItemRecorrer[i].getItems().length; j++) {
								if (null != arrItemRecorrer[i].getItems()[j] && arrItemRecorrer[i].getItems()[j].getChecked()) {
									contador++;
									obtenerHijosCheckeados(retColItemSelEhijos, arrItemRecorrer[i].getItems());
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

	/**
	 * 
	 * Nombre: getAreasRetrieve Funcion:
	 * 
	 * @return
	 */
	public List<IArea> getAreas() {

		List<IArea> lista = new ArrayList<IArea>();
		Collection<TreeItem> retColItemSelEhijos = new ArrayList<TreeItem>();

		obtenerHijosCheckeados(retColItemSelEhijos, this.arbolSvAreas.getItems());

		if (null != retColItemSelEhijos) {
			for (Iterator iterator = retColItemSelEhijos.iterator(); iterator.hasNext();) {
				TreeItem treeItem = (TreeItem) iterator.next();

				IArea area = buscarElementoArea(((ObjStViewArea) treeItem.getData()).getNombre());
				if (null != area) {
					lista.add(area);
				}
			}
		}
		return lista;
	}

	/**
	 * 
	 * Nombre: group2PaintControl Funcion:
	 * 
	 * @param evt
	 */
	private void relationsCreateGroupPaintControl(PaintEvent evt) {
		int tamanio = (this.relationsCreateGroup.getSize().x - 10) / 2;
		TableColumn[] columns = this.tableRelationsCreate.getColumns();
		for (int i = 0; i < 2; i++) {
			columns[i].setWidth(tamanio);
		}
	}

	/**
	 * 
	 * Nombre: group7PaintControl Funcion:
	 * 
	 * @param evt
	 */
	private void relationsFromGroupPaintControl(PaintEvent evt) {
		int tamanio = (this.relationsFromGroup.getSize().x - 10) / 2;
		TableColumn[] columns = this.tableRelFormUpdate.getColumns();
		for (int i = 0; i < 2; i++) {
			columns[i].setWidth(tamanio);
		}
	}

	/**
	 * 
	 * Nombre: group13PaintControl Funcion:
	 * 
	 * @param evt
	 */
	private void relationsAllGroupPaintControl(PaintEvent evt) {
		int tamanio = (this.relationsAllGroup.getSize().x - 10) / 2;
		TableColumn[] columns = this.tableRelationsAll.getColumns();
		for (int i = 0; i < 2; i++) {
			columns[i].setWidth(tamanio);
		}
	}

	/**
	 * 
	 * Nombre: actualizarListaSiteViews Funcion:
	 * 
	 * @throws ExecutionException
	 */
	public void actualizarListaSiteViews() throws ExecutionException {
		ProjectParameters.init();
		ProjectParameters.initSiteViews();
		this.listaSiteViews = ProjectParameters.getWebModel().getSiteViewList();
	}

	/**
	 * 
	 * Nombre: initialize Funcion:
	 */
	public void initialize() {
		try {
			if (this.entidad == null) {
				this.pageSelectEntity = (WizardSelectEntityPage) this.getWizard().getStartingPage();

				this.entidad = (IEntity) this.pageSelectEntity.getSelectedElement();
			}
			declaracionEstructuras(entidad);
			this.initRelationShips();
			// De aqui se obtienen los atributos de la entidad
			this.listaAtributosEntidad = this.entidad.getAllAttributeList();
			Iterator<IAttribute> iteratorAtributos = this.listaAtributosEntidad.iterator();
			IAttribute atributo;
			while (iteratorAtributos.hasNext()) {
				atributo = iteratorAtributos.next();
				if (Utilities.getAttribute(atributo, "derivationQuery").equals("")
						&& !Utilities.getAttribute(atributo, "key").equals("true")) {
					this.listaAtributosSinDerivados.add(atributo);
				}
			}
			this.listaSiteViews = ProjectParameters.getWebModel().getSiteViewList();

			this.crearUI();

			try {
				this.dispose();
				this.finalize();
			} catch (Throwable e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Nombre: initRelationShips Funcion:
	 */
	private void initRelationShips() {
		List<IRelationship> lista = this.entidad.getOutgoingRelationshipList();
		lista.addAll(this.entidad.getIncomingRelationshipList());
		Iterator<IRelationship> iteratorRelacion = lista.iterator();
		IRelationship relacion;
		IRelationshipRole role1, role2;
		String maxCard;
		this.entidadesRelacionadas = new ArrayList<IRelationshipRole>();
		while (iteratorRelacion.hasNext()) {
			relacion = iteratorRelacion.next();
			if (relacion.getSourceEntity() == this.entidad) {
				role1 = relacion.getRelationshipRole1();
				maxCard = Utilities.getAttribute(role1, "maxCard");
				if (maxCard.equals("1")) {
					this.entidadesRelacionadas.add(role1);
				} else {
					role2 = relacion.getRelationshipRole2();
					maxCard = Utilities.getAttribute(role2, "maxCard");
					if (maxCard.equals("N")) {
						this.entidadesRelacionadas.add(role1);
					}
				}
			} else {
				role1 = relacion.getRelationshipRole2();
				maxCard = Utilities.getAttribute(role1, "maxCard");
				if (maxCard.equals("1")) {
					this.entidadesRelacionadas.add(role1);
				} else {
					role2 = relacion.getRelationshipRole1();
					maxCard = Utilities.getAttribute(role2, "maxCard");
					if (maxCard.equals("N")) {
						this.entidadesRelacionadas.add(role1);
					}
				}
			}
		}
	}

	/**
	 * 
	 * Nombre: setEntity Funcion:
	 * 
	 * @param entidad
	 */
	public void setEntity(IEntity entidad) {
		this.entidad = entidad;
	}

	/**
	 * 
	 * Nombre: inicializarListaYarbol Funcion:
	 */
	private void inicializarListaYarbol() {
		List<ISiteView> listaSiteViewsPreviaPage = ProjectParameters.getWebModel().getSiteViewList();

		// Inicializa elementos del arbol para volver a version de siteView-
		// areas creados
		List<ObjStViewArea> listaSiteViewArea = new ArrayList();

		arbolSvAreas.removeAll();
		arbolSvAreas.clearAll(Boolean.TRUE);

		if (null != listaSiteViewsPreviaPage && listaSiteViewsPreviaPage.size() > 0) {
			for (Iterator iterator = listaSiteViewsPreviaPage.iterator(); iterator.hasNext();) {
				ISiteView siteView = (ISiteView) iterator.next();
				if (null != siteView) {
					ObjStViewArea objStView = new ObjStViewArea();
					objStView.setNombre(Utilities.getAttribute(siteView, "name") + " (" + siteView.getFinalId() + ")");
					objStView.setTipo("STVIEW");
					listaSiteViewArea.add(objStView);

					TreeItem itemSiteView = new TreeItem(arbolSvAreas, 0);

					itemSiteView.setText(Utilities.getAttribute(siteView, "name") + " (" + siteView.getFinalId() + ")");

					if (null != siteView.getAreaList() && siteView.getAreaList().size() > 0) {
						montarArbolAreas(itemSiteView, objStView, siteView.getAreaList());
					}
				}
			}
		}

		arbolSvAreas.redraw();
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

					arbolSvAreas.select(itemHijo);

					montarArbolAreas(itemHijo, objArea1, area.getAreaList());
				}
			}

		}
	}

	private void aniadirElementoToArbol(Tree nodoArbolPadre, TreeItem nodoItemPadre, List<ObjStViewArea> listaElementosPagePrevia) {

		if (null != listaElementosPagePrevia && listaElementosPagePrevia.size() > 0) {
			for (Iterator iterator = listaElementosPagePrevia.iterator(); iterator.hasNext();) {
				ObjStViewArea objStViewArea = (ObjStViewArea) iterator.next();
				TreeItem item0 = null;
				if (objStViewArea.getTipo().equals("AREA")) {
					item0 = new TreeItem(nodoItemPadre, 0);
				} else {
					item0 = new TreeItem(nodoArbolPadre, 0);
				}
				item0.setText(objStViewArea.getNombre());
				item0.setData(objStViewArea);

				aniadirElementoToArbol(nodoArbolPadre, item0, objStViewArea.getListHijos());
			}
		}

	}

	/**
	 * 
	 * Nombre: listaSiteAreaToArbol Funcion:
	 * 
	 * @param arbol
	 */
	private void listaSiteAreaToArbol(Tree arbol) {
		if (null != ProjectParameters.getlistaSiteViewArea() && ProjectParameters.getlistaSiteViewArea().size() > 0) {

			aniadirElementoToArbol(arbol, null, ProjectParameters.getlistaSiteViewArea());
		}
	}

	/**
	 * 
	 * Nombre: getAttributesIndexAllInOne Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesIndexAllInOne() {
		List<IAttribute> lista = new ArrayList<IAttribute>();
		for (int i = 0; i < this.listaAtributosEntidad.size(); i++) {
			if (this.tableIndexAllInOne.getItem(i).getChecked())
				lista.add(this.listaAtributosEntidad.get(i));
		}
		return lista;
	}

	/**
	 * 
	 * Nombre: checkPath Funcion:
	 * 
	 * @param item
	 * @param checked
	 * @param grayed
	 */
	static void checkPath(

	TreeItem item, boolean checked, boolean grayed) {
		if (item == null)
			return;
		if (grayed) {
			checked = true;
		} else {
			int index = 0;
			TreeItem[] items = item.getItems();
			while (index < items.length) {
				TreeItem child = items[index];
				if (child.getGrayed() || checked != child.getChecked()) {
					checked = grayed = true;
					break;
				}
				index++;
			}
		}
		item.setChecked(checked);
		item.setGrayed(grayed);
		checkPath(item.getParentItem(), checked, grayed);
	}

	/**
	 * 
	 * Nombre: checkItems Funcion:
	 * 
	 * @param item
	 * @param checked
	 */
	static void checkItems(TreeItem item, boolean checked) {
		item.setGrayed(false);
		item.setChecked(checked);
		TreeItem[] items = item.getItems();
		for (int i = 0; i < items.length; i++) {
			checkItems(items[i], checked);
		}
	}
}