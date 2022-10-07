package ca.corefacility.bioinformatics.irida.ria.web.models.project;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.models.MinimalModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.ModelKeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectMinimalModelTest {
	@Test
	public void testProjectMinimalModel() {
		Project project = mock(Project.class);
		long projectId = 1L;
		when(project.getId()).thenReturn(projectId);
		String projectName = "PROJECT NAME";
		when(project.getName()).thenReturn(projectName);

		ProjectMinimalModel projectMinimalModel = new ProjectMinimalModel(project);
		assertThat(projectMinimalModel).isInstanceOf(MinimalModel.class);
		assertEquals(projectId, projectMinimalModel.getId(), "Id should not be changed");
		assertEquals(ModelKeys.Project.label + project.getId(), projectMinimalModel.getKey(),
				"Key should be concatenated with id");
		assertEquals(projectName, projectMinimalModel.getName(), "Name should not be changed");
	}
}