package ca.corefacility.bioinformatics.irida.service.analysis;

import java.nio.file.Path;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration.RemoteWorkflow;

public interface AnalysisSubmission {

	public Workflow getWorkflow();

	public void setSequenceFiles(Set<Path> sequenceFiles);

	public void setReferenceFile(Path referenceFile);

	public void setRemoteWorkflow(RemoteWorkflow remoteWorkflow);

	public void setAnalysisType(Class<? extends Analysis> analysisType);
}
