package ca.corefacility.bioinformatics.irida.ria.unit.web;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoinMinimal;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectMinimal;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.SampleMinimal;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.LineListController;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.EntriesResponse;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.github.jsonldjava.shaded.com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

	@BeforeEach
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
		Project project = new Project("p1");
		when(projectService.read(anyLong())).thenReturn(project);
		lineListController.getProjectMetadataTemplateFields(projectId, Locale.ENGLISH);
		verify(projectService, times(1)).read(projectId);
		verify(metadataTemplateService, times(1)).getMetadataFieldsForProject(any(Project.class));
	}

	@Test
	public void testGetAllProjectMetadataEntries() {
		long projectId = 1L;
		Project project = new Project("p1");
		ProjectMinimal p1 = mock(ProjectMinimal.class);
		when(p1.getId()).thenReturn(1L);
		when(p1.getName()).thenReturn("p1");
		SampleMinimal s1 = mock(SampleMinimal.class);
		when(s1.getId()).thenReturn(1L);
		when(s1.getSampleName()).thenReturn("s1");
		when(s1.getCreatedDate()).thenReturn(new Date());
		when(s1.getModifiedDate()).thenReturn(new Date());
		SampleMinimal s2 = mock(SampleMinimal.class);
		when(s2.getId()).thenReturn(2L);
		when(s2.getSampleName()).thenReturn("s2");
		when(s2.getCreatedDate()).thenReturn(new Date());
		when(s2.getModifiedDate()).thenReturn(new Date());
		MetadataTemplateField field = new MetadataTemplateField("field", "text");

		ProjectSampleJoinMinimal psjm1 = mock(ProjectSampleJoinMinimal.class);
		when(psjm1.getSubject()).thenReturn(p1);
		when(psjm1.getObject()).thenReturn(s1);
		ProjectSampleJoinMinimal psjm2 = mock(ProjectSampleJoinMinimal.class);
		when(psjm2.getSubject()).thenReturn(p1);
		when(psjm2.getObject()).thenReturn(s2);

		Map<Long, Set<MetadataEntry>> metadata = new HashMap<>();
		metadata.put(s1.getId(), Sets.newHashSet(new MetadataEntry("value", "text", field)));
		metadata.put(s2.getId(), Sets.newHashSet(new MetadataEntry("value2", "text", field)));

		Page<ProjectSampleJoinMinimal> pageOne = new PageImpl<>(Lists.newArrayList(psjm1, psjm2));
		Page<ProjectSampleJoinMinimal> pageTwo = new PageImpl<>(Lists.newArrayList());

		when(projectService.read(projectId)).thenReturn(project);
		when(sampleService.getFilteredProjectSamples(eq(Arrays.asList(project)), eq(Collections.emptyList()), eq(""),
				eq(""), eq(""), isNull(), isNull(), eq(0), any(Integer.class), any(Sort.class))).thenReturn(pageOne);
		when(sampleService.getFilteredProjectSamples(eq(Arrays.asList(project)), eq(Collections.emptyList()), eq(""),
				eq(""), eq(""), isNull(), isNull(), eq(1), any(Integer.class), any(Sort.class))).thenReturn(pageTwo);
		when(sampleService.getMetadataForProjectSamples(eq(project), anyList())).thenReturn(metadata);
		EntriesResponse response = lineListController.getProjectSamplesMetadataEntries(projectId, 0, 5000);

		assertEquals(2, response.getTotal());

		verify(sampleService, times(1)).getMetadataForProjectSamples(project, Lists.newArrayList(1L, 2L));
	}
}
