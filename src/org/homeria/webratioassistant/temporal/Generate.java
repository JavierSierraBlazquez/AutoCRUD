package org.homeria.webratioassistant.temporal;

import org.eclipse.draw2d.geometry.Point;
import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaPagina;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;
import org.homeria.webratioassistant.plugin.EventoNuevoLink;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IContentUnit;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IPage;
import com.webratio.ide.model.ISiteView;

public abstract class Generate {

	public static IPage page(ISiteView sv, String name) {
		Point posicion = Utilities.buscarHueco();
		int x = posicion.x;
		int y = posicion.y;

		Evento evento = new EventoNuevaPagina(sv, x, y, name);
		return (IPage) evento.ejecutar();
	}

	public static IContentUnit powerIndexUnit(IMFElement parent, ContentUnit contentUnit, IEntity entity) {
		Point posicion = Utilities.buscarHueco();
		int x = posicion.x;
		int y = posicion.y;

		Evento evento = new EventoNuevaUnit(parent, contentUnit.getType(), x, y, contentUnit.getName(), entity);

		IContentUnit pIndexUnit = (IContentUnit) evento.ejecutar();
		// Se añaden los atributos
		Utilities.setAttribute(pIndexUnit, "displayAttributes", contentUnit.getSelectedAttributes(entity));
		return pIndexUnit;
	}

	public static IContentUnit dataUnit(IMFElement parent, ContentUnit contentUnit, IEntity entity) {
		Point posicion = Utilities.buscarHueco();
		int x = posicion.x;
		int y = posicion.y;

		Evento evento = new EventoNuevaUnit(parent, contentUnit.getType(), x, y, contentUnit.getName(), entity);

		IContentUnit dataUnit = (IContentUnit) evento.ejecutar();
		// Se añaden los atributos
		Utilities.setAttribute(dataUnit, "displayAttributes", contentUnit.getSelectedAttributes(entity));
		return dataUnit;
	}

	public static void normalLink(String name, IMFElement source, IMFElement target) {
		Evento evento = new EventoNuevoLink(name, source, target, "normal");
		evento.ejecutar();
	}
}
