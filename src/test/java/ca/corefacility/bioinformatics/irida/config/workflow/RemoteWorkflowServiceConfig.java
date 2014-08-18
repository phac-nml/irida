package ca.corefacility.bioinformatics.irida.config.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ca.corefacility.bioinformatics.irida.config.conditions.NonWindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
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
public class RemoteWorkflowServiceConfig {

	@Autowired
	private LocalGalaxy localGalaxy;
	
	/**
	 * @return A RemoteWorkflowServicePhylogenomics with a correnctly implemented workflow.
	 */
	@Bean
	public RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics() {
		String workflowCorePipelineTestId = localGalaxy.getWorkflowCorePipelineTestId();
		String workflowChecksum = localGalaxy.getWorkflowCorePipelineTestChecksum();
		String sequenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestSequenceFilesLabel();
		String referenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestReferenceLabel();
		String treeLabel = localGalaxy.getWorkflowCorePipelineTestTreeLabel();
		String matrixLabel = localGalaxy.getWorkflowCorePipelineTestMatrixLabel();
		String tableLabel = localGalaxy.getWorkflowCorePipelineTestTabelLabel();
		
		RemoteWorkflowPhylogenomics remoteWorkflow =
				new RemoteWorkflowPhylogenomics(workflowCorePipelineTestId,
				workflowChecksum, sequenceFileInputLabel, referenceFileInputLabel,
				treeLabel, matrixLabel, tableLabel);
		
		return new RemoteWorkflowServicePhylogenomics(remoteWorkflow);
	}
	
	/**
	 * @return A RemoteWorkflowServicePhylogenomics with a workflow with an invalid id.
	 */
	@Bean
	public RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomicsInvalidId() {
		String workflowCorePipelineTestId = localGalaxy.getInvalidWorkflowId();
		String workflowChecksum = localGalaxy.getWorkflowCorePipelineTestChecksum();
		String sequenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestSequenceFilesLabel();
		String referenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestReferenceLabel();
		String treeLabel = localGalaxy.getWorkflowCorePipelineTestTreeLabel();
		String matrixLabel = localGalaxy.getWorkflowCorePipelineTestMatrixLabel();
		String tableLabel = localGalaxy.getWorkflowCorePipelineTestTabelLabel();
		
		RemoteWorkflowPhylogenomics remoteWorkflow =
				new RemoteWorkflowPhylogenomics(workflowCorePipelineTestId,
				workflowChecksum, sequenceFileInputLabel, referenceFileInputLabel,
				treeLabel, matrixLabel, tableLabel);
		
		return new RemoteWorkflowServicePhylogenomics(remoteWorkflow);
	}
	
	/**
	 * @return A RemoteWorkflowServicePhylogenomics with a workflow with an invalid checksum.
	 */
	@Bean
	public RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomicsInvalidChecksum() {
		String workflowCorePipelineTestId = localGalaxy.getWorkflowCorePipelineTestId();
		String workflowChecksum = localGalaxy.getSingleInputWorkflowChecksumInvalid();
		String sequenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestSequenceFilesLabel();
		String referenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestReferenceLabel();
		String treeLabel = localGalaxy.getWorkflowCorePipelineTestTreeLabel();
		String matrixLabel = localGalaxy.getWorkflowCorePipelineTestMatrixLabel();
		String tableLabel = localGalaxy.getWorkflowCorePipelineTestTabelLabel();
		
		RemoteWorkflowPhylogenomics remoteWorkflow =
				new RemoteWorkflowPhylogenomics(workflowCorePipelineTestId,
				workflowChecksum, sequenceFileInputLabel, referenceFileInputLabel,
				treeLabel, matrixLabel, tableLabel);
		
		return new RemoteWorkflowServicePhylogenomics(remoteWorkflow);
	}
}
