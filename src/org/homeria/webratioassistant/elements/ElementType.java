package org.homeria.webratioassistant.elements;

public abstract class ElementType {
	public static final String PAGE = "Page";
	
	public static final String POWER_INDEX_UNIT = "PowerIndexUnit";
	public static final String DATA_UNIT = "DataUnit";
	public static final String MULTI_MESSAGE_UNIT = "MultiMessageUnit";
	public static final String ENTRY_UNIT = "EntryUnit";
	public static final String CREATE_UNIT = "CreateUnit";
	public static final String UPDATE_UNIT = "ModifyUnit";
	public static final String DELETE_UNIT = "DeleteUnit";
	public static final String CONNECT_UNIT = "ConnectUnit";
	public static final String SELECTOR_UNIT = "SelectorUnit";

	public static final String NORMAL_NAVIGATION_FLOW = "NormalNavigationFlow";
	public static final String DATA_FLOW = "DataFlow";
	public static final String OK_LINK = "OKLink";
	public static final String KO_LINK = "KOLink";

	// DATA FLOW TYPES:
	public static final String DATAFLOW_AUTOMATIC = "automatic";
	public static final String DATAFLOW_PRELOAD = "preload";
	public static final String DATAFLOW_ENTRY_TO_CONNECT = "entryToConnect";
	public static final String DATAFLOW_UNIT_TO_ENTRY = "unitToEntry";
	
	// NORMAL NAVIGATION FLOW TYPES:
	public static final String NORMALFLOW_ENTRY_TO_CREATE = "entryToCreate";
	public static final String NORMALFLOW_ENTRY_TO_MODIFY = "entryToModify";
	
}
