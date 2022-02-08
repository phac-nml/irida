package ca.corefacility.bioinformatics.irida.processing.impl.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.processing.impl.ChecksumFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

public class ChecksumFileProcessorTest {
	private ChecksumFileProcessor fileProcessor;
	private SequenceFileRepository sequenceFileRepository;
	private static final String FILE_CONTENTS = ">test read\nACGTACTCATG";
	private static final String CHECKSUM = "aeaa0755dc44b393ffe12f02e9bd42b0169b12ca9c15708085db6a4ac9110ee0";
	private IridaFileStorageUtility iridaFileStorageUtility;

	@BeforeEach
	public void setUp() {
		sequenceFileRepository = mock(SequenceFileRepository.class);
		iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl();
		fileProcessor = new ChecksumFileProcessor(sequenceFileRepository, iridaFileStorageUtility);
		IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);
	}

	@Test
	public void testChecksumCreated() throws IOException {
		final SequenceFile sf = constructSequenceFile();

		SingleEndSequenceFile so = new SingleEndSequenceFile(sf);

		fileProcessor.process(so);

		ArgumentCaptor<SequenceFile> fileCaptor = ArgumentCaptor.forClass(SequenceFile.class);
		verify(sequenceFileRepository).saveMetadata(fileCaptor.capture());

		SequenceFile file = fileCaptor.getValue();

		assertEquals(CHECKSUM, file.getUploadSha256(), "checksums should be equal");
	}

	@Test
	public void testFileNotExists() throws IOException {
		final SequenceFile sf = new SequenceFile(Paths.get("/reallyfakefile"));

		SingleEndSequenceFile so = new SingleEndSequenceFile(sf);

		assertThrows(FileProcessorException.class, () -> {
			fileProcessor.process(so);
		});
	}

	private SequenceFile constructSequenceFile() throws IOException {
		SequenceFile sf = new SequenceFile();
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FILE_CONTENTS.getBytes());
		sf.setFile(sequenceFile);
		return sf;
	}
}
