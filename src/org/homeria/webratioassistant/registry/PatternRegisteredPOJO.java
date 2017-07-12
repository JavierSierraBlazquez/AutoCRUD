package org.homeria.webratioassistant.registry;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class PatternRegisteredPOJO {
	private String id;
	private int timesUsed;

	private SortedMap<String, Integer> svReg;
	private SortedMap<String, Integer> elementsReg;

	public PatternRegisteredPOJO() {
		this.id = "";
		this.timesUsed = 0;
		this.svReg = new TreeMap<String, Integer>();
		this.elementsReg = new TreeMap<String, Integer>();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getTimesUsed() {
		return this.timesUsed;
	}

	public void increaseTimesUsed() {
		this.timesUsed++;
	}

	public Map<String, Integer> getSvReg() {
		return this.svReg;
	}

	public Map<String, Integer> getElementsReg() {
		return this.elementsReg;
	}

	public void addSv(String svId) {
		Integer count;

		if (this.svReg.containsKey(svId))
			count = 1 + this.svReg.get(svId);
		else
			count = new Integer(1);

		this.svReg.put(svId, count);
	}

	public void addElement(String unitType) {
		Integer count;

		if (this.elementsReg.containsKey(unitType))
			count = 1 + this.elementsReg.get(unitType);
		else
			count = new Integer(1);

		this.elementsReg.put(unitType, count);
	}

	@Override
	public String toString() {
		//Arrays.sort(this.svReg.keySet().toArray());
		//Arrays.sort(this.elementsReg.keySet().toArray());
		String output = "";

		output += " - Pattern: \n";
		output += "\t" + this.id + " (" + this.timesUsed + ")\n";
		output += "\n";
		output += " - SiteViews: \n";

		for (String sv : this.svReg.keySet())
			output += "\t" + sv + " (" + this.svReg.get(sv) + ")\n";

		output += "\n";
		output += " - Elements: \n";

		for (String element : this.elementsReg.keySet())
			output += "\t" + element + " (" + this.elementsReg.get(element) + ")\n";

		return output;
	}
}
