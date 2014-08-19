package ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;

/**
 * Defines a Phylogenomics Pipeline analysis submission.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisSubmissionPhylogenomics
	extends AnalysisSubmissionGalaxy<RemoteWorkflowPhylogenomics> {
	
	private ReferenceFile referenceFile;

	/**
	 * Builds a new Phylogenomics Pipeline analysis submission with the given information.
	 * @param inputFiles  The set of input files to submit.
	 * @param referenceFile  The reference file to submit.
	 * @param remoteWorkflow  The remote workflow to submit.
	 */
	public AnalysisSubmissionPhylogenomics(
			Set<SequenceFile> inputFiles,
			ReferenceFile referenceFile,
			RemoteWorkflowPhylogenomics remoteWorkflow) {
		super(inputFiles, remoteWorkflow);
		this.referenceFile = referenceFile;
	}

	/**
	 * Sets the reference file.
	 * @param referenceFile  The reference file.
	 */
	public void setReferenceFile(ReferenceFile referenceFile) {
		this.referenceFile = referenceFile;
	}

	/**
	 * Gets the ReferenceFile.
	 * @return  The ReferenceFile.
	 */
	public ReferenceFile getReferenceFile() {
		return referenceFile;
	}
}
