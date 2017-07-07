package org.homeria.webratioassistant.registry;

import org.homeria.webratioassistant.plugin.Utilities;

// SINGLETON
public class Registry {
	private static Registry instance = null;
	// absolute pattern folder path
	private String path;
	private String pattId;
	private String pattName;

	private Registry(String path) {
		this.path = path;
	}

	public static Registry getInstance() {
		if (instance == null) {
			instance = new Registry(Utilities.getPatternsPath());
		}
		return instance;
	}

	public void setPatternData(String id, String name) {
		this.pattId = id;
		this.pattName = name;
	}

	public void setSiteView(String finalId) {
		// TODO Auto-generated method stub
		//FIXME voy por aqui
	}

}