package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * An analysis object for storing results of an analysis execution.
 * 
 *
 */
@Entity
@Table(name = "analysis")
@Inheritance(strategy = InheritanceType.JOINED)
public class Analysis extends IridaResourceSupport implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@Lob
	private final String description;

	// identifier linking an analysis to an external workflow manager.
	@NotNull
	private final String executionManagerAnalysisId;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "property_key", nullable = false)
	@Column(name = "property_value", nullable = false)
	@CollectionTable(name = "analysis_properties", joinColumns = @JoinColumn(name = "analysis_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"analysis_id", "property_key" }, name = "UK_ANALYSIS_PROPERTY_KEY"))
	private final Map<String, String> additionalProperties;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "analysis_output_file_key", nullable = false)
	@Column(name = "analysis_output_file_value", nullable = false)
	@CollectionTable(name = "analysis_output_file_map", joinColumns = @JoinColumn(name = "analysis_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"analysis_id", "analysis_output_file_key" }, name = "UK_ANALYSIS_OUTPUT_FILE_KEY"))
	private final Map<String, AnalysisOutputFile> analysisOutputFilesMap;

	@Embedded
	@NotNull
	private AnalysisType analysisType;
	
	/**
	 * For hibernate
	 */
	protected Analysis() {
		this.id = null;
		this.createdDate = null;
		this.description = null;
		this.executionManagerAnalysisId = null;
		this.additionalProperties = null;
		this.analysisOutputFilesMap = null;
		this.analysisType = null;
	}

	/**
	 * Builds a new {@link Analysis} object with the given information.
	 *
	 * @param executionManagerAnalysisId The id for an execution manager used with this analysis.
	 * @param analysisOutputFilesMap     A {@link Map} of output file keys and
	 *                                   {@link AnalysisOutputFile}s.
	 * @param analysisType               The {@link AnalysisType} for this analysis
	 */
	public Analysis(final String executionManagerAnalysisId,
			final Map<String, AnalysisOutputFile> analysisOutputFilesMap, AnalysisType analysisType) {
		this.id = null;
		this.createdDate = new Date();
		this.executionManagerAnalysisId = executionManagerAnalysisId;
		this.analysisOutputFilesMap = analysisOutputFilesMap;
		this.description = null;
		this.additionalProperties = Collections.emptyMap();
		this.analysisType = analysisType;
	}

	/**
	 * Builds a new {@link Analysis} object with the given information.
	 * 
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
	public Analysis(final String executionManagerAnalysisId,
			final Map<String, AnalysisOutputFile> analysisOutputFilesMap, final String description,
			final Map<String, String> additionalProperties) {
		this.id = null;
		this.createdDate = new Date();
		this.executionManagerAnalysisId = executionManagerAnalysisId;
		this.analysisOutputFilesMap = analysisOutputFilesMap;
		this.description = description;
		this.additionalProperties = additionalProperties;
	}

	/**
	 * Builds a new {@link Analysis} object with the given information and an
	 * empty set of output files.
	 * 
	 * @param executionManagerAnalysisId
	 *            The id for an execution manager used with this analysis.
	 * @param description
	 *            a description of the analysis.
	 * @param additionalProperties
	 *            any other properties available
	 */
	public Analysis(final String executionManagerAnalysisId, final String description,
			final Map<String, String> additionalProperties) {
		this.id = null;
		this.createdDate = new Date();
		this.executionManagerAnalysisId = executionManagerAnalysisId;
		this.analysisOutputFilesMap = Collections.emptyMap();
		this.description = description;
		this.additionalProperties = additionalProperties;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDate, description, executionManagerAnalysisId, analysisOutputFilesMap);
	}

	@Override
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

	@Override
	public String toString() {
		return "Analysis{" + "id=" + id + 
				", createdDate=" + createdDate +
				", description='" + description +
				'\'' + ", executionManagerAnalysisId='" + executionManagerAnalysisId + '\'' +
				", additionalProperties=" + additionalProperties +
				", analysisOutputFilesMap=" + analysisOutputFilesMap +
				", analysisType=" + analysisType + '}';
	}

	/**
	 * Get all output files produced by this {@link Analysis}.
	 * 
	 * @return the set of all output files produced by the {@link Analysis}.
	 */
	@JsonIgnore
	public Set<AnalysisOutputFile> getAnalysisOutputFiles() {
		return ImmutableSet.copyOf(analysisOutputFilesMap.values());
	}

	public Map<String, AnalysisOutputFile> getAnalysisOutputFilesMap() {
		return ImmutableMap.copyOf(this.analysisOutputFilesMap);
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

	/**
	 * Get an output file with the given key
	 * @param key the key
	 * @return an AnalysisOutputFile
	 */
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

	public void setId(Long id){
		this.id = id;
	}

	public Map<String, String> getAdditionalProperties() {
		return ImmutableMap.copyOf(additionalProperties);
	}

	/**
	 * Return the names of available output files from this analysis
	 * 
	 * @return Set of names
	 */
	@JsonIgnore
	public Set<String> getAnalysisOutputFileNames() {
		return analysisOutputFilesMap.keySet();
	}
	
	public AnalysisType getAnalysisType() {
		return analysisType;
	}
	
	public void setAnalysisType(AnalysisType analysisType) {
		this.analysisType = analysisType;
	}
}
