package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy.integration;

import java.nio.file.Path;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

public class AnalysisTest extends Analysis {

	public AnalysisTest(Set<SequenceFile> inputFiles) {
		super(inputFiles);
	}

	public Path getOutputFile() {
		throw new UnsupportedOperationException();
	}

	public String getOutputFileId() {
		throw new UnsupportedOperationException();
	}
}
