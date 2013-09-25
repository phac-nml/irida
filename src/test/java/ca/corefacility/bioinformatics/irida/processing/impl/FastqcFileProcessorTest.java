package ca.corefacility.bioinformatics.irida.processing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;

/**
 * Tests for {@link FastqcFileProcessor}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class FastqcFileProcessorTest {
	private FastqcFileProcessor fileProcessor;
	private SequenceFileRepository sequenceFileRepository;
	private static final String SEQUENCE = "ACGTACGTN";
	private static final String FASTQ_FILE_CONTENTS = "@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
			+ SEQUENCE + "\n+\n?????????";
	private static final String FASTA_FILE_CONTENTS = ">test read\n" + SEQUENCE;

	@Before
	public void setUp() {
		sequenceFileRepository = mock(SequenceFileRepository.class);
		fileProcessor = new FastqcFileProcessor(sequenceFileRepository);
	}

	@Test
	public void testHandleFastaFile() throws IOException {
		// fastqc fails to handle fasta files (there's no quality scores,
		// dummy), but that's A-OK.
		Path fasta = Files.createTempFile(null, null);
		Files.write(fasta, FASTA_FILE_CONTENTS.getBytes());
		SequenceFile sf = new SequenceFile(fasta);

		try {
			fileProcessor.process(sf);
			fail();
		} catch (FileProcessorException e) {
		} catch (Exception e) {
			fail();
		}

		Files.deleteIfExists(fasta);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testHandleFastqFile() throws IOException {
		// fastqc shouldn't barf on a fastq file.
		Path fastq = Files.createTempFile(null, null);
		Files.write(fastq, FASTQ_FILE_CONTENTS.getBytes());

		@SuppressWarnings("rawtypes")
		ArgumentCaptor<Map> argument = ArgumentCaptor.forClass(Map.class);

		SequenceFile sf = new SequenceFile(fastq);
		sf.setId(1L);
		try {
			fileProcessor.process(sf);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		verify(sequenceFileRepository).update(eq(1L), argument.capture());
		Map<String, Object> updatedProperties = argument.getValue();
		assertEquals("GC Content was not set correctly.", (short) 50, updatedProperties.get("gcContent"));
		assertEquals("Filtered sequences was not 0.", 0, updatedProperties.get("filteredSequences"));
		assertEquals("File type was not correct.", "Conventional base calls", updatedProperties.get("fileType"));
		assertEquals("Max length was not correct.", SEQUENCE.length(), updatedProperties.get("maxLength"));
		assertEquals("Min length was not correct.", SEQUENCE.length(), updatedProperties.get("minLength"));
		assertEquals("Total sequences was not correct.", 2, updatedProperties.get("totalSequences"));
		assertEquals("Encoding was not correct.", "Illumina <1.3", updatedProperties.get("encoding"));
		assertEquals("Total number of bases was not correct.", Long.valueOf(SEQUENCE.length() * 2),
				updatedProperties.get("totalBases"));

		assertNotNull("Per-base quality score chart was not created.",
				updatedProperties.get("perBaseQualityScoreChart"));
		assertTrue("Per-base quality score chart was created, but was empty.",
				((byte[]) updatedProperties.get("perBaseQualityScoreChart")).length > 0);

		assertNotNull("Per-sequence quality score chart was not created.",
				updatedProperties.get("perSequenceQualityScoreChart"));
		assertTrue("Per-sequence quality score chart was created, but was empty.",
				((byte[]) updatedProperties.get("perSequenceQualityScoreChart")).length > 0);

		assertNotNull("Duplication level chart was not created.", updatedProperties.get("duplicationLevelChart"));
		assertTrue("Duplication level chart was not created.",
				((byte[]) updatedProperties.get("duplicationLevelChart")).length > 0);

		ArgumentCaptor<OverrepresentedSequence> overrepresentedSequenceCaptor = ArgumentCaptor
				.forClass(OverrepresentedSequence.class);

		verify(sequenceFileRepository).addOverrepresentedSequenceToSequenceFile(any(SequenceFile.class),
				overrepresentedSequenceCaptor.capture());
		OverrepresentedSequence overrepresentedSequence = overrepresentedSequenceCaptor.getValue();
		assertEquals("Sequence was not the correct sequence.", SEQUENCE, overrepresentedSequence.getSequence());
		assertEquals("The count was not correct.", 2, overrepresentedSequence.getOverrepresentedSequenceCount());
		assertEquals("The percent was not correct.", BigDecimal.valueOf(100.), overrepresentedSequence.getPercentage());

		Files.deleteIfExists(fastq);
	}
}
