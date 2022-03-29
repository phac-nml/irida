package ca.corefacility.bioinformatics.irida.ria.unit.web;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.ProjectMetadataResponse;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.LineListController;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.EntriesResponse;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
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
	private ProjectOwnerPermission ownerPermission;
	private MessageSource messageSource;

	@BeforeEach
	public void setUp() {
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		metadataTemplateService = mock(MetadataTemplateService.class);
		messageSource = mock(MessageSource.class);
		ownerPermission = mock(ProjectOwnerPermission.class);
		lineListController = new LineListController(projectService, sampleService, metadataTemplateService,
				ownerPermission, messageSource);
	}

	@Test
	public void testGetProjectMetadataTemplateFields() {
		long projectId = 1L;
		Project project = new Project("p1");

		when(projectService.read(anyLong())).thenReturn(project);
		lineListController.getProjectMetadataTemplateFields(projectId, Locale.ENGLISH);
		verify(projectService, times(1)).read(projectId);
		verify(metadataTemplateService, times(1)).getPermittedFieldsForCurrentUser(any(Project.class), eq(true));
	}

	@Test
	public void testGetAllProjectMetadataEntries() {
		long projectId = 1L;
		Project project = new Project("p1");
		project.setId(1L);
		Sample s1 = new Sample("s1");
		s1.setId(1L);
		s1.setModifiedDate(new Date());
		Sample s2 = new Sample("s2");
		s2.setId(2L);
		s2.setModifiedDate(new Date());
		MetadataTemplateField field = new MetadataTemplateField("field", "text");
		List<MetadataTemplateField> fieldList = Lists.newArrayList(field);

		ProjectSampleJoin psj1 = mock(ProjectSampleJoin.class);
		when(psj1.getSubject()).thenReturn(project);
		when(psj1.getObject()).thenReturn(s1);
		ProjectSampleJoin psj2 = mock(ProjectSampleJoin.class);
		when(psj2.getSubject()).thenReturn(project);
		when(psj2.getObject()).thenReturn(s2);

		Map<Long, Set<MetadataEntry>> metadata = new HashMap<>();
		metadata.put(s1.getId(), Sets.newHashSet(new MetadataEntry("value", "text", field)));
		metadata.put(s2.getId(), Sets.newHashSet(new MetadataEntry("value2", "text", field)));
		ProjectMetadataResponse projectMetadata = new ProjectMetadataResponse(project, metadata);

		Page<ProjectSampleJoin> pageOne = new PageImpl<>(Lists.newArrayList(psj1, psj2));
		Page<ProjectSampleJoin> pageTwo = new PageImpl<>(Lists.newArrayList());

		when(projectService.read(projectId)).thenReturn(project);
		when(metadataTemplateService.getPermittedFieldsForCurrentUser(project, true)).thenReturn(fieldList);
		when(sampleService.getFilteredSamplesForProjects(eq(Arrays.asList(project)), eq(Collections.emptyList()),
				eq(""), eq(""), eq(""), isNull(), isNull(), eq(0), any(Integer.class), any(Sort.class)))
						.thenReturn(pageOne);
		when(sampleService.getFilteredSamplesForProjects(eq(Arrays.asList(project)), eq(Collections.emptyList()),
				eq(""), eq(""), eq(""), isNull(), isNull(), eq(1), any(Integer.class), any(Sort.class)))
						.thenReturn(pageTwo);
		when(sampleService.getMetadataForProjectSamples(eq(project), anyList(), eq(fieldList)))
				.thenReturn(projectMetadata);
		EntriesResponse response = lineListController.getProjectSamplesMetadataEntries(projectId, 0, 5000);

		assertEquals(2, response.getTotal());

		verify(sampleService, times(1)).getMetadataForProjectSamples(project, Lists.newArrayList(1L, 2L), fieldList);
	}
}
