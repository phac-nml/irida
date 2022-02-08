package ca.corefacility.bioinformatics.irida.pipeline.results.updater.impl.unit;

import ca.corefacility.bioinformatics.irida.exceptions.AnalysisAlreadySetException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.updater.impl.SISTRSampleUpdater;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SISTRSampleUpdaterTest {

	private SISTRSampleUpdater updater;

	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;
	private IridaWorkflowsService iridaWorkflowsService;
	private IridaWorkflow iridaWorkflow;
	private IridaWorkflowDescription iridaWorkflowDescription;

	private UUID uuid = UUID.randomUUID();

	@BeforeEach
	public void setUp() throws IridaWorkflowNotFoundException {
		sampleService = mock(SampleService.class);
		metadataTemplateService = mock(MetadataTemplateService.class);
		iridaWorkflowsService = mock(IridaWorkflowsService.class);
		iridaWorkflow = mock(IridaWorkflow.class);
		iridaWorkflowDescription = mock(IridaWorkflowDescription.class);
		IridaFiles.setIridaFileStorageUtility(new IridaFileStorageLocalUtilityImpl());

		updater = new SISTRSampleUpdater(metadataTemplateService, sampleService, iridaWorkflowsService);

		when(iridaWorkflowsService.getIridaWorkflow(uuid)).thenReturn(iridaWorkflow);

		when(iridaWorkflow.getWorkflowDescription()).thenReturn(iridaWorkflowDescription);
		when(iridaWorkflowDescription.getVersion()).thenReturn("0.1");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testUpdaterPassed() throws PostProcessingException, AnalysisAlreadySetException {
		ImmutableMap<String, String> expectedResults = ImmutableMap.<String, String>builder().put(
				"SISTR serovar (v0.1)", "Enteritidis")
				.put("SISTR cgMLST Subspecies (v0.1)", "enterica")
				.put("SISTR QC Status (v0.1)", "PASS")
				.put("SISTR O-antigen (v0.1)", "1,9,12")
				.put("SISTR Serogroup (v0.1)", "D1")
				.put("SISTR cgMLST Alleles Matching Genome (v0.1)", "317")
				.put("SISTR H1 (v0.1)", "g,m")
				.put("SISTR H2 (v0.1)", "-")
				.build();

		Path outputPath = Paths.get("src/test/resources/files/sistr-predictions-pass.json");

		AnalysisOutputFile outputFile = new AnalysisOutputFile(outputPath, null, null, null);

		Analysis analysis = new Analysis(null, ImmutableMap.of("sistr-predictions", outputFile), null, null);
		AnalysisSubmission submission = AnalysisSubmission.builder(uuid)
				.inputFiles(ImmutableSet.of(new SingleEndSequenceFile(null)))
				.build();

		submission.setAnalysis(analysis);

		Sample sample = new Sample();
		sample.setId(1L);

		Set<MetadataEntry> metadataSet = Sets.newHashSet(
				new MetadataEntry("Value1", "text", new MetadataTemplateField("SISTR Field", "text")));

		when(metadataTemplateService.convertMetadataStringsToSet(any(Map.class))).thenReturn(metadataSet);

		updater.update(Lists.newArrayList(sample), submission);

		ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);

		//this is the important bit.  Ensures the correct values got pulled from the file
		verify(metadataTemplateService).convertMetadataStringsToSet(mapCaptor.capture());
		Map<String, MetadataEntry> metadata = mapCaptor.getValue();

		int found = 0;
		for (Map.Entry<String, MetadataEntry> e : metadata.entrySet()) {

			if (expectedResults.containsKey(e.getKey())) {
				String expected = expectedResults.get(e.getKey());

				MetadataEntry value = e.getValue();

				assertEquals(expected, value.getValue(), "metadata values should match");
				found++;
			}
		}
		assertEquals(expectedResults.keySet().size(), found,
				"should have found the same number of results");

		ArgumentCaptor<Set> setCaptor = ArgumentCaptor.forClass(Set.class);
		// this bit just ensures the merged data got saved
		verify(sampleService).mergeSampleMetadata(eq(sample), setCaptor.capture());

		Set<MetadataEntry> value = setCaptor.getValue();

		assertEquals(metadataSet.iterator()
				.next(), value.iterator()
				.next());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testUpdateFailResultsNullST() throws PostProcessingException, AnalysisAlreadySetException {
		ImmutableMap<String, String> expectedResults = ImmutableMap.<String, String>builder().put(
				"SISTR serovar (v0.1)", "-:-:-")
				.put("SISTR QC Status (v0.1)", "FAIL")
				.put("SISTR cgMLST Sequence Type (v0.1)", "")
				.build();

		Path outputPath = Paths.get("src/test/resources/files/sistr-predictions-fail-null-cgmlstst.json");

		AnalysisOutputFile outputFile = new AnalysisOutputFile(outputPath, null, null, null);

		Analysis analysis = new Analysis(null, ImmutableMap.of("sistr-predictions", outputFile), null, null);
		AnalysisSubmission submission = AnalysisSubmission.builder(uuid)
				.inputFiles(ImmutableSet.of(new SingleEndSequenceFile(null)))
				.build();

		submission.setAnalysis(analysis);

		Sample sample = new Sample();
		sample.setId(1L);

		Set<MetadataEntry> metadataSet = Sets.newHashSet(
				new MetadataEntry("Value1", "text", new MetadataTemplateField("SISTR Field", "text")));

		when(metadataTemplateService.convertMetadataStringsToSet(any(Map.class))).thenReturn(metadataSet);

		updater.update(Lists.newArrayList(sample), submission);

		ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);

		//this is the important bit.  Ensures the correct values got pulled from the file
		verify(metadataTemplateService).convertMetadataStringsToSet(mapCaptor.capture());
		Map<String, MetadataEntry> metadata = mapCaptor.getValue();

		int found = 0;
		for (Map.Entry<String, MetadataEntry> e : metadata.entrySet()) {

			if (expectedResults.containsKey(e.getKey())) {
				String expected = expectedResults.get(e.getKey());

				MetadataEntry value = e.getValue();

				assertEquals(expected, value.getValue(), "metadata values should match");
				found++;
			}
		}
		assertEquals(expectedResults.keySet().size(), found,
				"should have found the same number of results");

		ArgumentCaptor<Set> setCaptor = ArgumentCaptor.forClass(Set.class);
		// this bit just ensures the merged data got saved
		verify(sampleService).mergeSampleMetadata(eq(sample), setCaptor.capture());

		Set<MetadataEntry> value = setCaptor.getValue();

		assertEquals(metadataSet.iterator()
				.next(), value.iterator()
				.next());
	}

	@Test
	public void testUpdaterBadFile() throws PostProcessingException, AnalysisAlreadySetException {
		Path outputPath = Paths.get("src/test/resources/files/snp_tree.tree");

		AnalysisOutputFile outputFile = new AnalysisOutputFile(outputPath, null, null, null);

		Analysis analysis = new Analysis(null, ImmutableMap.of("sistr-predictions", outputFile), null, null);
		AnalysisSubmission submission = AnalysisSubmission.builder(uuid)
				.inputFiles(ImmutableSet.of(new SingleEndSequenceFile(null)))
				.build();

		submission.setAnalysis(analysis);

		Sample sample = new Sample();
		sample.setId(1L);

		assertThrows(PostProcessingException.class, () -> {
			updater.update(Lists.newArrayList(sample), submission);
		});
	}


	@Test
	public void testUpdaterNoFile() throws PostProcessingException, AnalysisAlreadySetException, IOException {
		Path outputPath = Paths.get("src/test/resources/files/not_really_a_file.txt");

		AnalysisOutputFile outputFile = new AnalysisOutputFile(outputPath, null, null, null);

		Analysis analysis = new Analysis(null, ImmutableMap.of("sistr-predictions", outputFile), null, null);
		AnalysisSubmission submission = AnalysisSubmission.builder(uuid)
				.inputFiles(ImmutableSet.of(new SingleEndSequenceFile(null)))
				.build();

		submission.setAnalysis(analysis);

		Sample sample = new Sample();
		sample.setId(1L);

		assertThrows(PostProcessingException.class, () -> {
			updater.update(Lists.newArrayList(sample), submission);
		});
	}
}
