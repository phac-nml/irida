package ca.corefacility.bioinformatics.irida.web.controller.test.unit.samples;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.ProjectMetadataResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleMetadataResponse;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleMetadataController;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RESTSampleMetadataControllerTest {

	private RESTSampleMetadataController metadataController;
	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;
	private ProjectService projectService;

	@BeforeEach
	public void setUp() {
		sampleService = mock(SampleService.class);
		metadataTemplateService = mock(MetadataTemplateService.class);
		projectService = mock(ProjectService.class);

		metadataController = new RESTSampleMetadataController(sampleService, metadataTemplateService, projectService);
	}

	@Test
	public void testReadProjectSampleMetadata() {
		Sample s1 = new Sample("s1");
		s1.setId(1L);
		s1.setModifiedDate(new Date());
		Sample s2 = new Sample("s2");
		s2.setId(2L);
		s2.setModifiedDate(new Date());
		Project p1 = new Project("p1");
		p1.setId(3L);

		MetadataTemplateField f1 = new MetadataTemplateField("f1", "text");
		List<MetadataTemplateField> fieldList = Lists.newArrayList(f1);

		ProjectSampleJoin psj1 = mock(ProjectSampleJoin.class);
		when(psj1.getSubject()).thenReturn(p1);
		when(psj1.getObject()).thenReturn(s1);
		ProjectSampleJoin psj2 = mock(ProjectSampleJoin.class);
		when(psj2.getSubject()).thenReturn(p1);
		when(psj2.getObject()).thenReturn(s2);

		Map<Long, Set<MetadataEntry>> metadata = new HashMap<>();
		metadata.put(s1.getId(), Sets.newHashSet(new MetadataEntry("value", "text", f1)));
		metadata.put(s2.getId(), Sets.newHashSet(new MetadataEntry("value2", "text", f1)));

		ProjectMetadataResponse projectMetadata = new ProjectMetadataResponse(p1, metadata);

		Page<ProjectSampleJoin> pageOne = new PageImpl<>(Lists.newArrayList(psj1, psj2));
		Page<ProjectSampleJoin> pageTwo = new PageImpl<>(Lists.newArrayList());

		when(projectService.read(p1.getId())).thenReturn(p1);
		when(metadataTemplateService.getPermittedFieldsForCurrentUser(p1, true)).thenReturn(fieldList);
		when(sampleService.getFilteredSamplesForProjects(eq(Arrays.asList(p1)), eq(Collections.emptyList()), eq(""),
				eq(""), eq(""), isNull(), isNull(), eq(0), any(Integer.class), any(Sort.class))).thenReturn(pageOne);
		when(sampleService.getFilteredSamplesForProjects(eq(Arrays.asList(p1)), eq(Collections.emptyList()), eq(""),
				eq(""), eq(""), isNull(), isNull(), eq(1), any(Integer.class), any(Sort.class))).thenReturn(pageTwo);
		when(sampleService.getMetadataForProjectSamples(eq(p1), anyList(), eq(fieldList))).thenReturn(projectMetadata);

		ResponseResource<ResourceCollection<SampleMetadataResponse>> responseResource = metadataController
				.getProjectSampleMetadata(p1.getId());

		ResourceCollection<SampleMetadataResponse> responses = responseResource.getResource();

		assertEquals(2, responses.size());
		for (SampleMetadataResponse response : responses) {
			assertEquals(1, response.getMetadata().size());
			assertTrue(response.getMetadata().keySet().contains(f1));
		}

		verify(projectService).read(p1.getId());
		verify(sampleService, times(1)).getMetadataForProjectSamples(p1, Lists.newArrayList(1L, 2L), fieldList);
	}

	@Test
	public void testGetSampleMetadata() {
		Sample s1 = new Sample("s1");
		s1.setId(1L);

		MetadataTemplateField f1 = new MetadataTemplateField("f1", "text");
		MetadataEntry entry1 = new MetadataEntry("val1", "text", f1);

		when(sampleService.read(s1.getId())).thenReturn(s1);
		when(sampleService.getMetadataForSample(s1)).thenReturn(Sets.newHashSet(entry1));

		ResponseResource<SampleMetadataResponse> sampleMetadata = metadataController.getSampleMetadata(s1.getId());

		SampleMetadataResponse response = sampleMetadata.getResource();

		verify(sampleService).getMetadataForSample(s1);

		Map<MetadataTemplateField, MetadataEntry> metadata = response.getMetadata();

		assertTrue(metadata.containsKey(f1));
	}

	@Test
	public void testAddSampleMetadata() {
		Sample s1 = new Sample("s1");
		s1.setId(1L);

		MetadataTemplateField f1 = new MetadataTemplateField("f1", "text");
		MetadataEntry entry1 = new MetadataEntry("val1", "text", f1);
		MetadataEntry entry2 = new MetadataEntry("val2", "text", f1);

		ImmutableMap<String, MetadataEntry> updateMap = ImmutableMap.of(f1.getLabel(), entry2);

		HashSet<MetadataEntry> originalSet = Sets.newHashSet(entry1);
		HashSet<MetadataEntry> newSet = Sets.newHashSet(entry2);

		when(sampleService.read(s1.getId())).thenReturn(s1);
		when(sampleService.getMetadataForSample(s1)).thenReturn(originalSet);
		when(metadataTemplateService.convertMetadataStringsToSet(updateMap)).thenReturn(newSet);

		metadataController.addSampleMetadata(s1.getId(), updateMap);

		verify(sampleService).mergeSampleMetadata(s1, newSet);
	}

	@Test
	public void testSaveSampleMetadata() {
		Sample s1 = new Sample("s1");
		s1.setId(1L);

		MetadataTemplateField f1 = new MetadataTemplateField("f1", "text");
		MetadataEntry entry1 = new MetadataEntry("val1", "text", f1);
		MetadataEntry entry2 = new MetadataEntry("val2", "text", f1);

		ImmutableMap<String, MetadataEntry> updateMap = ImmutableMap.of(f1.getLabel(), entry2);

		HashSet<MetadataEntry> originalSet = Sets.newHashSet(entry1);
		HashSet<MetadataEntry> newSet = Sets.newHashSet(entry2);

		when(sampleService.read(s1.getId())).thenReturn(s1);
		when(sampleService.getMetadataForSample(s1)).thenReturn(originalSet);
		when(metadataTemplateService.convertMetadataStringsToSet(updateMap)).thenReturn(newSet);

		metadataController.saveSampleMetadata(s1.getId(), updateMap);

		verify(sampleService).updateSampleMetadata(s1, newSet);
	}

}
