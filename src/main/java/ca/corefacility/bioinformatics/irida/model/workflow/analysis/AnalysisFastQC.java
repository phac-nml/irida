package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.util.ReflectionUtils;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

import com.google.common.collect.ImmutableSet;

/**
 * Specific implementation of {@link Analysis} for storing properties created by
 * FastQC.
 * 
 *
 */
@Entity
@Table(name = "analysis_fastqc")
public class AnalysisFastQC extends Analysis {

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
	@NotNull
	@Lob
	private final byte[] perBaseQualityScoreChart;
	@NotNull
	@Lob
	private final byte[] perSequenceQualityScoreChart;
	@NotNull
	@Lob
	private final byte[] duplicationLevelChart;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private final Set<OverrepresentedSequence> overrepresentedSequences;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private final AnalysisOutputFile fastQCReport;

	/**
	 * Required for hibernate, should not be used anywhere else, so private.
	 */
	@SuppressWarnings("unused")
	private AnalysisFastQC() {
		super();
		this.fileType = null;
		this.encoding = null;
		this.totalSequences = null;
		this.filteredSequences = null;
		this.totalBases = null;
		this.minLength = null;
		this.maxLength = null;
		this.gcContent = null;
		this.perBaseQualityScoreChart = null;
		this.perSequenceQualityScoreChart = null;
		this.duplicationLevelChart = null;
		this.overrepresentedSequences = null;
		this.fastQCReport = null;
	}

	public AnalysisFastQC(final AnalysisFastQCBuilder builder) {
		super(builder.inputFiles, builder.executionManagerAnalysisId, builder.analysisOutputFilesMap,
				builder.description, builder.additionalProperties);
		this.fileType = builder.fileType;
		this.encoding = builder.encoding;
		this.totalSequences = builder.totalSequences;
		this.filteredSequences = builder.filteredSequences;
		this.totalBases = builder.totalBases;
		this.minLength = builder.minLength;
		this.maxLength = builder.maxLength;
		this.gcContent = builder.gcContent;
		this.perBaseQualityScoreChart = builder.perBaseQualityScoreChart;
		this.perSequenceQualityScoreChart = builder.perSequenceQualityScoreChart;
		this.duplicationLevelChart = builder.duplicationLevelChart;
		this.overrepresentedSequences = builder.overrepresentedSequences;
		this.fastQCReport = builder.fastQCReport;
	}

	public static AnalysisFastQCBuilder builder() {
		return new AnalysisFastQCBuilder();
	}

	public static AnalysisFastQCBuilder sloppyBuilder() {
		return new AnalysisFastQCBuilder(false);
	}

	/**
	 * Builder for creating instances of {@link AnalysisFastQC}. This builder
	 * can optionally check if all required fields are set.
	 * 
	 * @author Franklin Bristow franklin.bristow@phac-aspc.gc.ca
	 *
	 */
	public static class AnalysisFastQCBuilder {
		private String fileType;
		private String encoding;
		private Integer totalSequences;
		private Integer filteredSequences;
		private Long totalBases;
		private Integer minLength;
		private Integer maxLength;
		private Short gcContent;
		private byte[] perBaseQualityScoreChart;
		private byte[] perSequenceQualityScoreChart;
		private byte[] duplicationLevelChart;
		private Set<OverrepresentedSequence> overrepresentedSequences;
		private AnalysisOutputFile fastQCReport;
		private String description;
		private String executionManagerAnalysisId;
		private Map<String, String> additionalProperties;
		private Set<SequenceFile> inputFiles;
		private Map<String, AnalysisOutputFile> analysisOutputFilesMap;

		private final boolean enforceRequiredFieldCheck;

		/**
		 * Create an instance of AnalysisFastQCBuilder, and enforce checking
		 * required fields by default.
		 */
		public AnalysisFastQCBuilder() {
			this.enforceRequiredFieldCheck = true;
		}

		/**
		 * Create an instance of AnalysisFastQCBuilder, only enforcing checking
		 * required fields if requested.
		 * 
		 * @param enforceRequiredFieldCheck
		 *            if true, the `build()` method will throw an exception if
		 *            all required fields are not set; if false, the `build()`
		 *            method will not. NOTE: This setting does **NOT** affect
		 *            database-level constraints.
		 */
		public AnalysisFastQCBuilder(final boolean enforceRequiredFieldCheck) {
			this.enforceRequiredFieldCheck = enforceRequiredFieldCheck;
		}

		public AnalysisFastQCBuilder analysisOutputFilesMap(final Map<String, AnalysisOutputFile> analysisOutputFilesMap) {
			this.analysisOutputFilesMap = analysisOutputFilesMap;
			return this;
		}

		public AnalysisFastQCBuilder inputFiles(final Set<SequenceFile> inputFiles) {
			this.inputFiles = inputFiles;
			return this;
		}

		public AnalysisFastQCBuilder additionalProperties(final Map<String, String> additionalProperties) {
			this.additionalProperties = additionalProperties;
			return this;
		}

		public AnalysisFastQCBuilder executionManagerAnalysisId(final String executionManagerAnalysisId) {
			this.executionManagerAnalysisId = executionManagerAnalysisId;
			return this;
		}

		public AnalysisFastQCBuilder description(final String description) {
			this.description = description;
			return this;
		}

		public AnalysisFastQCBuilder fastQCReport(final AnalysisOutputFile fastQCReport) {
			this.fastQCReport = fastQCReport;
			return this;
		}

		public AnalysisFastQCBuilder overrepresentedSequences(
				final Set<OverrepresentedSequence> overrepresentedSequences) {
			this.overrepresentedSequences = overrepresentedSequences;
			return this;
		}

		public AnalysisFastQCBuilder duplicationLevelChart(final byte[] duplicationLevelChart) {
			this.duplicationLevelChart = duplicationLevelChart;
			return this;
		}

		public AnalysisFastQCBuilder perSequenceQualityScoreChart(final byte[] perSequenceQualityScoreChart) {
			this.perSequenceQualityScoreChart = perSequenceQualityScoreChart;
			return this;
		}

		public AnalysisFastQCBuilder perBaseQualityScoreChart(final byte[] perBaseQualityScoreChart) {
			this.perBaseQualityScoreChart = perBaseQualityScoreChart;
			return this;
		}

		public AnalysisFastQCBuilder gcContent(final Short gcContent) {
			this.gcContent = gcContent;
			return this;
		}

		public AnalysisFastQCBuilder maxLength(final Integer maxLength) {
			this.maxLength = maxLength;
			return this;
		}

		public AnalysisFastQCBuilder minLength(final Integer minLength) {
			this.minLength = minLength;
			return this;
		}

		public AnalysisFastQCBuilder totalBases(final Long totalBases) {
			this.totalBases = totalBases;
			return this;
		}

		public AnalysisFastQCBuilder filteredSequences(final Integer filteredSequences) {
			this.filteredSequences = filteredSequences;
			return this;
		}

		public AnalysisFastQCBuilder totalSequences(final Integer totalSequences) {
			this.totalSequences = totalSequences;
			return this;
		}

		public AnalysisFastQCBuilder fileType(final String fileType) {
			this.fileType = fileType;
			return this;
		}

		public AnalysisFastQCBuilder encoding(final String encoding) {
			this.encoding = encoding;
			return this;
		}

		public AnalysisFastQC build() {
			if (enforceRequiredFieldCheck) {
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
			}
			return new AnalysisFastQC(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<AnalysisOutputFile> getAnalysisOutputFiles() {
		return ImmutableSet.of(fastQCReport);
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

	/**
	 * Line chartshowing per-sequence quality scores as a PNG-formatted image in
	 * a byte array.
	 * 
	 * @return a PNG-formatted byte array for per-sequence quality score.
	 */
	public byte[] getPerSequenceQualityScoreChart() {
		return perSequenceQualityScoreChart;
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

	public AnalysisOutputFile getFastQCReport() {
		return fastQCReport;
	}
}
