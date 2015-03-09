package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * An analysis object for storing results of an analysis execution.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "analysis")
@Inheritance(strategy = InheritanceType.JOINED)
public class Analysis implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private final Long id;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@Lob
	private String description;

	// identifier linking an analysis to an external workflow manager.
	@NotNull
	private final String executionManagerAnalysisId;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "property_key", nullable = false)
	@Column(name = "property_value", nullable = false)
	@CollectionTable(name = "analysis_properties", joinColumns = @JoinColumn(name = "analysis_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"analysis_id", "property_key" }, name = "UK_ANALYSIS_PROPERTY_KEY"))
	private Map<String, String> additionalProperties;

	@NotNull
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	private final Set<SequenceFile> inputFiles;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "analysis_output_file_key", nullable = false)
	@Column(name = "analysis_output_file_value", nullable = false)
	@CollectionTable(name = "analysis_output_file_map", joinColumns = @JoinColumn(name = "analysis_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"analysis_id", "analysis_output_file_key" }, name = "UK_ANALYSIS_OUTPUT_FILE_KEY"))
	private final Map<String, AnalysisOutputFile> analysisOutputFilesMap;

	/**
	 * For hibernate
	 */
	protected Analysis() {
		this.id = null;
		this.createdDate = null;
		this.description = null;
		this.executionManagerAnalysisId = null;
		this.additionalProperties = null;
		this.inputFiles = null;
		this.analysisOutputFilesMap = null;
	}

	/**
	 * Builds a new {@link Analysis} object with the given information.
	 * 
	 * @param inputFiles
	 *            The input {@link SequenceFile}s for this analysis.
	 * @param executionManagerAnalysisId
	 *            The id for an execution manager used with this analysis.
	 * @param analysisOutputFilesMap
	 *            A {@link Map} of output file keys and
	 *            {@link AnalysisOutputFile}s.
	 */
	public Analysis(final Set<SequenceFile> inputFiles, final String executionManagerAnalysisId,
			final Map<String, AnalysisOutputFile> analysisOutputFilesMap) {
		this.id = null;
		this.createdDate = new Date();
		this.inputFiles = inputFiles;
		this.executionManagerAnalysisId = executionManagerAnalysisId;
		this.analysisOutputFilesMap = analysisOutputFilesMap;
		this.description = null;
		this.additionalProperties = Collections.emptyMap();
	}

	/**
	 * Builds a new {@link Analysis} object with the given information.
	 * 
	 * @param inputFiles
	 *            The input {@link SequenceFile}s for this analysis.
	 * @param executionManagerAnalysisId
	 *            The id for an execution manager used with this analysis.
	 * @param analysisOutputFilesMap
	 *            A {@link Map} of output file keys and
	 *            {@link AnalysisOutputFile}s.
	 * @param description
	 *            a description of the analysis.
	 * @param additionalProperties
	 *            any other properties available
	 */
	public Analysis(final Set<SequenceFile> inputFiles, final String executionManagerAnalysisId,
			final Map<String, AnalysisOutputFile> analysisOutputFilesMap, final String description,
			final Map<String, String> additionalProperties) {
		this.id = null;
		this.createdDate = new Date();
		this.inputFiles = inputFiles;
		this.executionManagerAnalysisId = executionManagerAnalysisId;
		this.analysisOutputFilesMap = analysisOutputFilesMap;
		this.description = description;
		this.additionalProperties = additionalProperties;
	}

	/**
	 * Builds a new {@link Analysis} object with the given information and an
	 * empty set of output files.
	 * 
	 * @param inputFiles
	 *            The input {@link SequenceFile}s for this analysis.
	 * @param executionManagerAnalysisId
	 *            The id for an execution manager used with this analysis.
	 */
	public Analysis(final Set<SequenceFile> inputFiles, final String executionManagerAnalysisId,
			final String description, final Map<String, String> additionalProperties) {
		this(inputFiles, executionManagerAnalysisId, Maps.newHashMap());
		this.description = description;
		this.additionalProperties = additionalProperties;
	}

	public int hashCode() {
		return Objects.hash(createdDate, description, executionManagerAnalysisId, analysisOutputFilesMap);
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof Analysis) {
			Analysis a = (Analysis) o;
			return Objects.equals(createdDate, a.createdDate) && Objects.equals(description, a.description)
					&& Objects.equals(executionManagerAnalysisId, a.executionManagerAnalysisId)
					&& Objects.equals(analysisOutputFilesMap, a.analysisOutputFilesMap);
		}

		return false;
	}

	/**
	 * Get all output files produced by this {@link Analysis}.
	 * 
	 * @return the set of all output files produced by the {@link Analysis}.
	 */
	public Set<AnalysisOutputFile> getAnalysisOutputFiles() {
		return ImmutableSet.copyOf(analysisOutputFilesMap.values());
	}

	public Set<SequenceFile> getInputSequenceFiles() {
		return ImmutableSet.copyOf(inputFiles);
	}

	public String getDescription() {
		return description;
	}

	public String getExecutionManagerAnalysisId() {
		return executionManagerAnalysisId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public AnalysisOutputFile getAnalysisOutputFile(String key) {
		return this.analysisOutputFilesMap.get(key);
	}

	@Override
	public String getLabel() {
		return executionManagerAnalysisId;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Date getModifiedDate() {
		return this.createdDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		throw new UnsupportedOperationException("Analysis types cannot be modified.");
	}

	public Map<String, String> getAdditionalProperties() {
		return ImmutableMap.copyOf(additionalProperties);
	}

	@Override
	public void setId(Long id) {
		throw new UnsupportedOperationException("Analysis types cannot be modified.");
	}
}
