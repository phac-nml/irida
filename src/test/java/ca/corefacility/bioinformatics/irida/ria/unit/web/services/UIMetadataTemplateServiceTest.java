package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

import com.google.common.collect.ImmutableList;

import static org.mockito.Mockito.*;

public class UIMetadataTemplateServiceTest {
	private ProjectService projectService;
	private MetadataTemplateService metadataTemplateService;
	private UIMetadataTemplateService service;

	private final Project project = new Project("FUBAR");
	private final long PROJECT_ID = 1L;
	private final MetadataTemplate template = new MetadataTemplate("SNAFU", ImmutableList.of());
	private final long TEMPLATE_ID = 1L;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		metadataTemplateService = mock(MetadataTemplateService.class);
		MessageSource messageSource = mock(MessageSource.class);
		this.service = new UIMetadataTemplateService(projectService, metadataTemplateService, messageSource);

		when(projectService.read(PROJECT_ID)).thenReturn(project);
		when(metadataTemplateService.read(TEMPLATE_ID)).thenReturn(template);
	}

	@Test
	public void testGetProjectMetadataTemplates() {
		service.getProjectMetadataTemplates(PROJECT_ID);
		verify(projectService, times(1)).read(PROJECT_ID);
		verify(metadataTemplateService, timeout(1)).getMetadataTemplatesForProject(project);
	}

	@Test
	public void testGetMetadataTemplateDetails(){
		service.getMetadataTemplateDetails(TEMPLATE_ID);
		verify(metadataTemplateService, timeout(1)).read(TEMPLATE_ID);
	}
}
