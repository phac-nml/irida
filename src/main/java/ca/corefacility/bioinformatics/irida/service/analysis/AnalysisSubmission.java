package ca.corefacility.bioinformatics.irida.service.analysis;

import java.nio.file.Path;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.RemoteWorkflow;

public interface AnalysisSubmission<T extends RemoteWorkflow> {

	public void setSequenceFiles(Set<SequenceFile> sequenceFiles);

	public void setReferenceFile(Path referenceFile);

	public void setRemoteWorkflow(T remoteWorkflow);

	public T getRemoteWorkflow();

	public Set<SequenceFile> getSequenceFiles();

	public Path getReferenceFile();
}
