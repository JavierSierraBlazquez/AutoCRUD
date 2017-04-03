/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.plugin;

import org.eclipse.draw2d.geometry.Point;

import com.webratio.commons.mf.IMFElement;

public abstract class Evento {

	private IMFElement padre;
	private Point punto;

	public Evento(IMFElement padre, int x, int y) {

		this.padre = padre;
		this.punto = new Point(x, y);
	}

	public abstract IMFElement ejecutar();

	protected IMFElement getPadre() {
		return this.padre;
	}

	protected Point getPunto() {
		return this.punto;
	}

}
