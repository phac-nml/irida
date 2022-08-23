package ca.corefacility.bioinformatics.irida.config;

import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceTestConfig;
import ca.corefacility.bioinformatics.irida.config.analysis.GalaxyExecutionTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaDbUnitConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.IridaWorkflowsGalaxyIntegrationTestConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.IridaWorkflowsTestConfig;
import com.google.common.util.concurrent.MoreExecutors;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;

import java.util.concurrent.Executor;

/**
 * Configuration for any integration tests requiring the use of Galaxy. Used to make sure the configuration is the same
 * for every test requiring Galaxy to avoid duplicate Galaxy beans being created.
 */
@TestConfiguration
@Import({
		GalaxyExecutionTestConfig.class,
		NonWindowsLocalGalaxyConfig.class,
		WindowsLocalGalaxyConfig.class,
		AnalysisExecutionServiceTestConfig.class,
		IridaWorkflowsTestConfig.class,
		IridaWorkflowsGalaxyIntegrationTestConfig.class,
		IridaDbUnitConfig.class })
public class IridaApiGalaxyTestConfig {

	/**
	 * Sets up an {@link Unmarshaller} for workflow objects.
	 *
	 * @return An {@link Unmarshaller} for workflow objects.
	 */
	@Bean
	public Unmarshaller workflowDescriptionUnmarshaller() {
		Jaxb2Marshaller jaxb2marshaller = new Jaxb2Marshaller();
		jaxb2marshaller.setPackagesToScan(new String[] { "ca.corefacility.bioinformatics.irida.model.workflow" });
		return jaxb2marshaller;
	}

	/**
	 * @return An ExecutorService executing code in the same thread for testing purposes.
	 */
	@Bean
	public Executor uploadExecutor() {
		return MoreExecutors.directExecutor();
	}

	/**
	 * @return An InMemoryOAuth2AuthorizationService so that ClientDetailsService does not have bean errors during
	 *         Galaxy integration testing.
	 */
	@Bean
	public OAuth2AuthorizationService authorizationService() {
		return new InMemoryOAuth2AuthorizationService();
	}
}
