package ca.corefacility.bioinformatics.irida.graphql.datafetchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ca.corefacility.bioinformatics.irida.graphql.dataloaders.ProjectsDataLoader;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

@SpringBootTest(classes = { DgsAutoConfiguration.class, ProjectsDataLoader.class, ProjectSamplesDatafetcher.class,
		SampleProjectsDatafetcher.class, UserProjectsDatafetcher.class, ViewerDatafetcher.class })
public class SampleProjectsDatafetcherTest {

	@Autowired
	private DgsQueryExecutor dgsQueryExecutor;

	@MockBean
	private ProjectService projectService;

	@MockBean
	private ViewerDatafetcher viewerDatafetcher;

	@MockBean
	private UserProjectsDatafetcher userProjectsDatafetcher;

	@MockBean
	private ProjectSamplesDatafetcher projectSamplesDatafetcher;

	@MockBean
	private ProjectsDataLoader projectsDataLoader;

	@BeforeEach
	public void before() {
		User user = new User(1L, "jdoe", null, null, null, null, null);
		when(viewerDatafetcher.viewer(any(DgsDataFetchingEnvironment.class))).thenReturn(user);

		Project project = new Project("myproject");
		project.setId(1L);
		when(userProjectsDatafetcher.projects(any(DgsDataFetchingEnvironment.class)))
				.thenAnswer(invovation -> List.of(project));

		Sample sample = new Sample("mysample");
		sample.setId(1L);
		when(projectSamplesDatafetcher.samples(any(DgsDataFetchingEnvironment.class)))
				.thenAnswer(invocation -> CompletableFuture.completedStage(List.of(sample)));

		when(projectService.getProjectIdsForSample(sample)).thenAnswer(invocation -> List.of(1L));

		when(projectsDataLoader.load(List.of(1L)))
				.thenAnswer(Invocation -> CompletableFuture.completedStage(List.of(project)));
	}

	@Test
	public void testProjects() {
		List<String> projectNames = dgsQueryExecutor.executeAndExtractJsonPath(
				" { viewer { projects { samples { projects { id name } } } } }",
				"data.viewer.projects[*].samples[*].projects[*].name");

		assertEquals(1, projectNames.size());
		assertTrue(projectNames.contains("myproject"));
	}
}
