package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;

/**
 * Used to send genome assembly object information with file size to the UI
 */
public class SampleGenomeAssemblyFileModel {
	private GenomeAssembly fileInfo;
	private String firstFileSize;

	public SampleGenomeAssemblyFileModel(GenomeAssembly fileInfo, String firstFileSize) {
		this.fileInfo = fileInfo;
		this.firstFileSize = firstFileSize;
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
}
