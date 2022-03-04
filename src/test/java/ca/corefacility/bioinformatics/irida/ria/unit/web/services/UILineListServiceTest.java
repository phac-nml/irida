package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UILineListService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UILineListServiceTest {
	private ProjectService projectService;
	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;
	private UILineListService service;

	@BeforeEach
	public void setUp() {
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		metadataTemplateService = mock(MetadataTemplateService.class);
		service = new UILineListService(projectService, sampleService, metadataTemplateService);
	}

	@Test
	public void testGetLineListEntries() {
		Sample sample1 = new Sample("sample1");
		sample1.setId(1L);
		Sample sample2 = new Sample("sample2");
		sample2.setId(2L);
		Project project = new Project("project");
		project.setId(1L);
		List<MetadataTemplateField> metadataTemplateFields = new ArrayList<MetadataTemplateField>();
		TableRequest tableRequest = new TableRequest();
		tableRequest.setCurrent(0);
		tableRequest.setPageSize(10);
		PageRequest pageRequest = PageRequest.of(0, 10);

		when(projectService.read(1L)).thenReturn(project);

		when(metadataTemplateService.getPermittedFieldsForCurrentUser(project, true))
				.thenReturn(metadataTemplateFields);

		when(sampleService.getLockedSamplesInProject(project)).thenReturn(new ArrayList<Long>());

		when(sampleService.getSamplesWithMetadataForProject(eq(project), eq(metadataTemplateFields),
				any(PageRequest.class)))
						.thenReturn(new PageImpl<Sample>(Arrays.asList(sample1, sample2), pageRequest, 2));

		service.getLineListEntries(project.getId(), tableRequest);
	}
}
