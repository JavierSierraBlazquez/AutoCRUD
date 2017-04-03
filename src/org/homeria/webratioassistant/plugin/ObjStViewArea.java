/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.plugin;

import java.util.List;

/**
 * 
 * @author carlos
 *
 */
public class ObjStViewArea {

	private String nombre;
	private String tipo;
	private Boolean indNuevo;
	private List listHijos;

	public void ObjStViewArea() {
		this.indNuevo = Boolean.FALSE;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Boolean getEsNuevo() {
		return indNuevo;
	}

	public void setEsNuevo(Boolean indNuevo) {
		this.indNuevo = indNuevo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public List getListHijos() {
		return listHijos;
	}

	public void setListHijos(List listHijos) {
		this.listHijos = listHijos;
	}

}
