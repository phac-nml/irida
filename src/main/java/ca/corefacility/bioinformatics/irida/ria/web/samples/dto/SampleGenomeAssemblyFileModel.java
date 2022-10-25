package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;

/**
 * Used to send genome assembly object information with file size to the UI
 */
public class SampleGenomeAssemblyFileModel {
	private GenomeAssembly fileInfo;
	private String firstFileSize;
	private String fileType;

	public SampleGenomeAssemblyFileModel(GenomeAssembly fileInfo, String firstFileSize) {
		this.fileInfo = fileInfo;
		this.firstFileSize = firstFileSize;
		this.fileType = "assembly";
	}

	public GenomeAssembly getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(GenomeAssembly fileInfo) {
		this.fileInfo = fileInfo;
	}

	public String getFirstFileSize() {
		return firstFileSize;
	}

	public void setFirstFileSize(String firstFileSize) {
		this.firstFileSize = firstFileSize;
	}

	public String getFileType() {
		return fileType;
	}
}
