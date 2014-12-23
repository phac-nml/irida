package ca.corefacility.bioinformatics.irida.config.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowIdSet;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowSet;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;

import com.google.common.collect.Sets;

/**
 * Class used to load up test workflows.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Configuration
@Profile({ "dev", "prod", "it" })
public class IridaWorkflowsConfig {

	@Autowired
	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	/**
	 * Builds a set of workflows to load up into IRIDA.
	 * 
	 * @return A set of workflows to load into IRIDA.
	 */
	@Bean
	public IridaWorkflowSet iridaWorkflows() {
		return new IridaWorkflowSet(Sets.newHashSet());
	}

	/**
	 * A set of workflow ids to use as defaults.
	 * 
	 * @return A set of workflow ids to use as defaults.
	 */
	@Bean
	public IridaWorkflowIdSet defaultIridaWorkflows() {
		return new IridaWorkflowIdSet(Sets.newHashSet());
	}
}
