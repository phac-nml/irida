package ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;

/**
 * Defines a Phylogenomics Pipeline analysis submission.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "analysis_submission_phylogenomics")
@Audited
public class AnalysisSubmissionPhylogenomics
	extends AnalysisSubmissionGalaxy<RemoteWorkflowPhylogenomics> {
	
	@SuppressWarnings("unused")
	private AnalysisSubmissionPhylogenomics() {
	}

	/**
	 * Builds a new Phylogenomics Pipeline analysis submission with the given information.
	 * @param inputFiles  The set of input files to submit.
	 * @param referenceFile  The reference file to submit.
	 * @param remoteWorkflow  The remote workflow to submit.
	 * @param workflowId The id of the workflow to run for this submission.
	 */
	public AnalysisSubmissionPhylogenomics(String name,
			Set<SequenceFile> inputFiles,
			ReferenceFile referenceFile,
			RemoteWorkflowPhylogenomics remoteWorkflow, UUID workflowId) {
		super(name, inputFiles, remoteWorkflow, workflowId);
		setReferenceFile(referenceFile);
	}

	@Override
	public String toString() {
		return "AnalysisSubmissionPhylogenomics [toString()=" + super.toString() + "]";
	}
}
