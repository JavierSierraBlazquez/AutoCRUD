package org.homeria.webratioassistant.elements;

public abstract class ElementType {
	public static final String PAGE = "Page";
	public static final String XOR_PAGE = "XOR";
	
	public static final String POWER_INDEX_UNIT = "PowerIndexUnit";
	public static final String DATA_UNIT = "DataUnit";
	public static final String MULTI_MESSAGE_UNIT = "MultiMessageUnit";
	public static final String ENTRY_UNIT = "EntryUnit";
	public static final String CREATE_UNIT = "CreateUnit";
	public static final String UPDATE_UNIT = "UpdateUnit";
	public static final String MODIFY_UNIT = "ModifyUnit";
	public static final String DELETE_UNIT = "DeleteUnit";
	public static final String CONNECT_UNIT = "ConnectUnit";
	public static final String DISCONNECT_UNIT = "DisconnectUnit";
	public static final String RECONNECT_UNIT = "ReconnectUnit";
	public static final String SELECTOR_UNIT = "SelectorUnit";
	public static final String NO_OP_CONTENT_UNIT = "NoOpContentUnit";
	public static final String IS_NOT_NULL_UNIT = "IsNotNullUnit";
	
	
	// ENTRY UNIT TYPES
	public static String ENTRYUNIT_PRELOADED = "preloaded";
	
	// SELECTOR UNIT TYPES
	public static String SELECTOR_KEYCONDITION = "keyCondition";
	public static String SELECTOR_ROLECONDITION = "roleCondition";
	
	// LINKS
	public static final String NORMAL_NAVIGATION_FLOW = "NormalNavigationFlow";
	public static final String DATA_FLOW = "DataFlow";
	public static final String OK_LINK = "OKLink";
	public static final String KO_LINK = "KOLink";

	// DATA FLOW TYPES:
	public static final String DATAFLOW_AUTOMATIC = "automatic";
	public static final String DATAFLOW_PRELOAD = "preload";
	public static final String DATAFLOW_ENTRY_TO_CONNECT = "entryToConnect";
	public static final String DATAFLOW_ENTRY_TO_RECONNECT = "entryToReconnect";
	public static final String DATAFLOW_UNIT_TO_ENTRY = "unitToEntry";
	public static final String DATAFLOW_UNIT_TO_ENTRY_ROLE = "unitToEntryRole";
	
	// NORMAL NAVIGATION FLOW TYPES:
	public static final String NORMALFLOW_FIXED_VALUE = "fixedValue";
	public static final String NORMALFLOW_IS_NOT_NULL = "isNotNull";
	
	// TYPES FOR DATA FLOW AND NORMAL NAVIGATION FLOW (BOTH)
	public static final String FLOW_ENTRY_TO_CREATE = "entryToCreate";
	public static final String FLOW_ENTRY_TO_UPDATE = "entryToUpdate";
	
	// OK/KO TYPES
	public static final String OK_LINK_NO_COUPLING = "noCoupling";
	public static final String KO_LINK_NO_COUPLING = "noCoupling";
	
}
