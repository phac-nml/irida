package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration;

import java.nio.file.Path;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.service.analysis.AnalysisSubmission;

public class AnalysisSubmissionTestImpl implements AnalysisSubmission<ExecutionManagerGalaxy> {

	@Override
	public void setSequenceFiles(Set<Path> sequenceFiles) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setReferenceFile(Path referenceFile) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRemoteWorkflow(RemoteWorkflow<ExecutionManagerGalaxy> remoteWorkflow) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAnalysisType(Class<? extends Analysis> analysisType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public RemoteWorkflow<ExecutionManagerGalaxy> getRemoteWorkflow() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Path> getSequenceFiles() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Path getReferenceFile() {
		throw new UnsupportedOperationException();
	}
}
