/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.webratioaux;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.ISiteView;
import com.webratio.ide.model.IWebMLElement;
import com.webratio.ide.model.IWebProject;
import com.webratio.ide.ui.properties.items.WebMLIdRefPropertyItem;
import com.webratio.ide.units.core.IUnitProperty;

public final class SiteViewProperty extends WebMLIdRefPropertyItem {
	protected IUnitProperty unitProp;

	public SiteViewProperty(IUnitProperty unitProp, int externalEditorsCount) {
		super(unitProp.get("attributeName"), unitProp.getLabel(), "Choose a site view:", true, externalEditorsCount);
		this.unitProp = unitProp;
	}

	@Override
	protected List<ISiteView> getAvailableElements(IMFElement elem, boolean multiple) {
		return getAvailableSiteViews(((IWebMLElement) elem).getWebProject(), this.unitProp);
	}

	public static final List<ISiteView> getAvailableSiteViews(IWebProject webProject, IUnitProperty unitProp) {
		List<ISiteView> siteViews = webProject.getWebModel().getSiteViewList();
		String type = StringUtils.defaultIfEmpty(unitProp.get("type"), "all");
		if (type.equals("all")) {
			return siteViews;
		}
		boolean searchProtected = type.equals("protected");
		List<ISiteView> filteredSiteViews = new ArrayList<ISiteView>(siteViews.size());
		for (ISiteView siteView : siteViews) {
			if (siteView.valueOf("@protected").equals("true") == searchProtected) {
				filteredSiteViews.add(siteView);
			}
		}
		return filteredSiteViews;
	}
}