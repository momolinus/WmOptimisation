package org.athmis.wmoptimisation.algorithm;

/**
 * An OptimizationResult object is an "utility object", which stores the attributes of an optimized
 * changeset like original mean area, optimized mean area similar attributes.
 *
 * @author @author Marcus Bleil, http://www.marcusbleil.de
 */
public class OptimizationResult {

	private String changesHeader;
	private String fileName;
	private String generatorName;
	private double meanAreaOptimized;
	private double meanAreaSource;
	private int numberChangeSetsOptimized;
	private int numberChangeSetsSource;
	private int numberNodesOptimized;
	private int numberNodesSource;
	private String optimizedChangesTable;
	private String originalChangesTable;

	/**
	 * @param fileName
	 *            the file name of the file with the change content
	 * @param generatorName
	 *            a name/description of the changeset generator
	 */
	public OptimizationResult(String fileName, String generatorName) {
		this.fileName = fileName;
		this.generatorName = generatorName;
	}

	/**
	 * Method sets the value of the table of the optimized changes.
	 *
	 * @param changeSetsAsStrTable
	 */
	public void appendOptimizedChanges(String changeSetsAsStrTable) {
		this.optimizedChangesTable = changeSetsAsStrTable;
	}

	public String getChangesHeader() {
		return changesHeader;
	}

	public String getOptimizedChangesTable() {
		return optimizedChangesTable;
	}

	public String getOriginalChangesTable() {
		return originalChangesTable;
	}

	public String meanAreaChange() {
		double result;

		result = (meanAreaOptimized - meanAreaSource) * 100.0;
		result = result / meanAreaSource;

		return String.valueOf(result) + " %";
	}

	public String numberChangeSetsChange() {
		double result;

		result = (numberChangeSetsOptimized - numberChangeSetsSource) * 100.0;
		result = result / numberChangeSetsSource;

		return String.valueOf(result) + " %";
	}

	public String oneRowHeader() {
		String result;

		result =
			"fileName;generatorName;meanAreaSource;meanAreaOptimized;numberChangeSetsSource;"
				+ "numberChangeSetsOptimized";

		return result;
	}

	public void setChangesHeader(String changeSetsAsStrTableHeader) {
		this.changesHeader = changeSetsAsStrTableHeader;
	}

	public void setMeanAreaOptimized(double meanArea) {
		this.meanAreaOptimized = meanArea;
	}

	public void setMeanAreaSource(double meanArea) {
		this.meanAreaSource = meanArea;
	}

	public void setNoChangeSetsOptimized(int noChangeSets) {
		this.numberChangeSetsOptimized = noChangeSets;
	}

	public void setNoChangeSetsSource(int noChangeSets) {
		this.numberChangeSetsSource = noChangeSets;
	}

	public void setNumberNodesOptimized(int nodes) {
		this.numberNodesOptimized = nodes;
	}

	public void setNumberNodesSource(int nodes) {
		this.numberNodesSource = nodes;
	}

	public void setOptimizedChangesTable(String optimizedChangesTable) {
		this.optimizedChangesTable = optimizedChangesTable;
	}

	public void setOriginalChangesAsTable(String changeSetsAsStrTable) {
		this.originalChangesTable = changeSetsAsStrTable;
	}

	public String toOneRow() {
		String result;

		result =
			fileName + ";" + generatorName + ";" + meanAreaSource + ";" + meanAreaOptimized + ";"
				+ numberChangeSetsSource + ";" + numberChangeSetsOptimized;

		return result;
	}

	@Override
	public String toString() {
		String result;

		result = fileName + " with " + generatorName;
		result += "\n*** Nodes ***";
		result += "\nnumber nodes = " + numberNodesSource;
		result += "\nnumber nodes opt = " + numberNodesOptimized;
		result += "\n*** Area ***";
		result += "\nmean area = " + meanAreaSource;
		result += "\nmean area opt = " + meanAreaOptimized;
		result += "\nchange = " + meanAreaChange();
		result += "\n*** ChangeSets ***";
		result += "\nnumber changesets = " + numberChangeSetsSource;
		result += "\nnumber changesets opt = " + numberChangeSetsOptimized;
		result += "\nchange = " + numberChangeSetsChange();

		return result;
	}
}
