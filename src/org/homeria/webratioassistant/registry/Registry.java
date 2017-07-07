package org.homeria.webratioassistant.registry;

// SINGLETON
public class Registry {
	private static Registry instance = null;

	private Registry() {
		// Exists only to defeat instantiation.
	}

	public static Registry getInstance() {
		if (instance == null) {
			instance = new Registry();
		}
		return instance;
	}
	
}