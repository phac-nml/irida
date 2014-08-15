package ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.PreparedWorkflowGalaxy;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;

/**
 * Defines a prepared workflow for the phylogenomics pipeline.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyPreparedWorkflowPhylogenomicsPipeline implements PreparedWorkflowGalaxy {

	private WorkflowInputs workflowInputs;
	private GalaxyAnalysisId galaxyAnalysisId;
	
	public GalaxyPreparedWorkflowPhylogenomicsPipeline(GalaxyAnalysisId galaxyAnalysisId, WorkflowInputs workflowInputs) {
		this.galaxyAnalysisId = galaxyAnalysisId;
		this.workflowInputs = workflowInputs;
	}
	
	@Override
	public WorkflowInputs getWorkflowInputs() {
		return workflowInputs;
	}

	public GalaxyAnalysisId getRemoteAnalysisId() {
		return galaxyAnalysisId;
	}
}
