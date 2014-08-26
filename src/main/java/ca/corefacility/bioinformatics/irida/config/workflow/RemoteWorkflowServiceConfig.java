package ca.corefacility.bioinformatics.irida.config.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerConfigurationException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.workflow.RemoteWorkflowRepository;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

/**
 * Configuration for loading up remote workflows.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Configuration
@Profile({ "dev", "prod", "it" })
public class RemoteWorkflowServiceConfig {
	
	private static final Logger logger = LoggerFactory
			.getLogger(RemoteWorkflowServiceConfig.class);
	
	private static final String WORKFLOW_ID = "workflow.id";
	private static final String WORKFLOW_CHECKSUM = "workflow.checksum";
	private static final String SEQUENCE_INPUT_LABEL = "workflow.input.sequence.label";
	private static final String REFERENCE_INPUT_LABEL = "workflow.input.reference.label";
	private static final String TREE_OUTPUT_NAME = "workflow.output.tree.name";
	private static final String MATRIX_OUTPUT_NAME = "workflow.output.matrix.name";
	private static final String SNP_TABLE_OUTPUT_NAME = "workflow.output.snp.table.name";
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private RemoteWorkflowRepository remoteWorkflowRepository;
	
	@Autowired
	private GalaxyWorkflowService galaxyWorkflowService;
	
	/**
	 * @return A RemoteWorkflowServicePhylogenomics with a correctly implemented workflow.
	 * @throws WorkflowException 
	 */
	@Bean
	public RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics() throws WorkflowException {
		try {
			String workflowId = getProperty("workflowId", WORKFLOW_ID);
			
			String workflowChecksum = getProperty("workflowChecksum", WORKFLOW_CHECKSUM);
			String sequenceFileInputLabel = getProperty("sequenceFileInputLabel",SEQUENCE_INPUT_LABEL);
			String referenceFileInputLabel = getProperty("referenceFileInputLabel",REFERENCE_INPUT_LABEL);
			String treeName = getProperty("treeName",TREE_OUTPUT_NAME);
			String matrixName = getProperty("matrixName",MATRIX_OUTPUT_NAME);
			String tableName = getProperty("tableName",SNP_TABLE_OUTPUT_NAME);
			
			RemoteWorkflowPhylogenomics remoteWorkflow;
			
			User user = new User();
			user.setUsername("admin");
			Authentication authentication = new AnonymousAuthenticationToken("remoteWorkflowServicePhylogenomics",
					user, AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			if (remoteWorkflowRepository.exists(workflowId)) {
				remoteWorkflow = remoteWorkflowRepository.getByType(workflowId,
						RemoteWorkflowPhylogenomics.class);
			} else {
				String realWorkflowChecksum = galaxyWorkflowService.getWorkflowChecksum(workflowId);
				logger.info("Checksum for workflow " + workflowId + " is " + realWorkflowChecksum);
				
				remoteWorkflow = new RemoteWorkflowPhylogenomics(workflowId,
					workflowChecksum, sequenceFileInputLabel, referenceFileInputLabel,
					treeName, matrixName, tableName);
				
				remoteWorkflow = remoteWorkflowRepository.save(remoteWorkflow);
			}
			
			return new RemoteWorkflowServicePhylogenomics(remoteWorkflow);
		} catch (ExecutionManagerConfigurationException e) {
			logger.error("Could not build ExecutionManagerGalaxy: " + e.getMessage());
		} finally {
			logger.info("Clearing context");
			SecurityContextHolder.clearContext();
		}
		
		return null;
	}
	
	/**
	 * Gets a properties value given it's name.
	 * @param propertyType  The type of property (used for error messages).
	 * @param propertyName  The name of the property.
	 * @return  The value of the property.
	 * @throws ExecutionManagerConfigurationException  If there was no corresponding string for the given property.
	 */
	private String getProperty(String propertyType, String propertyName) throws ExecutionManagerConfigurationException {
		String propertyValue = environment.getProperty(propertyName);
		
		if (propertyValue == null) {
			throw new ExecutionManagerConfigurationException("Missing " + propertyType,propertyName);
		} else {
			return propertyValue;
		}
	}
}
