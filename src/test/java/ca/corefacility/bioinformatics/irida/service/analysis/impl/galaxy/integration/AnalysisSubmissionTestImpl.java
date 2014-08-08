package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration;

import java.nio.file.Path;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.service.analysis.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.analysis.Workflow;

public class AnalysisSubmissionTestImpl implements AnalysisSubmission {

	@Override
	public Workflow getWorkflow() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSequenceFiles(Set<Path> sequenceFiles) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setReferenceFile(Path referenceFile) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRemoteWorkflow(RemoteWorkflow remoteWorkflow) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAnalysisType(Class<? extends Analysis> analysisType) {
		throw new UnsupportedOperationException();
	}
}
