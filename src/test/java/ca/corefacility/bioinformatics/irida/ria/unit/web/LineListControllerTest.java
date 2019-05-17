package ca.corefacility.bioinformatics.irida.ria.unit.web;

import java.security.Principal;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.LineListController;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.LineListPermissions;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import static org.mockito.Mockito.*;

/**
 * Unit test for {@link LineListController}
 */
public class LineListControllerTest {
	private LineListController lineListController;
	private ProjectService projectService;
	private MetadataTemplateService metadataTemplateService;
	private SampleService sampleService;
	private LineListPermissions permissions;
	private MessageSource messageSource;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		metadataTemplateService = mock(MetadataTemplateService.class);
		messageSource = mock(MessageSource.class);
		permissions = mock(LineListPermissions.class);
		lineListController = new LineListController(projectService, sampleService, metadataTemplateService, permissions,
				messageSource);
	}

	@Test
	public void testGetProjectMetadataTemplateFields() {
		long projectId = 1L;
		lineListController.getProjectMetadataTemplateFields(projectId, Locale.ENGLISH);
		verify(projectService, times(1)).read(projectId);
		verify(metadataTemplateService, times(1)).getMetadataFieldsForProject(any(Project.class));
	}

	@Test
	public void testGetAllProjectMetadataEntries() {
		long projectId = 1L;
		Principal principal = new Principal() {
			@Override
			public String getName() {
				return "Foobar";
			}
		};
		lineListController.getProjectSamplesMetadataEntries(projectId, principal);
		verify(sampleService, times(1)).getSamplesForProject(any(Project.class));
	}
}
