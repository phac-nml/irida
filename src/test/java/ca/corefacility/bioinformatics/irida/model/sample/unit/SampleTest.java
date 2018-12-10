package ca.corefacility.bioinformatics.irida.model.sample.unit;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.PipelineProvidedMetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

public class SampleTest {

	private Sample sample1;
	private Sample sample2;

	private MetadataTemplateField field1;
	private MetadataTemplateField field2;

	private MetadataEntry entry1;
	private MetadataEntry entry2;

	private PipelineProvidedMetadataEntry pipelineEntry1;
	private PipelineProvidedMetadataEntry pipelineEntry2;

	private AnalysisSubmission submission1;

	@Before
	public void setup() {
		sample1 = new Sample("newsample");

		field1 = new MetadataTemplateField("field1", "text");
		entry1 = new MetadataEntry("entry1", "text");

		field2 = new MetadataTemplateField("field2", "text");
		entry2 = new MetadataEntry("entry2", "text");

		Map<MetadataTemplateField, MetadataEntry> metadataMap = Maps.newHashMap();
		metadataMap.put(field1, entry1);
		metadataMap.put(field2, entry2);

		sample1.setMetadata(metadataMap);

		sample2 = new Sample("newsample2");

		submission1 = AnalysisSubmission.builder(UUID.randomUUID()).inputFiles(Sets.newHashSet(new SequenceFilePair()))
				.build();
		submission1.setId(1L);

		pipelineEntry1 = new PipelineProvidedMetadataEntry("pipelineEntry1", "text", submission1);
		pipelineEntry2 = new PipelineProvidedMetadataEntry("pipelineEntry2", "text", submission1);

		Map<MetadataTemplateField, MetadataEntry> metadataMap2 = Maps.newHashMap();
		metadataMap2.put(field1, pipelineEntry1);
		metadataMap2.put(field2, pipelineEntry2);

		sample2.setMetadata(metadataMap2);
	}

	@Test
	public void testMergeMetadataSuccess() {
		Map<MetadataTemplateField, MetadataEntry> inputMetadata = Maps.newHashMap();
		MetadataEntry entry = new MetadataEntry("entry2", "text");

		inputMetadata.put(field1, entry);

		sample1.mergeMetadata(inputMetadata);

		assertEquals("Metadata map does not have correct number of entries", 2, sample1.getMetadata().size());
		assertEquals("Updated metadata entry does not match", entry, sample1.getMetadata().get(field1));
		assertEquals("Non-updated metadata entry does not match", this.entry2, sample1.getMetadata().get(field2));
	}

	@Test
	public void testMergeMetadataPipelineSuccess() {
		Map<MetadataTemplateField, MetadataEntry> inputMetadata = Maps.newHashMap();

		AnalysisSubmission submission = AnalysisSubmission.builder(UUID.randomUUID())
				.inputFiles(Sets.newHashSet(new SequenceFilePair())).build();
		submission.setId(2L);

		PipelineProvidedMetadataEntry entry = new PipelineProvidedMetadataEntry("entry2", "text", submission);

		inputMetadata.put(field1, entry);

		sample2.mergeMetadata(inputMetadata);

		assertEquals("Metadata map does not have correct number of entries", 2, sample2.getMetadata().size());
		assertEquals("Updated metadata entry does not match", entry, sample2.getMetadata().get(field1));

		PipelineProvidedMetadataEntry actualEntry = (PipelineProvidedMetadataEntry) sample2.getMetadata().get(field1);

		assertEquals("Updated metadata entry does not point to correct submission", new Long(2),
				actualEntry.getSubmission().getId());
		assertEquals("Non-updated metadata entry does not match", this.pipelineEntry2,
				sample2.getMetadata().get(field2));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMergeMetadataPipelineFailIncompatibleEntries() {
		Map<MetadataTemplateField, MetadataEntry> inputMetadata = Maps.newHashMap();

		MetadataEntry entry = new MetadataEntry("entry2", "text");

		inputMetadata.put(field1, entry);

		sample2.mergeMetadata(inputMetadata);
	}
}
