package ca.corefacility.bioinformatics.irida.graphql.datafetchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import graphql.ExecutionResult;
import graphql.validation.ValidationError;

@SpringBootTest(classes = { DgsAutoConfiguration.class, ProjectsDatafetcher.class })
public class ProjectsDatafetcherTest {

	@Autowired
	private DgsQueryExecutor dgsQueryExecutor;

	@MockBean
	private ProjectService projectService;

	@Test
	public void testCreateProject() {
		ExecutionResult createProjectResult = dgsQueryExecutor
				.execute("mutation { createProject(input : {name: \"myproject\"}) { id name } }");

		assertTrue(createProjectResult.getErrors().isEmpty());
		verify(projectService, times(1)).create(any(Project.class));
	}

	@Test
	public void testCreateProjectWithProjectDescription() {
		// return the passed Project object back from create
		when(projectService.create(any(Project.class))).thenAnswer(invocation -> {
			Project project = invocation.getArgument(0);
			project.setId(1L);
			return project;
		});

		String projectDescription = dgsQueryExecutor.executeAndExtractJsonPath(
				"mutation { createProject(input: { name: \"myproject\", projectDescription: \"mydescription\" }) { id name projectDescription } }",
				"data.createProject.projectDescription");

		assertEquals("mydescription", projectDescription);
	}

	@Test
	public void testCreateProjectMissingName() {
		ExecutionResult createProjectResult = dgsQueryExecutor.execute(
				"mutation { createProject(input : {projectDescription: \"My Project Description\"}) { id name } }");

		assertEquals(1, createProjectResult.getErrors().size());
		assertTrue(createProjectResult.getErrors().get(0) instanceof ValidationError);
		verifyNoInteractions(projectService);
	}

	@Test
	public void testCreateProjectInvalidAttribute() {
		ExecutionResult createProjectResult = dgsQueryExecutor.execute(
				"mutation { createProject(input : {name: \"myproject\", projectName: \"myproject\"}) { id name } }");

		assertEquals(1, createProjectResult.getErrors().size());
		assertTrue(createProjectResult.getErrors().get(0) instanceof ValidationError);
		verifyNoInteractions(projectService);
	}
}
