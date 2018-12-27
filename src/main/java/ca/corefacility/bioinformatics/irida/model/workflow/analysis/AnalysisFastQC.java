package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Specific implementation of {@link Analysis} for storing properties created by FastQC.
 */
@Entity
@Table(name = "analysis_fastqc")
public class AnalysisFastQC extends Analysis {

	@NotNull
	private final String fastqcVersion;
	@NotNull
	private final String fileType;
	@NotNull
	private final String encoding;
	@NotNull
	private final Integer totalSequences;
	@NotNull
	private final Integer filteredSequences;
	@NotNull
	private final Long totalBases;
	@NotNull
	private final Integer minLength;
	@NotNull
	private final Integer maxLength;
	@NotNull
	private final Short gcContent;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private final Set<OverrepresentedSequence> overrepresentedSequences;

	/**
	 * Required for hibernate, should not be used anywhere else, so private.
	 */
	protected AnalysisFastQC() {
		super();
		this.fileType = null;
		this.encoding = null;
		this.totalSequences = null;
		this.filteredSequences = null;
		this.totalBases = null;
		this.minLength = null;
		this.maxLength = null;
		this.gcContent = null;
		this.overrepresentedSequences = null;
		this.fastqcVersion = null;

		this.setAnalysisType(BuiltInAnalysisTypes.FASTQC);
	}

	public AnalysisFastQC(final AnalysisFastQCBuilder builder) {
		super(builder.executionManagerAnalysisId, builder.images, builder.description, builder.additionalProperties);
		this.fileType = builder.fileType;
		this.encoding = builder.encoding;
		this.totalSequences = builder.totalSequences;
		this.filteredSequences = builder.filteredSequences;
		this.totalBases = builder.totalBases;
		this.minLength = builder.minLength;
		this.maxLength = builder.maxLength;
		this.gcContent = builder.gcContent;
		this.overrepresentedSequences = builder.overrepresentedSequences;
		this.fastqcVersion = builder.fastqcVersion;

		this.setAnalysisType(BuiltInAnalysisTypes.FASTQC);
	}

	/**
	 * get an AnalysisFastQCBuilder
	 *
	 * @return an AnalysisFastQCBuilder
	 */
	public static AnalysisFastQCBuilder builder() {
		return new AnalysisFastQCBuilder();
	}

	/**
	 * Get a AnalysisFastQCBuilder sloppy builder that doesn't check fields
	 *
	 * @return a AnalysisFastQCBuilder
	 */
	public static AnalysisFastQCBuilder sloppyBuilder() {
		return new AnalysisFastQCBuilder(false);
	}

	/**
	 * Builder for creating instances of {@link AnalysisFastQC}. This builder can optionally check if all required
	 * fields are set.
	 */
	public static class AnalysisFastQCBuilder {
		private String fastqcVersion;
		private String fileType;
		private String encoding;
		private Integer totalSequences;
		private Integer filteredSequences;
		private Long totalBases;
		private Integer minLength;
		private Integer maxLength;
		private Short gcContent;
		private AnalysisOutputFile perBaseQualityScoreChart;
		private AnalysisOutputFile perSequenceQualityScoreChart;
		private AnalysisOutputFile duplicationLevelChart;
		private Set<OverrepresentedSequence> overrepresentedSequences;
		private AnalysisOutputFile fastQCReport;
		private String description;
		private String executionManagerAnalysisId;
		private Map<String, String> additionalProperties;
		private Map<String, AnalysisOutputFile> images;

		private final boolean enforceRequiredFieldCheck;

		/**
		 * Create an instance of AnalysisFastQCBuilder, and enforce checking required fields by default.
		 */
		public AnalysisFastQCBuilder() {
			this.enforceRequiredFieldCheck = true;
		}

		/**
		 * Create an instance of AnalysisFastQCBuilder, only enforcing checking required fields if requested.
		 *
		 * @param enforceRequiredFieldCheck if true, the `build()` method will throw an exception if all required fields
		 *                                  are not set; if false, the `build()` method will not. NOTE: This setting
		 *                                  does **NOT** affect database-level constraints.
		 */
		public AnalysisFastQCBuilder(final boolean enforceRequiredFieldCheck) {
			this.enforceRequiredFieldCheck = enforceRequiredFieldCheck;
		}

		/**
		 * set the additionalProperties
		 *
		 * @param additionalProperties the additionalProperties
		 * @return the builder
		 */
		public AnalysisFastQCBuilder additionalProperties(final Map<String, String> additionalProperties) {
			this.additionalProperties = additionalProperties;
			return this;
		}

		/**
		 * set the executionManagerAnalysisId
		 *
		 * @param executionManagerAnalysisId the executionManagerAnalysisId
		 * @return the builder
		 */
		public AnalysisFastQCBuilder executionManagerAnalysisId(final String executionManagerAnalysisId) {
			this.executionManagerAnalysisId = executionManagerAnalysisId;
			return this;
		}

		/**
		 * Set the fastqc version used in the analysis
		 *
		 * @param fastqcVersion the version of fastqc used
		 * @return the builder
		 */
		public AnalysisFastQCBuilder fastqcVersion(String fastqcVersion) {
			this.fastqcVersion = fastqcVersion;
			return this;
		}

		/**
		 * set the description
		 *
		 * @param description the description
		 * @return the builder
		 */
		public AnalysisFastQCBuilder description(final String description) {
			this.description = description;
			return this;
		}

		/**
		 * set the overrepresentedSequences
		 *
		 * @param overrepresentedSequences the overrepresentedSequences
		 * @return the builder
		 */
		public AnalysisFastQCBuilder overrepresentedSequences(
				final Set<OverrepresentedSequence> overrepresentedSequences) {
			this.overrepresentedSequences = overrepresentedSequences;
			return this;
		}

		/**
		 * set the duplicationLevelChart
		 *
		 * @param duplicationLevelChart the duplicationLevelChart
		 * @return the builder
		 */
		public AnalysisFastQCBuilder duplicationLevelChart(final AnalysisOutputFile duplicationLevelChart) {
			this.duplicationLevelChart = duplicationLevelChart;
			return this;
		}

		/**
		 * Set the perSequenceQualityScoreChart
		 *
		 * @param perSequenceQualityScoreChart the perSequenceQualityScoreChart
		 * @return the builder
		 */
		public AnalysisFastQCBuilder perSequenceQualityScoreChart(
				final AnalysisOutputFile perSequenceQualityScoreChart) {
			this.perSequenceQualityScoreChart = perSequenceQualityScoreChart;
			return this;
		}

		/**
		 * set the perBaseQualityScoreChart
		 *
		 * @param perBaseQualityScoreChart the perBaseQualityScoreChart
		 * @return the builder
		 */
		public AnalysisFastQCBuilder perBaseQualityScoreChart(final AnalysisOutputFile perBaseQualityScoreChart) {
			this.perBaseQualityScoreChart = perBaseQualityScoreChart;
			return this;
		}

		/**
		 * Set the gcContent
		 *
		 * @param gcContent the gcContent
		 * @return the builder
		 */
		public AnalysisFastQCBuilder gcContent(final Short gcContent) {
			this.gcContent = gcContent;
			return this;
		}

		/**
		 * Set the maxLength
		 *
		 * @param maxLength the maxLength
		 * @return the builder
		 */
		public AnalysisFastQCBuilder maxLength(final Integer maxLength) {
			this.maxLength = maxLength;
			return this;
		}

		/**
		 * Set the minLength
		 *
		 * @param minLength the minLength
		 * @return the builder
		 */
		public AnalysisFastQCBuilder minLength(final Integer minLength) {
			this.minLength = minLength;
			return this;
		}

		/**
		 * Set the totalBases
		 *
		 * @param totalBases the totalBases
		 * @return the builder
		 */
		public AnalysisFastQCBuilder totalBases(final Long totalBases) {
			this.totalBases = totalBases;
			return this;
		}

		/**
		 * Set the filteredSequences
		 *
		 * @param filteredSequences the filteredSequences
		 * @return the builder
		 */
		public AnalysisFastQCBuilder filteredSequences(final Integer filteredSequences) {
			this.filteredSequences = filteredSequences;
			return this;
		}

		/**
		 * Set the total sequences
		 *
		 * @param totalSequences the totalsequences
		 * @return the builder
		 */
		public AnalysisFastQCBuilder totalSequences(final Integer totalSequences) {
			this.totalSequences = totalSequences;
			return this;
		}

		/**
		 * Set the filetype
		 *
		 * @param fileType the filetype
		 * @return the builder
		 */
		public AnalysisFastQCBuilder fileType(final String fileType) {
			this.fileType = fileType;
			return this;
		}

		/**
		 * Set the encoding
		 *
		 * @param encoding the encoding
		 * @return the builder
		 */
		public AnalysisFastQCBuilder encoding(final String encoding) {
			this.encoding = encoding;
			return this;
		}

		/**
		 * Build an AnalysisFastQC with the currently set parameters
		 *
		 * @return the new AnalysisFastQC
		 */
		public AnalysisFastQC build() {
			images = new HashMap<>();
			images.put("perBaseQualityScoreChart", perBaseQualityScoreChart);
			images.put("perSequenceQualityScoreChart", perSequenceQualityScoreChart);
			images.put("duplicationLevelChart", duplicationLevelChart);

			/*if (enforceRequiredFieldCheck) {
				final Field[] fields = AnalysisFastQCBuilder.class.getDeclaredFields();
				for (final Field field : fields) {
					try {
						if (field.getName().equals("enforceRequiredFieldCheck")) {
							continue;
						}
						
						final Field analysisFastQCField = ReflectionUtils.findField(AnalysisFastQC.class,
								field.getName());
						if (analysisFastQCField.getAnnotation(NotNull.class) != null && field.get(this) == null) {
							throw new IllegalStateException("The field AnalaysisFastQC." + field.getName()
									+ " is not nullable.");
						}
					} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
						throw new IllegalStateException("The field AnalaysisFastQC." + field.getName()
								+ " is not accessible.", e);
					}
				}
			}*/
			return new AnalysisFastQC(this);
		}
	}

	/**
	 * Box and whisker plot showing per-base quality scores as a PNG-formatted image in a byte array.
	 *
	 * @return a PNG-formatted byte array for per-base quality score.
	 */
	@JsonIgnore
	public byte[] getPerBaseQualityScoreChart() throws IOException {
		return getBytesForFile("perBaseQualityScoreChart");
	}

	/**
	 * Line chartshowing per-sequence quality scores as a PNG-formatted image in a byte array.
	 *
	 * @return a PNG-formatted byte array for per-sequence quality score.
	 */
	@JsonIgnore
	public byte[] getPerSequenceQualityScoreChart() throws IOException {
		return getBytesForFile("perSequenceQualityScoreChart");
	}

	/**
	 * Line chartshowing duplication-level as a PNG-formatted image in a byte array.
	 *
	 * @return a PNG-formatted byte array for duplication levels.
	 */
	@JsonIgnore
	public byte[] getDuplicationLevelChart() throws IOException {
		return getBytesForFile("duplicationLevelChart");
	}

	public String getFastqcVersion() {
		return fastqcVersion;
	}

	public String getFileType() {
		return fileType;
	}

	public String getEncoding() {
		return encoding;
	}

	public Integer getTotalSequences() {
		return totalSequences;
	}

	public Integer getFilteredSequences() {
		return filteredSequences;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public Short getGcContent() {
		return gcContent;
	}

	public Long getTotalBases() {
		return totalBases;
	}

	public Set<OverrepresentedSequence> getOverrepresentedSequences() {
		return ImmutableSet.copyOf(overrepresentedSequences);
	}

	/**
	 * Read the bytes for a fastqc image
	 *
	 * @param key the file key to read
	 * @return the bytes for the file
	 * @throws IOException if the file couldn't be read
	 */
	private byte[] getBytesForFile(String key) throws IOException {
		AnalysisOutputFile chart = getAnalysisOutputFile(key);
		byte[] bytes = Files.readAllBytes(chart.getFile());
		return bytes;
	}
}
