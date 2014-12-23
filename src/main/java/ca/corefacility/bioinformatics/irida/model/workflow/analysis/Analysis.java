package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Date;
import java.util.HashMap;
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

import org.hibernate.envers.Audited;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * An analysis object for storing results of an analysis execution.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "analysis")
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
public abstract class Analysis implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@Lob
	private String description;

	// identifier linking an analysis to an external workflow manager.
	@NotNull
	private String executionManagerAnalysisId;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "property_key", nullable = false)
	@Column(name = "property_value", nullable = false)
	@CollectionTable(name = "analysis_properties", joinColumns = @JoinColumn(name = "analysis_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"analysis_id", "property_key" }, name = "UK_ANALYSIS_PROPERTY_KEY"))
	private Map<String, String> additionalProperties;

	@NotNull
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	private Set<SequenceFile> inputFiles;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "analysis_output_file_key", nullable = false)
	@Column(name = "analysis_output_file_value", nullable = false)
	@CollectionTable(name = "analysis_output_files", joinColumns = @JoinColumn(name = "analysis_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"analysis_id", "analysis_output_file_key" }, name = "UK_ANALYSIS_OUTPUT_FILE_KEY"))
	private Map<String, AnalysisOutputFile> analysisOutputFilesMap;

	private Analysis() {
		this.createdDate = new Date();
		this.modifiedDate = createdDate;
		this.analysisOutputFilesMap = new HashMap<>(); 
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
	public Analysis(Set<SequenceFile> inputFiles, String executionManagerAnalysisId,
			Map<String, AnalysisOutputFile> analysisOutputFilesMap) {
		this();
		this.inputFiles = inputFiles;
		this.executionManagerAnalysisId = executionManagerAnalysisId;
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
	public Analysis(Set<SequenceFile> inputFiles, String executionManagerAnalysisId) {
		this(inputFiles, executionManagerAnalysisId, Maps.newHashMap());
	}

	public int hashCode() {
		return Objects.hash(createdDate, modifiedDate, description, executionManagerAnalysisId, analysisOutputFilesMap);
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof Analysis) {
			Analysis a = (Analysis) o;
			return Objects.equals(createdDate, a.createdDate) && Objects.equals(modifiedDate, a.modifiedDate)
					&& Objects.equals(description, a.description)
					&& Objects.equals(executionManagerAnalysisId, a.executionManagerAnalysisId)
					&& Objects.equals(analysisOutputFilesMap,a.analysisOutputFilesMap);
		}

		return false;
	}
	
	/**
	 * Get all output files produced by this {@link Analysis}.
	 * 
	 * @return the set of all output files produced by the {@link Analysis}.
	 */
	public Set<AnalysisOutputFile> getAnalysisOutputFiles() {
		return Sets.newHashSet(analysisOutputFilesMap.values());
	}

	public Set<SequenceFile> getInputSequenceFiles() {
		return inputFiles;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExecutionManagerAnalysisId() {
		return executionManagerAnalysisId;
	}

	public void setExecutionManagerAnalysisId(String executionManagerAnalysisId) {
		this.executionManagerAnalysisId = executionManagerAnalysisId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setId(Long id) {
		this.id = id;
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
		return this.modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Map<String, String> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Map<String, String> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
}
