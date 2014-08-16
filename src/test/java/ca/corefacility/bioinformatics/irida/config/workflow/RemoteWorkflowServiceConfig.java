package ca.corefacility.bioinformatics.irida.config.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ca.corefacility.bioinformatics.irida.config.conditions.NonWindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowGalaxyPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServiceGalaxyPhylogenomics;

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
	 * @return A RemoteWorkflowServiceGalaxyPhylogenomics with a correnctly implemented workflow.
	 */
	@Bean
	public RemoteWorkflowServiceGalaxyPhylogenomics remoteWorkflowServiceGalaxyPhylogenomics() {
		String workflowCorePipelineTestId = localGalaxy.getWorkflowCorePipelineTestId();
		String workflowChecksum = localGalaxy.getWorkflowCorePipelineTestChecksum();
		String sequenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestSequenceFilesLabel();
		String referenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestReferenceLabel();
		
		RemoteWorkflowGalaxyPhylogenomics remoteWorkflow =
				new RemoteWorkflowGalaxyPhylogenomics(workflowCorePipelineTestId,
				workflowChecksum, sequenceFileInputLabel, referenceFileInputLabel);
		
		return new RemoteWorkflowServiceGalaxyPhylogenomics(remoteWorkflow);
	}
	
	/**
	 * @return A RemoteWorkflowServiceGalaxyPhylogenomics with a workflow with an invalid id.
	 */
	@Bean
	public RemoteWorkflowServiceGalaxyPhylogenomics remoteWorkflowServiceGalaxyPhylogenomicsInvalidId() {
		String workflowCorePipelineTestId = localGalaxy.getInvalidWorkflowId();
		String workflowChecksum = localGalaxy.getWorkflowCorePipelineTestChecksum();
		String sequenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestSequenceFilesLabel();
		String referenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestReferenceLabel();
		
		RemoteWorkflowGalaxyPhylogenomics remoteWorkflow =
				new RemoteWorkflowGalaxyPhylogenomics(workflowCorePipelineTestId,
				workflowChecksum, sequenceFileInputLabel, referenceFileInputLabel);
		
		return new RemoteWorkflowServiceGalaxyPhylogenomics(remoteWorkflow);
	}
	
	/**
	 * @return A RemoteWorkflowServiceGalaxyPhylogenomics with a workflow with an invalid checksum.
	 */
	@Bean
	public RemoteWorkflowServiceGalaxyPhylogenomics remoteWorkflowServiceGalaxyPhylogenomicsInvalidChecksum() {
		String workflowCorePipelineTestId = localGalaxy.getWorkflowCorePipelineTestId();
		String workflowChecksum = localGalaxy.getSingleInputWorkflowChecksumInvalid();
		String sequenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestSequenceFilesLabel();
		String referenceFileInputLabel = localGalaxy.getWorkflowCorePipelineTestReferenceLabel();
		
		RemoteWorkflowGalaxyPhylogenomics remoteWorkflow =
				new RemoteWorkflowGalaxyPhylogenomics(workflowCorePipelineTestId,
				workflowChecksum, sequenceFileInputLabel, referenceFileInputLabel);
		
		return new RemoteWorkflowServiceGalaxyPhylogenomics(remoteWorkflow);
	}
}
