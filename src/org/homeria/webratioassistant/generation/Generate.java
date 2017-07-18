package org.homeria.webratioassistant.generation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.transform.TransformerException;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.homeria.webratioassistant.elements.DataFlow;
import org.homeria.webratioassistant.elements.EntryUnit;
import org.homeria.webratioassistant.elements.Link;
import org.homeria.webratioassistant.elements.NormalNavigationFlow;
import org.homeria.webratioassistant.elements.Page;
import org.homeria.webratioassistant.elements.Unit;
import org.homeria.webratioassistant.elements.UnitOutsidePage;
import org.homeria.webratioassistant.elements.WebRatioElement;
import org.homeria.webratioassistant.registry.Registry;
import org.homeria.webratioassistant.webratio.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISiteView;

public final class Generate {

	private Queue<WebRatioElement> pages;
	private List<Unit> units;
	private List<Link> links;
	private List<IMFElement> siteViewsAreas;
	private Map<IRelationshipRole, IAttribute> relshipsSelected;

	private Queue<WebRatioElement> allElemPreprocessed;
	private IMFElement currentParent;

	private ProgressBar progressBar;
	private Label elemLabel;
	int progress;

	Map<String, IMFElement> createdElements;

	public Generate(Queue<WebRatioElement> pages, List<Unit> units, List<Link> links, List<IMFElement> siteViewsAreas,
			Map<IRelationshipRole, IAttribute> relshipsSelected) {

		this.pages = pages;
		this.units = units;
		this.links = links;
		this.siteViewsAreas = siteViewsAreas;
		this.relshipsSelected = relshipsSelected;

		this.allElemPreprocessed = new LinkedList<WebRatioElement>();
		this.currentParent = null;
		this.createdElements = new HashMap<String, IMFElement>();

		this.progress = 0;
		this.preprocess();
	}

	private void preprocess() {
		Point coords;

		for (IMFElement parent : this.siteViewsAreas) {
			coords = new Point(0, 0);
			// obtenemos las coordenadas del elemento más a la derecha para no superponer unidades

			if (parent instanceof ISiteView) {
				Utilities.switchSiteView((ISiteView) parent);
				coords = Utilities.findGap();
			}

			for (WebRatioElement page : this.pages) {
				WebRatioElement pageCopy = page.getCopy();

				if (pageCopy instanceof Page) {
					((Page) pageCopy).setParent(parent);
					((Page) pageCopy).addToCurrentPosition(coords);
				}

				this.allElemPreprocessed.add(pageCopy);
			}

			for (Unit unit : this.units) {
				Unit unitCopy = (Unit) unit.getCopy();

				if (unitCopy instanceof EntryUnit) {
					((EntryUnit) unitCopy).setRelshipsSelected(this.relshipsSelected);

				} else if (unitCopy instanceof UnitOutsidePage) {
					((UnitOutsidePage) unitCopy).setParent(parent);
					((UnitOutsidePage) unitCopy).addToCurrentPosition(coords);
				}

				this.allElemPreprocessed.add(unitCopy);
			}

			for (Link link : this.links) {
				Link linkCopy = (Link) link.getCopy();

				if (linkCopy instanceof NormalNavigationFlow)
					((NormalNavigationFlow) linkCopy).setRelshipsSelected(this.relshipsSelected);

				if (linkCopy instanceof DataFlow)
					((DataFlow) linkCopy).setRelshipsSelected(this.relshipsSelected);

				this.allElemPreprocessed.add(linkCopy);
			}
		}
	}

	public boolean next() throws TransformerException {
		if (this.allElemPreprocessed.isEmpty()) {
			// End
			Registry.getInstance().saveToFile();
			return false;

		} else {
			WebRatioElement element = this.allElemPreprocessed.poll();

			if (element instanceof Page) {
				IMFElement parentElement = ((Page) element).getParent();

				if (parentElement instanceof IArea)
					while (!(parentElement instanceof ISiteView))
						parentElement = parentElement.getParentElement();

				if (this.currentParent != parentElement && (parentElement instanceof ISiteView)) {
					this.createdElements = new HashMap<String, IMFElement>();
					this.currentParent = parentElement;

					if (parentElement instanceof ISiteView) {
						Utilities.switchSiteView((ISiteView) parentElement);

						// Register Sv
						Registry.getInstance().addSiteView(parentElement.getFinalId(), parentElement.getQName().getName());
					}
				}
			}
			IMFElement elementGenerated = element.generate(this.createdElements);
			this.createdElements.put(element.getId(), elementGenerated);

			// Register element
			Registry.getInstance().addElement(element.getClass().getSimpleName(), elementGenerated.getFinalId());

			// Update UI
			this.updateUI();

			return true;
		}
	}

	public void end() throws TransformerException {
		while (this.next())
			;
	}

	public void setUIelements(ProgressBar progressBar, Label nextElemLabel) {
		this.progress = 0;
		this.progressBar = progressBar;
		this.progressBar.setMaximum(this.allElemPreprocessed.size());
		this.progressBar.setSelection(this.progress);

		this.elemLabel = nextElemLabel;
		nextElemLabel.setText(this.allElemPreprocessed.peek().getClass().getSimpleName());
	}

	private void updateUI() {
		this.progressBar.setSelection(++this.progress);

		String nextElement;
		if (this.allElemPreprocessed.isEmpty())
			nextElement = "Finish";
		else
			nextElement = this.allElemPreprocessed.peek().getClass().getSimpleName();
		this.elemLabel.setText(nextElement);

		this.elemLabel.pack(true);
		this.elemLabel.getParent().layout(true, true);
	}
}
