package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.service.analysis.AnalysisExecution;
import ca.corefacility.bioinformatics.irida.service.analysis.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.analysis.WorkflowManagementService;

public class WorkflowManagementServiceGalaxy implements WorkflowManagementService {

	@Override
	public AnalysisExecution executeAnalysis(
			AnalysisSubmission analysisSubmission) throws WorkflowException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Analysis getAnalysisResults(AnalysisExecution analysisExecution)
			throws WorkflowException {
		throw new UnsupportedOperationException();
	}

	@Override
	public WorkflowStatus getWorkflowStatus(AnalysisExecution analysisExecution)
			throws WorkflowException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cancelAnalysis(AnalysisExecution analysisExecution)
			throws WorkflowException {
		throw new UnsupportedOperationException();
	}
}
