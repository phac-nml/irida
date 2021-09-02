package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

public class SampleSequencingObjectFileModel {
	private SequencingObject fileInfo;
	private String firstFileSize;
	private String secondFile2Size;

	public SampleSequencingObjectFileModel(SequencingObject fileInfo, String firstFileSize, String secondFile2Size) {
		this.fileInfo = fileInfo;
		this.firstFileSize = firstFileSize;
		this.secondFile2Size = secondFile2Size;
	}

	public SequencingObject getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(SequencingObject fileInfo) {
		this.fileInfo = fileInfo;
	}

	public String getFirstFileSize() {
		return firstFileSize;
	}

	public void setFirstFileSize(String firstFileSize) {
		this.firstFileSize = firstFileSize;
	}

	public String getSecondFile2Size() {
		return secondFile2Size;
	}

	public void setSecondFile2Size(String secondFile2Size) {
		this.secondFile2Size = secondFile2Size;
	}
}
