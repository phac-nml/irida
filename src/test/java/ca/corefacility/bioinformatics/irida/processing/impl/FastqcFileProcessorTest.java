package ca.corefacility.bioinformatics.irida.processing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

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
	private static final String FASTQ_FILE_CONTENTS = "@testread\nACGTACGT\n+\n????????";
	private static final String FASTA_FILE_CONTENTS = ">test read\nACGTACGT";

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
			fail();
		}

		verify(sequenceFileRepository).update(eq(1L), argument.capture());
		Map<String, Object> updatedProperties = argument.getValue();
		assertEquals((short) 50, updatedProperties.get("gcContent"));
		assertEquals(0, updatedProperties.get("filteredSequences"));
		assertEquals("Conventional base calls", updatedProperties.get("fileType"));
		assertEquals(8, updatedProperties.get("maxLength"));
		assertEquals(8, updatedProperties.get("minLength"));
		assertEquals(1, updatedProperties.get("totalSequences"));
		assertEquals("Illumina <1.3", updatedProperties.get("encoding"));
		assertNotNull(updatedProperties.get("perBaseQualityScoreChart"));
		assertTrue(((byte[]) updatedProperties.get("perBaseQualityScoreChart")).length > 0);

		assertNotNull(updatedProperties.get("perSequenceQualityScoreChart"));
		assertTrue(((byte[]) updatedProperties.get("perSequenceQualityScoreChart")).length > 0);

		Files.deleteIfExists(fastq);
	}
}
