package org.athmis.wmoptimisation.algorithm;

public class OptimizationResult {

	private String fileName;
	private String generatorName;
	private double meanAreaSource;
	private int noChangeSetsSource;
	private double meanAreaSourceOptimized;
	private int noChangeSetsSourceOptimized;

	public OptimizationResult(String fileName, String generatorName) {
		this.fileName = fileName;
		this.generatorName = generatorName;
	}

	public void setMeanAreaSource(double meanArea) {
		this.meanAreaSource = meanArea;
	}

	public void setNoChangeSetsSource(int noChangeSets) {
		this.noChangeSetsSource = noChangeSets;
	}

	public void setMeanAreaOptimized(double meanArea) {
		this.meanAreaSourceOptimized = meanArea;
	}

	public void setNoChangeSetsOptimized(int noChangeSets) {
		this.noChangeSetsSourceOptimized = noChangeSets;
	}

	@Override
	public String toString() {
		String result;

		result = fileName + " with " + generatorName;
		result += "\n*** Changes ***";
		result += "\nno changes = " + noChangeSetsSource;
		result += "\nno changes opt = " + noChangeSetsSourceOptimized;
		result += "\nchange = " + noChangeSetsChange();
		result += "\n*** Area ***";
		result += "\nmean area = " + meanAreaSource;
		result += "\nmean area opt = " + meanAreaSourceOptimized;
		result += "\nchange = " + meanAreaChange();

		return result;
	}

	public String noChangeSetsChange() {
		double result;

		result = Math.abs(noChangeSetsSource - noChangeSetsSourceOptimized) * 100.0;
		result = result / noChangeSetsSource;

		return String.valueOf(result);
	}

	public String meanAreaChange() {
		double result;

		result = Math.abs(meanAreaSource - meanAreaSourceOptimized) * 100.0;
		result = result / meanAreaSourceOptimized;

		return String.valueOf(result);
	}
}
