package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Specific implementation of {@link Analysis} for storing properties created by
 * FastQC.
 * 
 *
 */
@Entity
@Table(name = "analysis_fastqc")
@Audited
public class AnalysisFastQC extends Analysis {

	@NotNull
	private String fileType;
	@NotNull
	private String encoding;
	@NotNull
	private Integer totalSequences;
	@NotNull
	private Integer filteredSequences;
	@NotNull
	private Long totalBases;
	@NotNull
	private Integer minLength;
	@NotNull
	private Integer maxLength;
	@NotNull
	private Short gcContent;
	@NotNull
	@Lob
	private byte[] perBaseQualityScoreChart;
	@NotNull
	@Lob
	private byte[] perSequenceQualityScoreChart;
	@NotNull
	@Lob
	private byte[] duplicationLevelChart;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<OverrepresentedSequence> overrepresentedSequences;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private AnalysisOutputFile fastQCReport;


	/**
	 * Required for hibernate, should not be used anywhere else, so private.
	 */
	private AnalysisFastQC() {
		super(null, null);
	}

	public AnalysisFastQC(Set<SequenceFile> inputFiles, String executionManagerAnalysisId) {
		super(inputFiles, executionManagerAnalysisId);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<AnalysisOutputFile> getAnalysisOutputFiles() {
		return Sets.newHashSet(fastQCReport);
	}

	/**
	 * Box and whisker plot showing per-base quality scores as a PNG-formatted
	 * image in a byte array.
	 * 
	 * @return a PNG-formatted byte array for per-base quality score.
	 */
	public byte[] getPerBaseQualityScoreChart() {
		return perBaseQualityScoreChart;
	}

	public void setPerBaseQualityScoreChart(byte[] perBaseQualityScoreChart) {
		this.perBaseQualityScoreChart = perBaseQualityScoreChart;
	}

	/**
	 * Line chartshowing per-sequence quality scores as a PNG-formatted image in
	 * a byte array.
	 * 
	 * @return a PNG-formatted byte array for per-sequence quality score.
	 */
	public byte[] getPerSequenceQualityScoreChart() {
		return perSequenceQualityScoreChart;
	}

	public void setPerSequenceQualityScoreChart(byte[] perSequenceQualityScoreChart) {
		this.perSequenceQualityScoreChart = perSequenceQualityScoreChart;
	}

	/**
	 * Line chartshowing duplication-level as a PNG-formatted image in a byte
	 * array.
	 * 
	 * @return a PNG-formatted byte array for duplication levels.
	 */
	public byte[] getDuplicationLevelChart() {
		return duplicationLevelChart;
	}

	public void setDuplicationLevelChart(byte[] duplicationLevelChart) {
		this.duplicationLevelChart = duplicationLevelChart;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Integer getTotalSequences() {
		return totalSequences;
	}

	public void setTotalSequences(Integer totalSequences) {
		this.totalSequences = totalSequences;
	}

	public Integer getFilteredSequences() {
		return filteredSequences;
	}

	public void setFilteredSequences(Integer filteredSequences) {
		this.filteredSequences = filteredSequences;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public Short getGcContent() {
		return gcContent;
	}

	public void setGcContent(Short gcContent) {
		this.gcContent = gcContent;
	}

	public Long getTotalBases() {
		return totalBases;
	}

	public void setTotalBases(Long totalBases) {
		this.totalBases = totalBases;
	}

	public Set<OverrepresentedSequence> getOverrepresentedSequences() {
		return overrepresentedSequences;
	}

	public void setOverrepresentedSequences(Set<OverrepresentedSequence> overrepresentedSequences) {
		this.overrepresentedSequences = overrepresentedSequences;
	}

	public AnalysisOutputFile getFastQCReport() {
		return fastQCReport;
	}

	public void setFastQCReport(AnalysisOutputFile fastQCReport) {
		this.fastQCReport = fastQCReport;
	}
}
