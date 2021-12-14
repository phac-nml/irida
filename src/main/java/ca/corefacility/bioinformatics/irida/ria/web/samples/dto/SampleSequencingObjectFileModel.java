package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * Used to send sequencing object information with file sizes to the UI
 */
public class SampleSequencingObjectFileModel {
	private SequencingObject fileInfo;
	private String firstFileSize;
	private String secondFileSize;
	private String fileType;

	public SampleSequencingObjectFileModel(SequencingObject fileInfo, String firstFileSize, String secondFileSize) {
		this.fileInfo = fileInfo;
		this.firstFileSize = firstFileSize;
		this.secondFileSize = secondFileSize;
		this.fileType = "sequencingObject";
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

	public String getSecondFileSize() {
		return secondFileSize;
	}

	public void setSecondFileSize(String secondFileSize) {
		this.secondFileSize = secondFileSize;
	}

	public String getFileType() {
		return fileType;
	}

}
