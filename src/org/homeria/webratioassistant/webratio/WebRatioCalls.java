/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.webratio;

import org.eclipse.draw2d.geometry.Point;

import com.webratio.commons.mf.IMFElement;

public abstract class WebRatioCalls {

	private IMFElement parent;
	private Point point;

	public WebRatioCalls(IMFElement parent, int x, int y) {

		this.parent = parent;
		this.point = new Point(x, y);
	}

	public abstract IMFElement execute();

	protected IMFElement getParent() {
		return this.parent;
	}

	protected Point getPoint() {
		return this.point;
	}

}
