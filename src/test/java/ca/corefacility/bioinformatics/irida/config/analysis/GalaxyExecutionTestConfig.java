package ca.corefacility.bioinformatics.irida.config.analysis;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import ca.corefacility.bioinformatics.irida.config.conditions.NonWindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyRoleSearch;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;

/**
 * Test configuration for Galaxy execution services.
 */
@Configuration
@Profile("test")
@Conditional(NonWindowsPlatformCondition.class)
public class GalaxyExecutionTestConfig {

	@Autowired
	private LocalGalaxy localGalaxy;

	/**
	 * Timeout in seconds to stop polling a Galaxy library.
	 */
	private static final int LIBRARY_TIMEOUT = 5 * 60;

	/**
	 * Polling time in seconds to poll a Galaxy library to check if datasets
	 * have been properly uploaded.
	 */
	private static final int LIBRARY_POLLING_TIME = 5;

	@Lazy
	@Bean
	public GalaxyHistoriesService galaxyHistoriesService() {
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getHistoriesClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceWorkflowUser().getToolsClient();
		return new GalaxyHistoriesService(historiesClient, toolsClient, galaxyLibrariesService());
	}

	@Lazy
	@Bean
	public GalaxyLibrariesService galaxyLibrariesService() {
		LibrariesClient librariesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getLibrariesClient();
		return new GalaxyLibrariesService(librariesClient, LIBRARY_POLLING_TIME, LIBRARY_TIMEOUT);
	}

	@Lazy
	@Bean
	public GalaxyLibraryBuilder galaxyLibraryBuilder() {
		LibrariesClient librariesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getLibrariesClient();
		return new GalaxyLibraryBuilder(librariesClient, galaxyRoleSearch(), localGalaxy.getGalaxyURL());
	}

	@Lazy
	@Bean
	public GalaxyRoleSearch galaxyRoleSearch() {
		RolesClient rolesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getRolesClient();
		return new GalaxyRoleSearch(rolesClient, localGalaxy.getGalaxyURL());
	}
	
	@Lazy
	@Bean
	public GalaxyWorkflowService galaxyWorkflowService() {
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceWorkflowUser().getWorkflowsClient();

		return new GalaxyWorkflowService(historiesClient, workflowsClient, StandardCharsets.UTF_8);
	}
}
