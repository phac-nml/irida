package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Used to send sequencing object information with file sizes to the UI
 */
public class SampleSequencingObjectFileModel {
	private SequencingObject fileInfo;
	private String firstFileSize;
	private String secondFileSize;
	private String fileType;
	private Set<QCEntry> qcEntries;
	private AnalysisSubmission automatedAssembly;

	public SampleSequencingObjectFileModel(SequencingObject fileInfo, String firstFileSize, String secondFileSize,
			Set<QCEntry> qcEntries) {
		this.fileInfo = fileInfo;
		this.firstFileSize = firstFileSize;
		this.secondFileSize = secondFileSize;
		this.fileType = "sequencingObject";
		this.qcEntries = qcEntries;
	}

	public SampleSequencingObjectFileModel(SequencingObject fileInfo, String firstFileSize, String secondFileSize,
			Set<QCEntry> qcEntries, AnalysisSubmission automatedAssembly) {
		this.fileInfo = fileInfo;
		this.firstFileSize = firstFileSize;
		this.secondFileSize = secondFileSize;
		this.fileType = "sequencingObject";
		this.qcEntries = qcEntries;
		this.automatedAssembly = automatedAssembly;
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

	public Set<QCEntry> getQcEntries() {
		return qcEntries;
	}

	public void setQcEntries(Set<QCEntry> qcEntries) {
		this.qcEntries = qcEntries;
	}

	public AnalysisSubmission getAutomatedAssembly() {
		return automatedAssembly;
	}

	public void setSubmission(AnalysisSubmission automatedAssembly) {
		this.automatedAssembly = automatedAssembly;
	}
}
