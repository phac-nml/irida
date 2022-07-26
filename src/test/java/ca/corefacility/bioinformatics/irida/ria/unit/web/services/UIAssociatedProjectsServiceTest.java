package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIAddAssociatedProjectException;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIRemoveAssociatedProjectException;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.AssociatedProject;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAssociatedProjectsService;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UIAssociatedProjectsServiceTest {
	private UIAssociatedProjectsService service;
	private ProjectService projectService;

	// DATA
	private final Long PROJECT_1_ID = 1L;
	private final Long PROJECT_2_ID = 2L;
	private final Long PROJECT_4_ID = 4L;
	private final Project PROJECT_1 = new Project("PROJECT_1");
	private final Project PROJECT_2 = new Project("PROJECT_2");
	private final Project PROJECT_3 = new Project("PROJECT_3");
	private final Project PROJECT_4 = new Project("PROJECT_4");

	@BeforeEach
	public void setUp() {
		projectService = mock(ProjectService.class);
		ProjectOwnerPermission projectOwnerPermission = mock(ProjectOwnerPermission.class);
		MessageSource messageSource = mock(MessageSource.class);
		service = new UIAssociatedProjectsService(projectService, projectOwnerPermission, messageSource);

		PROJECT_1.setId(PROJECT_1_ID);
		PROJECT_2.setId(PROJECT_2_ID);
		PROJECT_4.setId(PROJECT_4_ID);

		when(projectService.read(PROJECT_1_ID)).thenReturn(PROJECT_1);
		when(projectService.read(PROJECT_2_ID)).thenReturn(PROJECT_2);
		when(projectService.read(PROJECT_4_ID)).thenReturn(PROJECT_4);
		List<RelatedProjectJoin> relatedProjectJoins = ImmutableList.of(new RelatedProjectJoin(PROJECT_1, PROJECT_2),
				new RelatedProjectJoin(PROJECT_1, PROJECT_3), new RelatedProjectJoin(PROJECT_1, PROJECT_4));

		when(projectService.getRelatedProjects(PROJECT_1)).thenReturn(relatedProjectJoins);
	}

	@Test
	public void testGetAssociatedProjectsForProject() {
		List<AssociatedProject> relatedProjectJoins = service.getAssociatedProjectsForProject(PROJECT_1_ID);
		assertEquals(3, relatedProjectJoins.size());

		verify(projectService, times(1)).read(PROJECT_1_ID);
		verify(projectService, times(1)).getRelatedProjects(PROJECT_1);
	}

	@Test
	public void testAddAssociatedProject() throws UIAddAssociatedProjectException {
		service.addAssociatedProject(PROJECT_1_ID, PROJECT_2_ID, Locale.ENGLISH);

		verify(projectService, times(1)).addRelatedProject(PROJECT_1, PROJECT_2);

		when(projectService.read(PROJECT_4_ID)).thenThrow(new EntityNotFoundException("Cannot find project"));
		assertThrows(UIAddAssociatedProjectException.class, () -> {
			service.addAssociatedProject(PROJECT_1_ID, PROJECT_4_ID, Locale.ENGLISH);
		});
	}

	@Test
	public void testRemoveAssociatedProject() throws UIRemoveAssociatedProjectException {
		service.removeAssociatedProject(PROJECT_1_ID, PROJECT_2_ID, Locale.ENGLISH);
		verify(projectService, times(1)).read(PROJECT_1_ID);
		verify(projectService, times(1)).read(PROJECT_2_ID);
		verify(projectService, times(1)).removeRelatedProject(PROJECT_1, PROJECT_2);

		when(projectService.read(PROJECT_4_ID)).thenThrow(new EntityNotFoundException("Cannot find project"));

		assertThrows(UIRemoveAssociatedProjectException.class, () -> {
			service.removeAssociatedProject(PROJECT_1_ID, PROJECT_4_ID, Locale.ENGLISH);
		});

	}
}
