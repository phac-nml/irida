package ca.corefacility.bioinformatics.irida.graphql.datafetchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

@SpringBootTest(classes = { DgsAutoConfiguration.class, UserProjectsDatafetcher.class, ViewerDatafetcher.class })
public class UserProjectsDatafetcherTest {

	@Autowired
	private DgsQueryExecutor dgsQueryExecutor;

	@MockBean
	private ProjectService projectService;

	@MockBean
	private ViewerDatafetcher viewerDatafetcher;

	@BeforeEach
	public void before() {
		User user = new User(1L, "jdoe", null, null, null, null, null);
		when(viewerDatafetcher.viewer(any(DgsDataFetchingEnvironment.class))).thenReturn(user);

		Project project = new Project("myproject");
		project.setId(1L);
		when(projectService.getProjectsForUser(user))
				.thenAnswer(invocation -> List.of(new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER)));
	}

	@Test
	public void testProjects() {
		List<String> projectNames = dgsQueryExecutor.executeAndExtractJsonPath(" { viewer { projects { id name } } }",
				"data.viewer.projects[*].name");

		assertEquals(1, projectNames.size());
		assertTrue(projectNames.contains("myproject"));
	}
}
