package ca.corefacility.bioinformatics.irida.ria.web.files.dto;

/**
 *  Used by UI to encapsulate fastqc images data.
 */

public class FastQCImagesResponse {
	private byte[] perbaseChart;
	private byte[] persequenceChart;
	private byte[] duplicationlevelChart;
	private String fastQCVersion;

	public FastQCImagesResponse(byte[] perbaseChart, byte[] persequenceChart, byte[] duplicationlevelChart, String fastQCVersion) {
		this.perbaseChart = perbaseChart;
		this.persequenceChart = persequenceChart;
		this.duplicationlevelChart = duplicationlevelChart;
		this.fastQCVersion = fastQCVersion;
	}

	public byte[] getPerbaseChart() {
		return perbaseChart;
	}

	public void setPerbaseChart(byte[] perbaseChart) {
		this.perbaseChart = perbaseChart;
	}

	public byte[] getPersequenceChart() {
		return persequenceChart;
	}

	public void setPersequenceChart(byte[] persequenceChart) {
		this.persequenceChart = persequenceChart;
	}

	public byte[] getDuplicationlevelChart() {
		return duplicationlevelChart;
	}

	public void setDuplicationlevelChart(byte[] duplicationlevelChart) {
		this.duplicationlevelChart = duplicationlevelChart;
	}

	public String getFastQCVersion() {
		return fastQCVersion;
	}

	public void setFastQCVersion(String fastQCVersion) {
		this.fastQCVersion = fastQCVersion;
	}
}
