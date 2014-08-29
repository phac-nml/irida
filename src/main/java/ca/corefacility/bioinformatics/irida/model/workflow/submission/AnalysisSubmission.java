package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

/**
 * Defines a submission to an AnalysisService for executing a remote workflow.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <T>
 *            Defines the RemoteWorkflow implementing this analysis.
 */
@Entity
@Table(name = "analysis_submission")
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
@EntityListeners(AuditingEntityListener.class)
public class AnalysisSubmission implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	private String remoteAnalysisId;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	private Set<SequenceFile> inputFiles;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	private AnalysisState analysisState;

	// Analysis entity for this analysis submission. Cascading everything except
	// removals
	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH })
	private Analysis analysis;

	protected AnalysisSubmission() {
		this.createdDate = new Date();
	}

	/**
	 * Builds a new AnalysisSubmission object with the given information.
	 * 
	 * @param inputFiles
	 *            The set of input files to perform an analysis on.
	 */
	public AnalysisSubmission(Set<SequenceFile> inputFiles) {
		this();
		this.inputFiles = inputFiles;
	}

	/**
	 * Gets an analysis id for this workflow
	 * 
	 * @return An analysis id for this workflow.
	 */
	public String getRemoteAnalysisId() {
		return remoteAnalysisId;
	}

	/**
	 * Gets the set of input sequence files.
	 * 
	 * @return The set of input sequence files.
	 */
	public Set<SequenceFile> getInputFiles() {
		return inputFiles;
	}

	/**
	 * Sets the remote analysis id.
	 * 
	 * @param remoteAnalysisId
	 *            The remote analysis id to set.
	 */
	public void setRemoteAnalysisId(String remoteAnalysisId) {
		this.remoteAnalysisId = remoteAnalysisId;
	}

	/**
	 * Gets the state of this analysis.
	 * 
	 * @return The state of this analysis.
	 */
	public AnalysisState getAnalysisState() {
		return analysisState;
	}

	/**
	 * Sets the state of this analysis.
	 * 
	 * @param analysisState
	 *            The state of this analysis.
	 */
	public void setAnalysisState(AnalysisState analysisState) {
		this.analysisState = analysisState;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public String getLabel() {
		return "AnalysisSubmission[ id=" + id + " ]";
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the analysis
	 */
	public Analysis getAnalysis() {
		return analysis;
	}

	/**
	 * @param analysis
	 *            the analysis to set
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}
}
