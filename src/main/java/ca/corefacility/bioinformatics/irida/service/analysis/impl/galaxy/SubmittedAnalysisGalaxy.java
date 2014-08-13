package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;

public class SubmittedAnalysisGalaxy implements SubmittedAnalysis<GalaxyAnalysisId> {

	private GalaxyAnalysisId analysisId;
	private WorkflowOutputs outputs;
	
	public SubmittedAnalysisGalaxy(GalaxyAnalysisId analysisId, WorkflowOutputs outputs) {
		this.analysisId = analysisId;
		this.outputs = outputs;
	}
	
	@Override
	public GalaxyAnalysisId getRemoteAnalysisId() {
		return analysisId;
	}

	@Override
	public WorkflowOutputs getOutputIds() {
		return outputs;
	}
}
