package ca.corefacility.bioinformatics.irida.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import ca.corefacility.bioinformatics.irida.config.workflow.IridaWorkflowsTestConfig;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploader;

import com.google.common.util.concurrent.MoreExecutors;

/**
 * Config name for test services that need to be setup.
 * 
 *
 */
@Configuration
@Import({ IridaWorkflowsTestConfig.class })
@Profile("test")
public class IridaApiNoGalaxyTestConfig {

	/**
	 * @return An ExecutorService executing code in the same thread for testing
	 *         purposes.
	 */
	@Bean
	public Executor uploadExecutor() {
		return MoreExecutors.sameThreadExecutor();
	}

	/**
	 * Builds a {@link GalaxyUploader} with no connection to any Galaxy.
	 * 
	 * @return A {@link GalaxyUploader} with no connection to any Galaxy.
	 */
	@Bean
	public Uploader<GalaxyProjectName, GalaxyAccountEmail> galaxyUploader() {
		return new GalaxyUploader();
	}
}
