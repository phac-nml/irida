package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

import static org.mockito.Mockito.mock;

public class UIMetadataTemplateServiceTest {
	private UIMetadataTemplateService service;

	@Before
	public void setUp() {
		ProjectService projectService = mock(ProjectService.class);
		MetadataTemplateService metadataTemplateService = mock(MetadataTemplateService.class);
		MessageSource messageSource = mock(MessageSource.class);
		this.service = new UIMetadataTemplateService(projectService, metadataTemplateService, messageSource);

	}

	@Test
	public void testGetProjectMetadataTemplates() {

	}
}
