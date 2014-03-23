package org.athmis.wmoptimisation.algorithm;

public class OptimizationResult {

	private String fileName;
	private String generatorName;
	private double meanAreaSource;
	private int numberChangeSetsSource;
	private double meanAreaOptimized;
	private int numberChangeSetsOptimized;
	private int numberNodesSource;
	private int numberNodesOptimized;

	public OptimizationResult(String fileName, String generatorName) {
		this.fileName = fileName;
		this.generatorName = generatorName;
	}

	public void setMeanAreaSource(double meanArea) {
		this.meanAreaSource = meanArea;
	}

	public void setNoChangeSetsSource(int noChangeSets) {
		this.numberChangeSetsSource = noChangeSets;
	}

	public void setMeanAreaOptimized(double meanArea) {
		this.meanAreaOptimized = meanArea;
	}

	public void setNoChangeSetsOptimized(int noChangeSets) {
		this.numberChangeSetsOptimized = noChangeSets;
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

	public String numberChangeSetsChange() {
		double result;

		result = (numberChangeSetsOptimized - numberChangeSetsSource) * 100.0;
		result = result / numberChangeSetsSource;

		return String.valueOf(result) + " %";
	}

	public String meanAreaChange() {
		double result;

		result = (meanAreaOptimized - meanAreaSource) * 100.0;
		result = result / meanAreaSource;

		return String.valueOf(result) + " %";
	}

	public void setNumberNodesSource(int nodes) {
		this.numberNodesSource = nodes;
	}

	public void setNumberNodesOptimized(int nodes) {
		this.numberNodesOptimized = nodes;
	}
}
