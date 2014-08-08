package ca.corefacility.bioinformatics.irida.service.analysis;

import java.nio.file.Path;
import java.util.Set;

public interface AnalysisSubmission {

	public Workflow getWorkflow();

	public void setInputSequenceFiles(Set<Path> sequenceFiles);

	public void setInputReferenceFile(Path referenceFile);
}
