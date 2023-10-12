package ca.corefacility.bioinformatics.irida.pipeline.results.updater.impl.unit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.exceptions.AnalysisAlreadySetException;
import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.updater.impl.BioHanselSampleUpdater;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BioHanselSampleUpdaterTest {

	private BioHanselSampleUpdater bioHanselSampleUpdater;
	private MetadataTemplateService metadataTemplateService;
	private SampleService sampleService;

	@BeforeEach
	public void setUp() {
		metadataTemplateService = mock(MetadataTemplateService.class);
		sampleService = mock(SampleService.class);
		bioHanselSampleUpdater = new BioHanselSampleUpdater(metadataTemplateService, sampleService);
		IridaFiles.setIridaFileStorageUtility(new IridaFileStorageLocalUtilityImpl(true));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateSucess() throws AnalysisAlreadySetException, PostProcessingException {
		// @formatter:off
		final Map<String, String> expectedResults = ImmutableMap.of(
				"bio_hansel/heidelberg/v0.5.0/Subtype", "2.2.1.1.2",
				"bio_hansel/heidelberg/v0.5.0/Average Tile Coverage", "27.3961038961",
				"bio_hansel/heidelberg/v0.5.0/QC Status", "FAIL",
				"bio_hansel/heidelberg/v0.5.0/QC Message", "FAIL: Missing Tiles Error 1: 25.25% missing tiles; more than 5.00% missing tiles threshold. Okay coverage depth (27.6 >= 20.0 expected), but this may be the wrong serovar or species for scheme \"heidelberg\" | FAIL: Mixed Sample Error 2: Mixed subtypes found: \"1; 2.2.1.1.2\"."
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

		Set<MetadataEntry> metadataSet = expectedResults.entrySet()
				.stream()
				.map(entry -> new MetadataEntry(entry.getValue(), "text",
						new MetadataTemplateField(entry.getKey(), "text")))
				.collect(Collectors.toSet());

		/*final Map<MetadataTemplateField, MetadataEntry> metadataMap = expectedResults.entrySet()
				.stream()
				.collect(Collectors.toMap(entry -> new MetadataTemplateField(entry.getKey(), "text"),
						entry -> new MetadataEntry(entry.getValue(), "text")));
		 */
		//when(metadataTemplateService.getMetadataMap(any(Map.class))).thenReturn(metadataMap);

		when(metadataTemplateService.convertMetadataStringsToSet(anyMap())).thenReturn(metadataSet);

		bioHanselSampleUpdater.update(Lists.newArrayList(sample), submission);

		ArgumentCaptor<Map<String, MetadataEntry>> mapCaptor = ArgumentCaptor.forClass(Map.class);

		//this is the important bit.  Ensures the correct values got pulled from the file
		verify(metadataTemplateService).convertMetadataStringsToSet(mapCaptor.capture());
		Map<String, MetadataEntry> metadataEntryMap = mapCaptor.getValue();

		AtomicInteger found = new AtomicInteger();
		metadataEntryMap.forEach((key, entry) -> {
			if (expectedResults.containsKey(key)) {
				assertEquals(expectedResults.get(key), entry.getValue(), "Metadata entry values should be equal!");
				found.getAndIncrement();
			}
		});
		assertEquals(expectedResults.size(), found.get(), "Should have the same number of metadata entries");

		ArgumentCaptor<Set<MetadataEntry>> setCaptor = ArgumentCaptor.forClass(Set.class);
		// this bit just ensures the merged data got saved
		verify(sampleService).mergeSampleMetadata(eq(sample), setCaptor.capture());

		Set<MetadataEntry> value = setCaptor.getValue();

		assertEquals(metadataSet.iterator().next(), value.iterator().next());
	}

}