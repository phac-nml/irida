package ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowGalaxyPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;

/**
 * Defines a Phylogenomics Pipeline analysis submission.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisSubmissionGalaxyPhylogenomicsPipeline
	extends AnalysisSubmissionGalaxy<RemoteWorkflowGalaxyPhylogenomics> {
	
	private ReferenceFile referenceFile;

	/**
	 * Builds a new Phylogenomics Pipeline analysis submission with the given information.
	 * @param inputFiles  The set of input files to submit.
	 * @param referenceFile  The reference file to submit.
	 * @param remoteWorkflow  The remote workflow to submit.
	 */
	public AnalysisSubmissionGalaxyPhylogenomicsPipeline(
			Set<SequenceFile> inputFiles,
			ReferenceFile referenceFile,
			RemoteWorkflowGalaxyPhylogenomics remoteWorkflow) {
		super(inputFiles, remoteWorkflow);
		this.referenceFile = referenceFile;
	}

	public void setReferenceFile(ReferenceFile referenceFile) {
		this.referenceFile = referenceFile;
	}

	public ReferenceFile getReferenceFile() {
		return referenceFile;
	}
}
