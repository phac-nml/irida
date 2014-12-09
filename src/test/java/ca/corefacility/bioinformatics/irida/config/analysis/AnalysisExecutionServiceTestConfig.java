package ca.corefacility.bioinformatics.irida.config.analysis;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import ca.corefacility.bioinformatics.irida.config.conditions.NonWindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyRoleSearch;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.workflow.RemoteWorkflowRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxySimplified;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.phylogenomics.impl.WorkspaceServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;

/**
 * Test configuration for AnalysisExecutionService classes.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Configuration
@Profile("test")
@Conditional(NonWindowsPlatformCondition.class)
public class AnalysisExecutionServiceTestConfig {

	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;
	
	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private AnalysisService analysisService;

	@Autowired
	private SampleSequenceFileJoinRepository sampleSequenceFileJoinRepository;

	@Autowired
	private RemoteWorkflowRepository remoteWorkflowRepository;

	@Autowired
	private ReferenceFileRepository referenceFileRepository;

	@Autowired
	private SequenceFileService seqeunceFileService;

	@Autowired
	private SampleService sampleService;
	
	@Autowired
	private SequenceFileRepository sequenceFileRepository;
	
	@Autowired
	private IridaWorkflowsService iridaWorkflowsService;

	@Lazy
	@Bean
	public AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics() {
		return new AnalysisExecutionServicePhylogenomics(
				analysisSubmissionService, analysisService,
				galaxyWorkflowService(), galaxyHistoriesService(),
				workspaceServicePhylogenomics());
	}
	
	@Lazy @Bean
	public AnalysisWorkspaceServiceGalaxySimplified analysisWorkspaceServiceSimplified() {
		return new AnalysisWorkspaceServiceGalaxySimplified(galaxyHistoriesService(), galaxyWorkflowService(),
				sampleSequenceFileJoinRepository, sequenceFileRepository, galaxyLibraryBuilder(), iridaWorkflowsService);
	}
	
	@Lazy
	@Bean
	public WorkspaceServicePhylogenomics workspaceServicePhylogenomics() {
		return new WorkspaceServicePhylogenomics(
				galaxyHistoriesService(), galaxyWorkflowService(),
				sampleSequenceFileJoinRepository, sequenceFileRepository, galaxyLibraryBuilder());
	}

	@Lazy
	@Bean
	public GalaxyHistoriesService galaxyHistoriesService() {
		HistoriesClient historiesClient = localGalaxy
				.getGalaxyInstanceWorkflowUser().getHistoriesClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceWorkflowUser()
				.getToolsClient();
		return new GalaxyHistoriesService(historiesClient, toolsClient, galaxyLibrariesService());
	}
	
	@Lazy
	@Bean
	public GalaxyLibrariesService galaxyLibrariesService() {
		LibrariesClient librariesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getLibrariesClient();
		return new GalaxyLibrariesService(librariesClient);
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
		HistoriesClient historiesClient = localGalaxy
				.getGalaxyInstanceWorkflowUser().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy
				.getGalaxyInstanceWorkflowUser().getWorkflowsClient();

		return new GalaxyWorkflowService(historiesClient, workflowsClient,
				new StandardPasswordEncoder(), StandardCharsets.UTF_8);
	}

	@Lazy
	@Bean
	public DatabaseSetupGalaxyITService analysisExecutionGalaxyITService() {
		return new DatabaseSetupGalaxyITService(remoteWorkflowRepository,
				referenceFileRepository, seqeunceFileService, sampleService,
				analysisExecutionServicePhylogenomics(), analysisSubmissionService, analysisSubmissionRepository);
	}
}
