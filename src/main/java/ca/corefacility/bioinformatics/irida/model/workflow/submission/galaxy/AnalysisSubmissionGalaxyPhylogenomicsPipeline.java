package ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy;

import java.util.Set;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionPhylogenomicsPipeline;

public class AnalysisSubmissionGalaxyPhylogenomicsPipeline extends
		AnalysisSubmissionGalaxy implements
		AnalysisSubmissionPhylogenomicsPipeline<RemoteWorkflowGalaxy> {
	
	private ReferenceFile referenceFile;
	private GalaxyAnalysisId remoteAnalysisId;
	private WorkflowOutputs outputs;

	public AnalysisSubmissionGalaxyPhylogenomicsPipeline(
			Set<SequenceFile> inputFiles, ReferenceFile referenceFile, 
			RemoteWorkflowGalaxy remoteWorkflow) {
		super(inputFiles, remoteWorkflow);
		this.referenceFile = referenceFile;
	}

	public void setReferenceFile(ReferenceFile referenceFile) {
		this.referenceFile = referenceFile;
	}

	@Override
	public ReferenceFile getReferenceFile() {
		return referenceFile;
	}
	
	public void setRemoteAnalysisId(GalaxyAnalysisId remoteAnalysisId) {
		this.remoteAnalysisId = remoteAnalysisId;
	}

	public GalaxyAnalysisId getRemoteAnalysisId() {
		return remoteAnalysisId;
	}

	public void setOutputs(WorkflowOutputs outputs) {
		this.outputs = outputs;
	}

	public WorkflowOutputs getOutputs() {
		return outputs;
	}
}
