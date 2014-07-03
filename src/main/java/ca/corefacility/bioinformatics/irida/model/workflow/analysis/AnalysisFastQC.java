package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

/**
 * Specific implementation of {@link Analysis} for storing properties created by
 * FastQC.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
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

	public AnalysisFastQC() {
		super();
	}

	/**
	 * Box and whisker plot showing per-base quality scores as a PNG-formatted
	 * image in a byte array.
	 * 
	 * @return
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
	 * @return
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
	 * @return
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
}
