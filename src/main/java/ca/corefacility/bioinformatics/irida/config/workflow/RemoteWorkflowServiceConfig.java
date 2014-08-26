package ca.corefacility.bioinformatics.irida.config.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerConfigurationException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
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
	
	private static final String WORKFLOW_ID = "galaxy.workflow.id";
	
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
	@Lazy @Bean
	public RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics() throws WorkflowException {
		RemoteWorkflowPhylogenomics remoteWorkflow = null;
		
		try {
			String workflowId = getProperty("workflowId", WORKFLOW_ID);
	
			if (remoteWorkflowRepository.exists(workflowId)) {
				remoteWorkflow = remoteWorkflowRepository.getByType(workflowId,
						RemoteWorkflowPhylogenomics.class);
			}			
		} catch (ExecutionManagerConfigurationException e) {
			logger.error(e.getMessage());
		}
		
		return new RemoteWorkflowServicePhylogenomics(remoteWorkflow);
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
