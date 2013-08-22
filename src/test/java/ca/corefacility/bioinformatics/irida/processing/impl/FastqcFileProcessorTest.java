package ca.corefacility.bioinformatics.irida.processing.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;

/**
 * Tests for {@link FastqcFileProcessor}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class FastqcFileProcessorTest {
	private FastqcFileProcessor fileProcessor;
	private static final String FASTQ_FILE_CONTENTS = "@testread\nACGTACGT\n+\n????????";
	private static final String FASTA_FILE_CONTENTS = ">test read\nACGTACGT";

	@Before
	public void setUp() {
		fileProcessor = new FastqcFileProcessor();
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

	@Test
	public void testHandleFastqFile() throws IOException {
		// fastqc shouldn't barf on a fastq file.
		Path fastq = Files.createTempFile(null, null);
		Files.write(fastq, FASTQ_FILE_CONTENTS.getBytes());

		SequenceFile sf = new SequenceFile(fastq);
		try {
			fileProcessor.process(sf);
		} catch (Exception e) {
			fail();
		}

		Files.deleteIfExists(fastq);
	}
}
