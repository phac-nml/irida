package ca.corefacility.bioinformatics.irida.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestFilesystemConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.IridaWorkflowsTestConfig;

/**
 * Configuration for any integration tests requiring
 *
 *
 */
@Configuration
@Import({ IridaApiNoGalaxyTestConfig.class, IridaApiServicesConfig.class, IridaApiTestFilesystemConfig.class,
		IridaApiJdbcDataSourceConfig.class, IridaWorkflowsTestConfig.class })
@Profile("it")
public class IridaApiServiceTestConfig {

	/**
	 * Sets up an {@link Unmarshaller} for workflow objects.
	 *
	 * @return An {@link Unmarshaller} for workflow objects.
	 */
	@Bean
	public Unmarshaller workflowDescriptionUnmarshaller() {
		Jaxb2Marshaller jaxb2marshaller = new Jaxb2Marshaller();
		jaxb2marshaller.setPackagesToScan("ca.corefacility.bioinformatics.irida.model.workflow");
		return jaxb2marshaller;
	}

}
