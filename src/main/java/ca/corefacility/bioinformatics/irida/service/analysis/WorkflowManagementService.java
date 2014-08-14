package ca.corefacility.bioinformatics.irida.service.analysis;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Used for executing workflows in a remote workflow manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface WorkflowManagementService<T extends AnalysisSubmission<?>> {
	
	public T executeAnalysis(T analysisSubmission)
		throws ExecutionManagerException;
	
	public Analysis getAnalysisResults(T analysisSubmission)
		throws WorkflowException;
	
	public WorkflowStatus getWorkflowStatus(T analysisSubmission) 
		throws ExecutionManagerException;
}
