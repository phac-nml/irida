package ca.corefacility.bioinformatics.irida.pipeline.results.impl.unit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.exceptions.AnalysisAlreadySetException;
import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.impl.BioHanselSampleUpdater;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class BioHanselSampleUpdaterTest {

	private BioHanselSampleUpdater bioHanselSampleUpdater;
	private MetadataTemplateService metadataTemplateService;
	private SampleService sampleService;
	private ProjectService projectService;

	@Before
	public void setUp() {
		metadataTemplateService = mock(MetadataTemplateService.class);
		sampleService = mock(SampleService.class);
		projectService = mock(ProjectService.class);
		bioHanselSampleUpdater = new BioHanselSampleUpdater(metadataTemplateService, sampleService, projectService);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateSucess() throws AnalysisAlreadySetException, PostProcessingException {
		final String expectedTemplateName = "bio_hansel/heidelberg/v0.5.0";
		// @formatter:off
		final Map<String, String> expectedResults = ImmutableMap.of(
				"bio_hansel/heidelberg/v0.5.0/Subtype", "2.2.1.1.2",
				"bio_hansel/heidelberg/v0.5.0/Average Tile Coverage", "27.3961038961",
				"bio_hansel/heidelberg/v0.5.0/QC Status", "FAIL",
				"bio_hansel/heidelberg/v0.5.0/QC Message","FAIL: Missing Tiles Error 1: 25.25% missing tiles; more than 5.00% missing tiles threshold. Okay coverage depth (27.6 >= 20.0 expected), but this may be the wrong serovar or species for scheme \"heidelberg\" | FAIL: Mixed Sample Error 2: Mixed subtypes found: \"1; 2.2.1.1.2\"."
		);
		final ImmutableList<String> expectedFields = ImmutableList.of(
				"bio_hansel/heidelberg/v0.5.0/Subtype",
				"bio_hansel/heidelberg/v0.5.0/QC Status",
				"bio_hansel/heidelberg/v0.5.0/QC Message",
				"bio_hansel/heidelberg/v0.5.0/Average Tile Coverage"
		);
		// @formatter:on
		final Path path = Paths.get(
				"src/test/resources/files/bio_hansel/SRR1203042-bio_hansel-results-heidelberg-0.5.0.json");
		final AnalysisOutputFile analysisOutputFile = new AnalysisOutputFile(path, null, null, null);
		final Analysis analysis = new Analysis(null, ImmutableMap.of("bio_hansel-results.json", analysisOutputFile),
				null, null);
		AnalysisSubmission submission = AnalysisSubmission.builder(UUID.randomUUID())
				.inputFiles(ImmutableSet.of(new SingleEndSequenceFile(null)))
				.build();
		submission.setAnalysis(analysis);
		final Project project = new Project();
		project.setName("project1");
		project.setId(1L);
		final Project projectWithTemplate = new Project();
		projectWithTemplate.setName("project2");
		projectWithTemplate.setId(2L);
		final Sample sample = new Sample();
		sample.setId(1L);

		Join<Project, Sample> projectSampleJoin = createProjectSampleJoin(project, sample);
		Join<Project, Sample> projectWithTemplateSampleJoin = createProjectSampleJoin(projectWithTemplate, sample);

		when(projectService.getProjectsForSample(sample)).thenReturn(
				ImmutableList.of(projectSampleJoin, projectWithTemplateSampleJoin));
		when(metadataTemplateService.getMetadataTemplatesForProject(project)).thenReturn(new ArrayList<>());
		final MetadataTemplate existingTemplate = new MetadataTemplate(expectedTemplateName, expectedFields.stream()
				.map(x -> new MetadataTemplateField(x, "text"))
				.collect(Collectors.toList()));
		when(metadataTemplateService.getMetadataTemplatesForProject(projectWithTemplate)).thenReturn(
				Collections.singletonList(
						new ProjectMetadataTemplateJoin(projectWithTemplate, existingTemplate)));

		final Map<MetadataTemplateField, MetadataEntry> metadataMap = expectedResults.entrySet()
				.stream()
				.collect(Collectors.toMap(entry -> new MetadataTemplateField(entry.getKey(), "text"),
						entry -> new MetadataEntry(entry.getValue(), "text")));
		when(metadataTemplateService.getMetadataMap(any(Map.class))).thenReturn(metadataMap);
		bioHanselSampleUpdater.update(Lists.newArrayList(sample), submission);

		ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);

		//this is the important bit.  Ensures the correct values got pulled from the file
		verify(metadataTemplateService).getMetadataMap(mapCaptor.capture());
		Map<String, MetadataEntry> metadataEntryMap = mapCaptor.getValue();

		AtomicInteger found = new AtomicInteger();
		metadataEntryMap.forEach((key, entry) -> {
			if (expectedResults.containsKey(key)) {
				assertEquals("Metadata entry values should be equal!", expectedResults.get(key), entry.getValue());
				found.getAndIncrement();
			}
		});
		assertEquals("Should have the same number of metadata entries", expectedResults.size(), found.get());

		// this bit just ensures the merged data got saved
		verify(sampleService).updateFields(eq(sample.getId()), mapCaptor.capture());
		Map<MetadataTemplateField, MetadataEntry> value = (Map<MetadataTemplateField, MetadataEntry>) mapCaptor.getValue()
				.get("metadata");

		assertEquals(metadataMap.keySet()
				.iterator()
				.next(), value.keySet()
				.iterator()
				.next());

		// capture the created MetadataTemplate and check values
		final ArgumentCaptor<MetadataTemplate> templateCaptor = ArgumentCaptor.forClass(
				MetadataTemplate.class);
		verify(metadataTemplateService).createMetadataTemplateInProject(templateCaptor.capture(),
				eq(project));

		assertEquals("Unexpected value metadata template name", expectedTemplateName,
				templateCaptor.getValue()
						.getName());

		assertEquals("Metadata fields should be equal and in the same order", expectedFields,
				templateCaptor.getValue()
						.getFields()
						.stream()
						.map(MetadataTemplateField::getLabel)
						.collect(Collectors.toList()));

		// Should never call createMetadataTemplateInProject on Project[id=2] since it has a bio_hansel Metadata Template already
		verify(metadataTemplateService, never()).createMetadataTemplateInProject(any(MetadataTemplate.class), eq(projectWithTemplate));
	}

	private Join<Project, Sample> createProjectSampleJoin(Project project, Sample sample) {
		return new Join<Project, Sample>() {
			@Override
			public Project getSubject() {
				return project;
			}

			@Override
			public Sample getObject() {
				return sample;
			}

			@Override
			public Date getTimestamp() {
				return null;
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Date getCreatedDate() {
				return null;
			}
		};
	}

}