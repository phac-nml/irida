package ca.corefacility.bioinformatics.irida.model.sample.metadata.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.PipelineProvidedMetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

public class MetadataEntryTest {

	@Test
	public void testMergeSameMetadataEntry() {
		MetadataEntry e1 = new MetadataEntry("e1", "text");
		MetadataEntry e2 = new MetadataEntry("e2", "text");

		e1.merge(e2);

		assertEquals("e2", e1.getValue(), "Metadata entries did not merge");
	}

	@Test
	public void testMergeSamePipelineMetadataEntry() {
		AnalysisSubmission s1 = AnalysisSubmission.builder(UUID.randomUUID())
				.inputFiles(Sets.newHashSet(new SequenceFilePair())).build();
		s1.setId(1L);
		AnalysisSubmission s2 = AnalysisSubmission.builder(UUID.randomUUID())
				.inputFiles(Sets.newHashSet(new SequenceFilePair())).build();
		s2.setId(2L);

		PipelineProvidedMetadataEntry e1 = new PipelineProvidedMetadataEntry("e1", "text", s1);
		PipelineProvidedMetadataEntry e2 = new PipelineProvidedMetadataEntry("e2", "text", s2);

		e1.merge(e2);

		assertEquals("e2", e1.getValue(), "Metadata entries did not merge");
		assertEquals(Long.valueOf(2), e1.getSubmission().getId(), "Metadata entries did not merge");
	}
	
	@Test
	public void testMergeSamePipelineMetadataEntryDifferentTypes() {
		AnalysisSubmission s1 = AnalysisSubmission.builder(UUID.randomUUID())
				.inputFiles(Sets.newHashSet(new SequenceFilePair())).build();
		s1.setId(1L);
		AnalysisSubmission s2 = AnalysisSubmission.builder(UUID.randomUUID())
				.inputFiles(Sets.newHashSet(new SequenceFilePair())).build();
		s2.setId(2L);

		PipelineProvidedMetadataEntry e1 = new PipelineProvidedMetadataEntry("e1", "text1", s1);
		PipelineProvidedMetadataEntry e2 = new PipelineProvidedMetadataEntry("e2", "text2", s2);

		e1.merge(e2);

		assertEquals("e2", e1.getValue(), "Metadata entries did not merge");
		assertEquals("text2", e1.getType(), "Metadata entries did not merge");
		assertEquals(Long.valueOf(2), e1.getSubmission().getId(), "Metadata entries did not merge");
	}

	@Test
	public void testMergeDifferentEntryPipeline() {
		AnalysisSubmission s2 = AnalysisSubmission.builder(UUID.randomUUID())
				.inputFiles(Sets.newHashSet(new SequenceFilePair())).build();
		s2.setId(2L);

		MetadataEntry e1 = new MetadataEntry("e1", "text");
		PipelineProvidedMetadataEntry e2 = new PipelineProvidedMetadataEntry("e2", "text", s2);

		assertThrows(IllegalArgumentException.class, () -> { e1.merge(e2); });
	}

	@Test
	public void testMergeDifferentPipelineEntry() {
		AnalysisSubmission s1 = AnalysisSubmission.builder(UUID.randomUUID())
				.inputFiles(Sets.newHashSet(new SequenceFilePair())).build();
		s1.setId(2L);

		PipelineProvidedMetadataEntry e1 = new PipelineProvidedMetadataEntry("e1", "text", s1);
		MetadataEntry e2 = new MetadataEntry("e2", "text");

		assertThrows(IllegalArgumentException.class, () -> { e1.merge(e2); });
	}
}
