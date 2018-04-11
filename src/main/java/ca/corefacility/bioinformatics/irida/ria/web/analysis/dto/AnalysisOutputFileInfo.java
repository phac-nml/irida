package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.List;

/**
 * DTO for {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile} text contents
 */
public class AnalysisOutputFileInfo {
	/**
	 * {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile#id}
	 */
	private Long id;
	/**
	 * {@link ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission#id}
	 */
	private Long analysisSubmissionId;
	/**
	 * {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis#id}
	 */
	private Long analysisId;
	/**
	 * {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile} filename
	 */
	private String filename;
	/**
	 * {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile} file extension
	 */
	private String fileExt;
	/**
	 * {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile} file size in bytes
	 */
	private Long fileSizeBytes;
	/**
	 * {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.ToolExecution#toolName}
	 */
	private String toolName;
	/**
	 * {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.ToolExecution#toolVersion}
	 */
	private String toolVersion;
	/**
	 * {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile} output name
	 */
	private String outputName;
	/**
	 * Text read from file
	 */
	private String text;
	/**
	 * Number of bytes to read at once
	 */
	private Long chunk;
	/**
	 * User-specified byte position to seek to and start reading lines or bytes
	 */
	private Long startSeek;
	/**
	 * {@link java.io.RandomAccessFile} file pointer; file byte position to which file was read
	 */
	private Long filePointer;
	/**
	 * Lines read from the {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile}
	 */
	private List<String> lines;
	/**
	 * First line from a tabular file which is expected to contain field headers
	 */
	private String firstLine;
	/**
	 * Number of lines to read from tabular file
	 */
	private Long limit;
	/**
	 * Line to start reading from in tabular file
	 */
	private Long start;
	/**
	 * Line to read until in tabular file
	 */
	private Long end;
	/**
	 * Error message if any
	 */
	private String error;

	public AnalysisOutputFileInfo() {
		this.id = null;
		this.analysisSubmissionId = null;
		this.analysisId = null;
		this.filename = null;
		this.fileExt = null;
		this.fileSizeBytes = null;
		this.toolName = null;
		this.toolVersion = null;
		this.outputName = null;
		this.text = null;
		this.chunk = null;
		this.startSeek = null;
		this.filePointer = null;
		this.lines = null;
		this.firstLine = null;
		this.limit = null;
		this.start = null;
		this.end = null;
		this.error = null;
	}

	@Override
	public String toString() {
		return "AnalysisOutputFileInfo{" + "id=" + id + ", analysisSubmissionId=" + analysisSubmissionId
				+ ", analysisId=" + analysisId + ", filename='" + filename + '\'' + ", fileExt='" + fileExt + '\''
				+ ", fileSizeBytes=" + fileSizeBytes + ", toolName='" + toolName + '\'' + ", toolVersion='"
				+ toolVersion + '\'' + ", outputName='" + outputName + '\'' + ", text='" + text + '\'' + ", chunk="
				+ chunk + ", startSeek=" + startSeek + ", filePointer=" + filePointer + ", lines=" + lines
				+ ", firstLine='" + firstLine + '\'' + ", limit=" + limit + ", start=" + start + ", end=" + end
				+ ", error='" + error + '\'' + '}';
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAnalysisSubmissionId() {
		return analysisSubmissionId;
	}

	public void setAnalysisSubmissionId(Long analysisSubmissionId) {
		this.analysisSubmissionId = analysisSubmissionId;
	}

	public Long getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(Long analysisId) {
		this.analysisId = analysisId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

	public Long getFileSizeBytes() {
		return fileSizeBytes;
	}

	public void setFileSizeBytes(Long fileSizeBytes) {
		this.fileSizeBytes = fileSizeBytes;
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public String getToolVersion() {
		return toolVersion;
	}

	public void setToolVersion(String toolVersion) {
		this.toolVersion = toolVersion;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getChunk() {
		return chunk;
	}

	public void setChunk(Long chunk) {
		this.chunk = chunk;
	}

	public Long getStartSeek() {
		return startSeek;
	}

	public void setStartSeek(Long startSeek) {
		this.startSeek = startSeek;
	}

	public Long getFilePointer() {
		return filePointer;
	}

	public void setFilePointer(Long filePointer) {
		this.filePointer = filePointer;
	}

	public List<String> getLines() {
		return lines;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public Long getEnd() {
		return end;
	}

	public void setEnd(Long end) {
		this.end = end;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getFirstLine() {
		return firstLine;
	}

	public void setFirstLine(String firstLine) {
		this.firstLine = firstLine;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}
}
