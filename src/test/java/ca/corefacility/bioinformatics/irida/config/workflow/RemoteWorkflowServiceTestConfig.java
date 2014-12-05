package ca.corefacility.bioinformatics.irida.config.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import ca.corefacility.bioinformatics.irida.config.conditions.NonWindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

/**
 * Test configuration for AnalysisExecutionService classes.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Configuration
@Profile("test")
@Conditional(NonWindowsPlatformCondition.class)
public class RemoteWorkflowServiceTestConfig {
	
	private static final Logger logger = LoggerFactory
			.getLogger(RemoteWorkflowServiceTestConfig.class);

	@Autowired
	private LocalGalaxy localGalaxy;
	
	@Autowired
	private GalaxyWorkflowService galaxyWorkflowService;
	
	/**
	 * @return A RemoteWorkflowServicePhylogenomics with a correnctly implemented workflow.
	 * @throws WorkflowException 
	 */
	@Lazy
	@Bean
	public RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics() throws WorkflowException {
		String workflowCorePipelineTestId = localGalaxy.getWorkflowCorePipelineTestId();
		String workflowChecksum = localGalaxy.getWorkflowCorePipelineTestChecksum();
		String sequenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestSequenceFilesLabel();
		String referenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestReferenceLabel();
		String treeLabel = localGalaxy.getWorkflowCorePipelineTestTreeName();
		String matrixLabel = localGalaxy.getWorkflowCorePipelineTestMatrixName();
		String tableLabel = localGalaxy.getWorkflowCorePipelineTestTabelName();
		
		String currentWorkflowChecksum = galaxyWorkflowService.getWorkflowChecksum(workflowCorePipelineTestId);
		logger.debug("A valid Core Pipeline Test Workflow checksum=" + currentWorkflowChecksum);
		
		RemoteWorkflowPhylogenomics remoteWorkflow =
				new RemoteWorkflowPhylogenomics(workflowCorePipelineTestId,
				workflowChecksum, sequenceFileInputLabel, referenceFileInputLabel,
				treeLabel, matrixLabel, tableLabel);
		
		return new RemoteWorkflowServicePhylogenomics(remoteWorkflow);
	}
	
	/**
	 * @return A RemoteWorkflowServicePhylogenomics with a workflow with an invalid id.
	 */
	@Lazy
	@Bean
	public RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomicsInvalidId() {
		String workflowCorePipelineTestId = localGalaxy.getInvalidWorkflowId();
		String workflowChecksum = localGalaxy.getWorkflowCorePipelineTestChecksum();
		String sequenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestSequenceFilesLabel();
		String referenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestReferenceLabel();
		String treeLabel = localGalaxy.getWorkflowCorePipelineTestTreeName();
		String matrixLabel = localGalaxy.getWorkflowCorePipelineTestMatrixName();
		String tableLabel = localGalaxy.getWorkflowCorePipelineTestTabelName();
		
		RemoteWorkflowPhylogenomics remoteWorkflow =
				new RemoteWorkflowPhylogenomics(workflowCorePipelineTestId,
				workflowChecksum, sequenceFileInputLabel, referenceFileInputLabel,
				treeLabel, matrixLabel, tableLabel);
		
		return new RemoteWorkflowServicePhylogenomics(remoteWorkflow);
	}
	
	/**
	 * @return A RemoteWorkflowServicePhylogenomics with a workflow with an invalid checksum.
	 */
	@Lazy
	@Bean
	public RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomicsInvalidChecksum() {
		String workflowCorePipelineTestId = localGalaxy.getWorkflowCorePipelineTestId();
		String workflowChecksum = localGalaxy.getSingleInputWorkflowChecksumInvalid();
		String sequenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestSequenceFilesLabel();
		String referenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestReferenceLabel();
		String treeLabel = localGalaxy.getWorkflowCorePipelineTestTreeName();
		String matrixLabel = localGalaxy.getWorkflowCorePipelineTestMatrixName();
		String tableLabel = localGalaxy.getWorkflowCorePipelineTestTabelName();
		
		RemoteWorkflowPhylogenomics remoteWorkflow =
				new RemoteWorkflowPhylogenomics(workflowCorePipelineTestId,
				workflowChecksum, sequenceFileInputLabel, referenceFileInputLabel,
				treeLabel, matrixLabel, tableLabel);
		
		return new RemoteWorkflowServicePhylogenomics(remoteWorkflow);
	}
}
