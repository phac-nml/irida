package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration;

import java.nio.file.Path;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.service.analysis.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.analysis.Workflow;

public class AnalysisSubmissionTestImpl implements AnalysisSubmission {

	@Override
	public Workflow getWorkflow() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setInputSequenceFiles(Set<Path> sequenceFiles) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setInputReferenceFile(Path referenceFile) {
		throw new UnsupportedOperationException();
	}
}
