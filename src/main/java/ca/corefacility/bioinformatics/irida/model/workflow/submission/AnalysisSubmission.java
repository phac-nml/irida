package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;

/**
 * Defines a submission to an AnalysisService for executing a remote workflow.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <T> Defines the RemoteWorkflow implementing this analysis.
 */
@Entity
@Table(name = "analysis_submission")
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
public abstract class AnalysisSubmission<T extends RemoteWorkflow> {

	@Id
	private String remoteAnalysisId;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	private Set<SequenceFile> inputFiles;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, targetEntity=RemoteWorkflow.class)
	@JoinColumn(name = "remote_workflow_id")
	private T remoteWorkflow;
	
	/**
	 * Builds a new AnalysisSubmission object with the given information.
	 * @param inputFiles  The set of input files to perform an analysis on.
	 * @param remoteWorkflow  The remote workflow implementing this analysis.
	 */
	public AnalysisSubmission(Set<SequenceFile> inputFiles, T remoteWorkflow) {
		this.inputFiles = inputFiles;
		this.remoteWorkflow = remoteWorkflow;
	}

	/**
	 * Gets an analysis id for this workflow
	 * @return  An analysis id for this workflow.
	 */
	public String getRemoteAnalysisId() {
		return remoteAnalysisId;
	}

	/**
	 * Gets a RemoteWorkflow implementing this submission.
	 * @return  A RemoteWorkflow implementing this submission.
	 */
	public T getRemoteWorkflow() {
		return remoteWorkflow;
	}

	/**
	 * Gets the set of input sequence files.
	 * @return  The set of input sequence files.
	 */
	public Set<SequenceFile> getInputFiles() {
		return inputFiles;
	}

	/**
	 * Sets the remote analysis id.
	 * @param remoteAnalysisId  The remote analysis id to set.
	 */
	public void setRemoteAnalysisId(String remoteAnalysisId) {
		this.remoteAnalysisId = remoteAnalysisId;
	}

	/**
	 * Sets the remote workflow for this analysis submission.
	 * @param remoteWorkflow  The RemoteWorkflow for this analysis submission.
	 */
	public void setRemoteWorkflow(T remoteWorkflow) {
		this.remoteWorkflow = remoteWorkflow;
	}
}
