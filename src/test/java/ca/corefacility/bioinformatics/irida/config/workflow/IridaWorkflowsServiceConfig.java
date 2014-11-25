package ca.corefacility.bioinformatics.irida.config.workflow;

import org.springframework.context.annotation.Bean;

public class IridaWorkflowsServiceConfig {

	@Bean
	public String workflowResourceLocation() {
		return "/ca/corefacility/bioinformatics/irida/service/workflow/integration/workflows";
	}
}
