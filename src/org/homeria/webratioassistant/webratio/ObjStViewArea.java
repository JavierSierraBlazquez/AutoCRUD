/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.webratio;

import java.util.List;

/**
 * 
 * @author carlos
 * 
 */
public class ObjStViewArea {

	private String name;
	private String type;
	private Boolean indNew;
	private List<ObjStViewArea> childList;

	public ObjStViewArea() {
		this.indNew = Boolean.FALSE;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsNew() {
		return this.indNew;
	}

	public void setIsNew(Boolean indNew) {
		this.indNew = indNew;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ObjStViewArea> getChildList() {
		return this.childList;
	}

	public void setChildList(List<ObjStViewArea> childList) {
		this.childList = childList;
	}

}
