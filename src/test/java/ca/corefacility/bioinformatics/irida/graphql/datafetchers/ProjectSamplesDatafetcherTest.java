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

import ca.corefacility.bioinformatics.irida.graphql.dataloaders.SamplesDataLoader;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@SpringBootTest(classes = { DgsAutoConfiguration.class, ProjectSamplesDatafetcher.class, SamplesDataLoader.class,
		UserProjectsDatafetcher.class, ViewerDatafetcher.class })
public class ProjectSamplesDatafetcherTest {

	@Autowired
	private DgsQueryExecutor dgsQueryExecutor;

	@MockBean
	private SampleService sampleService;

	@MockBean
	private ViewerDatafetcher viewerDatafetcher;

	@MockBean
	private UserProjectsDatafetcher userProjectsDatafetcher;

	@MockBean
	private SamplesDataLoader samplesDataLoader;

	@BeforeEach
	public void before() {
		User user = new User(1L, "jdoe", null, null, null, null, null);
		when(viewerDatafetcher.viewer(any(DgsDataFetchingEnvironment.class))).thenReturn(user);

		Project project = new Project("myproject");
		project.setId(1L);
		when(userProjectsDatafetcher.projects(any(DgsDataFetchingEnvironment.class)))
				.thenAnswer(invocation -> List.of(project));

		Sample sample = new Sample("mysample");
		sample.setId(1L);
		when(sampleService.getSampleIdsForProject(project)).thenAnswer(invocation -> List.of(1L));

		when(samplesDataLoader.load(List.of(1L)))
				.thenAnswer(invocation -> CompletableFuture.completedStage(List.of(sample)));
	}

	@Test
	public void testSamples() {
		List<String> sampleNames = dgsQueryExecutor.executeAndExtractJsonPath(
				" { viewer { projects { samples { id sampleName } } } }",
				"data.viewer.projects[*].samples[*].sampleName");

		assertEquals(1, sampleNames.size());
		assertTrue(sampleNames.contains("mysample"));
	}
}
