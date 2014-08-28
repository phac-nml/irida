package ca.corefacility.bioinformatics.irida.ria.components;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

/**
 * Component for handling data needed for pipeline submission
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Component
@Scope("session")
public class PipelineSubmission {
	private ReferenceFile referenceFile;
	private Set<SequenceFile> sequenceFiles;

	/*
	 * SERVICES
	 */
	private ReferenceFileService referenceFileService;
	private SequenceFileService sequenceFileService;
	private RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics;
	private AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics;

	@Autowired
	public PipelineSubmission(ReferenceFileService referenceFileService, SequenceFileService sequenceFileService,
			RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics,
			AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics) {
		this.referenceFileService = referenceFileService;
		this.sequenceFileService = sequenceFileService;
		this.remoteWorkflowServicePhylogenomics = remoteWorkflowServicePhylogenomics;
		this.analysisExecutionServicePhylogenomics = analysisExecutionServicePhylogenomics;

		this.sequenceFiles = new HashSet<>();
	}

	public void setReferenceFile(Long referenceFileId) {
		this.referenceFile = referenceFileService.read(referenceFileId);
	}

	public void setSequenceFiles(List<Long> fileIds) {
		sequenceFiles.addAll(fileIds.stream().map(sequenceFileService::read).collect(Collectors.toList()));
	}

	public void startPipeline(Long pipelineId) throws ExecutionManagerException {
		// TODO: (14-08-28 - Josh) pipelineId needs to be passed b/c front end does not need to know the details.
		RemoteWorkflowPhylogenomics workflow = remoteWorkflowServicePhylogenomics.getCurrentWorkflow();
		AnalysisSubmissionPhylogenomics asp = new AnalysisSubmissionPhylogenomics(sequenceFiles, referenceFile,
				workflow);
		analysisExecutionServicePhylogenomics.executeAnalysis(asp);

		// Clean up for the next pipeline
		resetPipelineSubmission();
	}

	/**
	 * Reset the submission for the next time.
	 */
	private void resetPipelineSubmission() {
		this.referenceFile = null;
		this.sequenceFiles.clear();
	}
}
