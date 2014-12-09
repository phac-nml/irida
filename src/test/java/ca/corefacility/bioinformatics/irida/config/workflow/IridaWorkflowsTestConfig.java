package ca.corefacility.bioinformatics.irida.config.workflow;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowIdSet;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowSet;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;
import ca.corefacility.bioinformatics.irida.service.workflow.integration.TestAnalysis;

import com.google.common.collect.Sets;

/**
 * Class used to load up test workflows.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Configuration
@Profile("test")
public class IridaWorkflowsTestConfig {

	@Autowired
	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	@Bean
	public IridaWorkflowSet iridaWorkflows() throws IOException, IridaWorkflowLoadException {
		Set<IridaWorkflow> workflowsSet = iridaWorkflowLoaderService.loadWorkflowsForClass(TestAnalysis.class);

		return new IridaWorkflowSet(workflowsSet);
	}

	@Bean
	public IridaWorkflowIdSet defaultIridaWorkflows() {
		return new IridaWorkflowIdSet(Sets.newHashSet());
	}
}
