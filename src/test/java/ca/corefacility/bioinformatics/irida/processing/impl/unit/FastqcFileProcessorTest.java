package ca.corefacility.bioinformatics.irida.processing.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.processing.impl.FastqcFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;

/**
 * Tests for {@link FastqcFileProcessor}.
 * 
 * 
 */
public class FastqcFileProcessorTest {
	private FastqcFileProcessor fileProcessor;
	private AnalysisRepository analysisRepository;
	private SequenceFileRepository sequenceFileRepository;
	private MessageSource messageSource;
	private static final Logger logger = LoggerFactory.getLogger(FastqcFileProcessorTest.class);

	private static final String SEQUENCE = "ACGTACGTN";
	private static final String FASTQ_FILE_CONTENTS = "@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
			+ SEQUENCE + "\n+\n?????????";
	private static final String FASTA_FILE_CONTENTS = ">test read\n" + SEQUENCE;

	@Before
	public void setUp() {
		analysisRepository = mock(AnalysisRepository.class);
		messageSource = mock(MessageSource.class);
		sequenceFileRepository = mock(SequenceFileRepository.class);
		fileProcessor = new FastqcFileProcessor(analysisRepository, messageSource, sequenceFileRepository);
	}

	@Test(expected = FileProcessorException.class)
	public void testHandleFastaFile() throws IOException {
		// fastqc fails to handle fasta files (there's no quality scores,
		// dummy), but that's A-OK.
		Path fasta = Files.createTempFile(null, null);
		Files.write(fasta, FASTA_FILE_CONTENTS.getBytes());
		SequenceFile sf = new SequenceFile(fasta);
		sf.setId(1L);
		Runtime.getRuntime().addShutdownHook(new DeleteFileOnExit(fasta));
		when(sequenceFileRepository.findOne(1L)).thenReturn(sf);

		fileProcessor.process(1L);
	}

	@Test
	public void testHandleFastqFile() throws IOException {
		// fastqc shouldn't barf on a fastq file.
		Path fastq = Files.createTempFile(null, null);
		Files.write(fastq, FASTQ_FILE_CONTENTS.getBytes());
		Runtime.getRuntime().addShutdownHook(new DeleteFileOnExit(fastq));

		ArgumentCaptor<AnalysisFastQC> argument = ArgumentCaptor.forClass(AnalysisFastQC.class);

		SequenceFile sf = new SequenceFile(fastq);
		sf.setId(1L);
		when(sequenceFileRepository.findOne(1L)).thenReturn(sf);
		try {
			fileProcessor.process(1L);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		verify(analysisRepository).save(argument.capture());
		AnalysisFastQC updated = argument.getValue();
		assertEquals("GC Content was not set correctly.", Short.valueOf((short) 50), updated.getGcContent());
		assertEquals("Filtered sequences was not 0.", Integer.valueOf(0), updated.getFilteredSequences());
		assertEquals("File type was not correct.", "Conventional base calls", updated.getFileType());
		assertEquals("Max length was not correct.", Integer.valueOf(SEQUENCE.length()), updated.getMaxLength());
		assertEquals("Min length was not correct.", Integer.valueOf(SEQUENCE.length()), updated.getMinLength());
		assertEquals("Total sequences was not correct.", Integer.valueOf(2), updated.getTotalSequences());
		assertEquals("Encoding was not correct.", "Illumina <1.3", updated.getEncoding());
		assertEquals("Total number of bases was not correct.", Long.valueOf(SEQUENCE.length() * 2),
				updated.getTotalBases());

		assertNotNull("Per-base quality score chart was not created.", updated.getPerBaseQualityScoreChart());
		assertTrue("Per-base quality score chart was created, but was empty.",
				((byte[]) updated.getPerBaseQualityScoreChart()).length > 0);

		assertNotNull("Per-sequence quality score chart was not created.", updated.getPerSequenceQualityScoreChart());
		assertTrue("Per-sequence quality score chart was created, but was empty.",
				((byte[]) updated.getPerSequenceQualityScoreChart()).length > 0);

		assertNotNull("Duplication level chart was not created.", updated.getDuplicationLevelChart());
		assertTrue("Duplication level chart was not created.", ((byte[]) updated.getDuplicationLevelChart()).length > 0);


		Iterator<OverrepresentedSequence> ovrs = updated.getOverrepresentedSequences().iterator();
		assertTrue("No overrepresented sequences added to analysis.", ovrs.hasNext());
		OverrepresentedSequence overrepresentedSequence = updated.getOverrepresentedSequences().iterator().next();
		assertEquals("Sequence was not the correct sequence.", SEQUENCE, overrepresentedSequence.getSequence());
		assertEquals("The count was not correct.", 2, overrepresentedSequence.getOverrepresentedSequenceCount());
		assertEquals("The percent was not correct.", BigDecimal.valueOf(100.), overrepresentedSequence.getPercentage());

	}

	private static final class DeleteFileOnExit extends Thread {

		private final Path fileToDelete;

		public DeleteFileOnExit(Path fileToDelete) {
			this.fileToDelete = fileToDelete;
		}

		@Override
		public void run() {
			try {
				Files.deleteIfExists(fileToDelete);
			} catch (IOException e) {
				logger.debug("Couldn't delete path ["
						+ fileToDelete
						+ "]. This should be safe to ignore; FastQC opens an input stream on the file and never closes it.");
			}
		}

	}
}
