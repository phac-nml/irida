package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.service.analysis.AnalysisSubmission;

public class AnalysisSubmissionTestImpl implements AnalysisSubmission<RemoteWorkflowGalaxy> {
	
	private Set<SequenceFile> sequenceFiles;
	private Path referenceFile;
	private RemoteWorkflowGalaxy remoteWorkflow;

	@Override
	public void setSequenceFiles(Set<SequenceFile> sequenceFiles) {
		checkNotNull(sequenceFiles, "sequenceFiles is null");
		this.sequenceFiles = sequenceFiles;
	}

	@Override
	public void setReferenceFile(Path referenceFile) {
		checkNotNull(referenceFile, "referenceFile is null");
		this.referenceFile = referenceFile;
	}

	@Override
	public void setRemoteWorkflow(RemoteWorkflowGalaxy remoteWorkflow) {
		checkNotNull(remoteWorkflow, "remoteWorkflow is null");
		this.remoteWorkflow = remoteWorkflow;
	}

	@Override
	public RemoteWorkflowGalaxy getRemoteWorkflow() {
		return remoteWorkflow;
	}

	@Override
	public Set<SequenceFile> getSequenceFiles() {
		return sequenceFiles;
	}

	@Override
	public Path getReferenceFile() {
		return referenceFile;
	}
}
