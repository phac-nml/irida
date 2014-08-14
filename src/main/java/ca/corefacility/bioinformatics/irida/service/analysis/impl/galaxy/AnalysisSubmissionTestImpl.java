package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.service.analysis.AnalysisSubmission;

public class AnalysisSubmissionTestImpl implements AnalysisSubmission<RemoteWorkflowGalaxy> {
	
	private Set<Path> sequenceFiles;
	private Path referenceFile;
	private RemoteWorkflowGalaxy remoteWorkflow;
	@SuppressWarnings("unused")
	private Class<? extends Analysis> analysisType;

	@Override
	public void setSequenceFiles(Set<Path> sequenceFiles) {
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
	public void setAnalysisType(Class<? extends Analysis> analysisType) {
		checkNotNull(analysisType, "analysisType is null");
		this.analysisType = analysisType;
	}

	@Override
	public RemoteWorkflowGalaxy getRemoteWorkflow() {
		return remoteWorkflow;
	}

	@Override
	public Set<Path> getSequenceFiles() {
		return sequenceFiles;
	}

	@Override
	public Path getReferenceFile() {
		return referenceFile;
	}
}
