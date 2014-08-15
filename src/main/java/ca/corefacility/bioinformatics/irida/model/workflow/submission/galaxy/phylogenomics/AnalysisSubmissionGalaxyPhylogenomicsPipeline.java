package ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;

/**
 * Defines a Phylogenomics Pipeline analysis submission.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisSubmissionGalaxyPhylogenomicsPipeline extends AnalysisSubmissionGalaxy {
	
	private ReferenceFile referenceFile;
	private String referenceFileInputLabel;

	/**
	 * Builds a new Phylogenomics Pipeline analysis submission with the given information.
	 * @param inputFiles  The set of input files to submit.
	 * @param sequenceFileInputLabel  The label for the analysis input files.
	 * @param referenceFile  The reference file to submit.
	 * @param referenceFileInputLabel  The input label for the reference file.
	 * @param remoteWorkflow  The remote workflow to submit.
	 */
	public AnalysisSubmissionGalaxyPhylogenomicsPipeline(
			Set<SequenceFile> inputFiles, String sequenceFileInputLabel, 
			ReferenceFile referenceFile, String referenceFileInputLabel,
			RemoteWorkflowGalaxy remoteWorkflow) {
		super(inputFiles, sequenceFileInputLabel, remoteWorkflow);
		this.referenceFile = referenceFile;
		this.referenceFileInputLabel = referenceFileInputLabel;
	}

	public void setReferenceFile(ReferenceFile referenceFile) {
		this.referenceFile = referenceFile;
	}

	public ReferenceFile getReferenceFile() {
		return referenceFile;
	}

	public String getReferenceFileInputLabel() {
		return referenceFileInputLabel;
	}

	public void setReferenceFileInputLabel(String referenceFileInputLabel) {
		this.referenceFileInputLabel = referenceFileInputLabel;
	}
}
