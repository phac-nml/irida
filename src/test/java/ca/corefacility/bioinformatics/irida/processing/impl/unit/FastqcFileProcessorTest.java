package ca.corefacility.bioinformatics.irida.processing.impl.unit;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.util.ReflectionUtils;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.Fast5Object;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.processing.impl.FastqcFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisOutputFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link FastqcFileProcessor}.
 */
public class FastqcFileProcessorTest {
	private FastqcFileProcessor fileProcessor;
	private SequenceFileRepository sequenceFileRepository;
	private AnalysisOutputFileRepository outputFileRepository;
	private MessageSource messageSource;
	private static final Logger logger = LoggerFactory.getLogger(FastqcFileProcessorTest.class);

	private static final String SEQUENCE = "ACGTACGTN";
	private static final String FASTQ_FILE_CONTENTS =
			"@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n" + SEQUENCE + "\n+\n?????????";
	private static final String FASTA_FILE_CONTENTS = ">test read\n" + SEQUENCE;
	private IridaFileStorageUtility iridaFileStorageUtility;

	@BeforeEach
	public void setUp() {
		messageSource = mock(MessageSource.class);
		sequenceFileRepository = mock(SequenceFileRepository.class);
		outputFileRepository = mock(AnalysisOutputFileRepository.class);
		iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl(true);
		IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);
		fileProcessor = new FastqcFileProcessor(messageSource, sequenceFileRepository, outputFileRepository,
				iridaFileStorageUtility);
	}

	@Test
	public void testHandleFastaFile() throws IOException {
		// fastqc fails to handle fasta files (there's no quality scores,
		// dummy), but that's A-OK.
		Path fasta = Files.createTempFile(null, null);
		Files.write(fasta, FASTA_FILE_CONTENTS.getBytes());
		SequenceFile sf = new SequenceFile(fasta);

		sf.setId(1L);
		Runtime.getRuntime().addShutdownHook(new DeleteFileOnExit(fasta));
		SingleEndSequenceFile so = new SingleEndSequenceFile(sf);

		assertThrows(FileProcessorException.class, () -> {
			fileProcessor.process(so);
		});
	}

	@Test
	public void testHandleFast5File() throws IOException {
		//ensure we don't process zipped fast5 files
		SequenceFile sf = new SequenceFile(null);

		Fast5Object obj = new Fast5Object(sf);

		obj.setFast5Type(Fast5Object.Fast5Type.SINGLE);
		assertTrue(fileProcessor.shouldProcessFile(obj), "should want to process single fast5 file)");

		obj.setFast5Type(Fast5Object.Fast5Type.ZIPPED);
		assertFalse(fileProcessor.shouldProcessFile(obj), "should not want to process zipped fast5 file)");

		obj.setFast5Type(Fast5Object.Fast5Type.UNKNOWN);
		assertFalse(fileProcessor.shouldProcessFile(obj), "should not want to process unknown fast5 file)");
	}

	@Test
	public void testHandleFastqFile() throws IOException, IllegalArgumentException, IllegalAccessException {
		// fastqc shouldn't barf on a fastq file.
		Path fastq = Files.createTempFile(null, null);
		Files.write(fastq, FASTQ_FILE_CONTENTS.getBytes());
		Runtime.getRuntime().addShutdownHook(new DeleteFileOnExit(fastq));

		ArgumentCaptor<SequenceFile> argument = ArgumentCaptor.forClass(SequenceFile.class);

		SequenceFile sf = new SequenceFile(fastq);
		sf.setId(1L);

		SingleEndSequenceFile so = new SingleEndSequenceFile(sf);
		try {
			fileProcessor.process(so);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		verify(sequenceFileRepository).saveMetadata(argument.capture());
		SequenceFile updatedFile = argument.getValue();

		final Field fastqcAnalysis = ReflectionUtils.findField(SequenceFile.class, "fastqcAnalysis");
		ReflectionUtils.makeAccessible(fastqcAnalysis);
		AnalysisFastQC updated = (AnalysisFastQC) fastqcAnalysis.get(updatedFile);
		assertEquals(Short.valueOf((short) 50), updated.getGcContent(), "GC Content was not set correctly.");
		assertEquals(Integer.valueOf(0), updated.getFilteredSequences(), "Filtered sequences was not 0.");
		assertEquals("Conventional base calls", updated.getFileType(), "File type was not correct.");
		assertEquals(Integer.valueOf(SEQUENCE.length()), updated.getMaxLength(), "Max length was not correct.");
		assertEquals(Integer.valueOf(SEQUENCE.length()), updated.getMinLength(), "Min length was not correct.");
		assertEquals(Integer.valueOf(2), updated.getTotalSequences(), "Total sequences was not correct.");
		assertEquals("Sanger / Illumina 1.9", updated.getEncoding(), "Encoding was not correct.");
		assertEquals(Long.valueOf(SEQUENCE.length() * 2), updated.getTotalBases(),
				"Total number of bases was not correct.");

		verify(outputFileRepository, times(3)).save(any(AnalysisOutputFile.class));

		assertNotNull(updated.getAnalysisOutputFileNames().contains("perBaseQualityScoreChart"),
				"Per-base quality score chart was not created.");

		assertNotNull(updated.getAnalysisOutputFileNames().contains("perSequenceQualityScoreChart"),
				"Per-sequence quality score chart was not created.");

		assertNotNull(updated.getAnalysisOutputFileNames().contains("duplicationLevelChart"),
				"Duplication level chart was not created.");

		Iterator<OverrepresentedSequence> ovrs = updated.getOverrepresentedSequences().iterator();
		assertTrue(ovrs.hasNext(), "No overrepresented sequences added to analysis.");
		OverrepresentedSequence overrepresentedSequence = updated.getOverrepresentedSequences().iterator().next();
		assertEquals(SEQUENCE, overrepresentedSequence.getSequence(), "Sequence was not the correct sequence.");
		assertEquals(2, overrepresentedSequence.getOverrepresentedSequenceCount(), "The count was not correct.");
		assertEquals(BigDecimal.valueOf(100.), overrepresentedSequence.getPercentage(), "The percent was not correct.");

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
				logger.debug("Couldn't delete path [" + fileToDelete
						+ "]. This should be safe to ignore; FastQC opens an input stream on the file and never closes it.");
			}
		}

	}
}
