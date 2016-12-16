package ca.corefacility.bioinformatics.irida.processing.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.processing.impl.ChecksumFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;

public class ChecksumFileProcessorTest {
	private SequencingObjectRepository objectRepository;
	private ChecksumFileProcessor fileProcessor;
	private SequenceFileRepository sequenceFileRepository;
	private static final String FILE_CONTENTS = ">test read\nACGTACTCATG";
	private static final String CHECKSUM = "aeaa0755dc44b393ffe12f02e9bd42b0169b12ca9c15708085db6a4ac9110ee0";

	@Before
	public void setUp() {
		sequenceFileRepository = mock(SequenceFileRepository.class);
		objectRepository = mock(SequencingObjectRepository.class);
		fileProcessor = new ChecksumFileProcessor(objectRepository, sequenceFileRepository);
	}

	@Test
	public void testChecksumCreated() throws IOException {
		final SequenceFile sf = constructSequenceFile();

		when(objectRepository.findOne(any(Long.class))).thenReturn(new SingleEndSequenceFile(sf));

		fileProcessor.process(1L);

		ArgumentCaptor<SequenceFile> fileCaptor = ArgumentCaptor.forClass(SequenceFile.class);
		verify(sequenceFileRepository).save(fileCaptor.capture());

		SequenceFile file = fileCaptor.getValue();

		assertEquals("checksums should be equal", CHECKSUM, file.getUploadChecksum());
	}

	@Test(expected = FileProcessorException.class)
	public void testFileNotExists() throws IOException {
		final SequenceFile sf = new SequenceFile(Paths.get("/reallyfakefile"));

		when(objectRepository.findOne(any(Long.class))).thenReturn(new SingleEndSequenceFile(sf));

		fileProcessor.process(1L);
	}

	private SequenceFile constructSequenceFile() throws IOException {
		SequenceFile sf = new SequenceFile();
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FILE_CONTENTS.getBytes());
		sf.setFile(sequenceFile);
		return sf;
	}
}
