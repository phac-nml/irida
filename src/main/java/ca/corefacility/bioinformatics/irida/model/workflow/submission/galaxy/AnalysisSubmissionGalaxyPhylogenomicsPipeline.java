package ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionPhylogenomicsPipeline;

/**
 * Defines a Phylogenomics Pipeline analysis submission.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisSubmissionGalaxyPhylogenomicsPipeline extends
		AnalysisSubmissionGalaxy implements
		AnalysisSubmissionPhylogenomicsPipeline<RemoteWorkflowGalaxy> {
	
	private ReferenceFile referenceFile;
	private String referenceFileInputLabel;

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

	@Override
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
