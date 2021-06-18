package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.ProjectMetadataResponse;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.LineListController;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.UISampleMetadata;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Unit test for {@link LineListController}
 */
public class LineListControllerTest {
	private LineListController lineListController;
	private ProjectService projectService;
	private MetadataTemplateService metadataTemplateService;
	private SampleService sampleService;
	private UpdateSamplePermission updateSamplePermission;
	private ProjectOwnerPermission ownerPermission;
	private MessageSource messageSource;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		metadataTemplateService = mock(MetadataTemplateService.class);
		updateSamplePermission = mock(UpdateSamplePermission.class);
		messageSource = mock(MessageSource.class);
		ownerPermission = mock(ProjectOwnerPermission.class);
		lineListController = new LineListController(projectService, sampleService, metadataTemplateService,
				updateSamplePermission, ownerPermission, messageSource);
	}

	@Test
	public void testGetProjectMetadataTemplateFields() {
		long projectId = 1L;
		lineListController.getProjectMetadataTemplateFields(projectId, Locale.ENGLISH);
		verify(projectService, times(1)).read(projectId);
		verify(metadataTemplateService, times(1)).getPermittedFieldsForCurrentUser(any(Project.class));
	}

	@Test
	public void testGetAllProjectMetadataEntries() {
		long projectId = 1L;
		Project project = new Project("p1");
		Sample s1 = new Sample("s1");
		s1.setId(1L);
		s1.setModifiedDate(new Date());
		Sample s2 = new Sample("s2");
		s2.setId(2L);
		s2.setModifiedDate(new Date());
		MetadataTemplateField field = new MetadataTemplateField("field", "text");
		List<MetadataTemplateField> fieldList = Lists.newArrayList(field);

		Map<Long, Set<MetadataEntry>> metadata = new HashMap<>();
		metadata.put(s1.getId(), Sets.newHashSet(new MetadataEntry("value", "text", field)));
		metadata.put(s2.getId(), Sets.newHashSet(new MetadataEntry("value2", "text", field)));

		when(projectService.read(projectId)).thenReturn(project);
		when(sampleService.getMetadataForProject(project, fieldList)).thenReturn(new ProjectMetadataResponse(project,metadata));
		when(sampleService.getSamplesForProjectShallow(project)).thenReturn(Lists.newArrayList(s1, s2));
		when(metadataTemplateService.getPermittedFieldsForCurrentUser(project)).thenReturn(fieldList);
		List<UISampleMetadata> projectSamplesMetadataEntries = lineListController.getProjectSamplesMetadataEntries(
				projectId);

		assertEquals(2, projectSamplesMetadataEntries.size());

		verify(sampleService, times(1)).getMetadataForProject(project, fieldList);
	}
}
