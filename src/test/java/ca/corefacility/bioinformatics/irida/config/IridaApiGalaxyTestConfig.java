package ca.corefacility.bioinformatics.irida.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.google.common.util.concurrent.MoreExecutors;

import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.IridaWorkflowsTestConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.RemoteWorkflowServiceTestConfig;

/**
 * Configuration for any integration tests requiring the use of Galaxy. Used to
 * make sure the configuration is the same for every test requiring Galaxy to
 * avoid duplicate Galaxy beans being created.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Configuration
@Import({ IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class,
		NonWindowsLocalGalaxyConfig.class, WindowsLocalGalaxyConfig.class, RemoteWorkflowServiceTestConfig.class,
		AnalysisExecutionServiceTestConfig.class, IridaWorkflowsTestConfig.class })
@Profile("test")
public class IridaApiGalaxyTestConfig {

	/**
	 * @return An ExecutorService executing code in the same thread for testing
	 *         purposes.
	 */
	@Bean
	public Executor uploadExecutor() {
		return MoreExecutors.sameThreadExecutor();
	}
}
