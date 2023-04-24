package ca.corefacility.bioinformatics.irida.processing.impl.unit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.processing.impl.GzipFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link GzipFileProcessor}.
 */

public class GzipFileProcessorTest {

	private GzipFileProcessor fileProcessor;
	private SequenceFileRepository sequenceFileRepository;
	private static final String FILE_CONTENTS = ">test read\nACGTACTCATG";
	private IridaFileStorageUtility iridaFileStorageUtility;

	@BeforeEach
	public void setUp() {
		sequenceFileRepository = mock(SequenceFileRepository.class);
		iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl(true);
		fileProcessor = new GzipFileProcessor(sequenceFileRepository, Boolean.FALSE, iridaFileStorageUtility);
		IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);
	}

	@Test
	public void testExceptionBehaviours() throws IOException {
		final SequenceFile sf = constructSequenceFile();

		// compress the file, update the sequence file reference
		Path uncompressed = sf.getFile();
		Path compressed = Files.createTempFile(null, ".gz");
		GZIPOutputStream out = new GZIPOutputStream(Files.newOutputStream(compressed));
		Files.copy(uncompressed, out);
		out.close();
		sf.setFile(compressed);

		SingleEndSequenceFile so = new SingleEndSequenceFile(sf);
		when(sequenceFileRepository.save(any(SequenceFile.class))).thenThrow(new RuntimeException());
		assertThrows(FileProcessorException.class, () -> {
			fileProcessor.process(so);
		});
	}

	@Test
	public void testDeleteOriginalFile() throws IOException {
		fileProcessor = new GzipFileProcessor(sequenceFileRepository, Boolean.TRUE, iridaFileStorageUtility);
		final SequenceFile sf = constructSequenceFile();

		// compress the file, update the sequence file reference
		Path uncompressed = sf.getFile();
		Path compressed = Files.createTempFile(null, ".gz");
		GZIPOutputStream out = new GZIPOutputStream(Files.newOutputStream(compressed));
		Files.copy(uncompressed, out);
		out.close();
		sf.setFile(compressed);

		SingleEndSequenceFile so = new SingleEndSequenceFile(sf);

		fileProcessor.process(so);

		verify(sequenceFileRepository, times(1)).save(any(SequenceFile.class));

		assertFalse(Files.exists(compressed), "The original file should have been deleted.");
	}

	@Test
	public void handleUncompressedFile() throws IOException {
		// the file processor just shouldn't do *anything*.
		SequenceFile sf = constructSequenceFile();
		Path original = sf.getFile();

		SingleEndSequenceFile so = new SingleEndSequenceFile(sf);

		fileProcessor.process(so);

		verify(sequenceFileRepository, times(0)).save(any(SequenceFile.class));

		Files.deleteIfExists(sf.getFile());
		Files.deleteIfExists(original);
	}

	@Test
	public void handleCompressedFileWithGzExtension() throws IOException {
		// the file processor should decompress the file, then update the
		// sequence file in the database.
		SequenceFile sf = constructSequenceFile();
		SequenceFile sfUpdated = new SequenceFile();
		sfUpdated.setFile(sf.getFile());
		final Long id = 1L;
		sf.setId(id);

		// compress the file, update the sequence file reference
		Path uncompressed = sf.getFile();
		Path compressed = Files.createTempFile(null, ".gz");
		GZIPOutputStream out = new GZIPOutputStream(Files.newOutputStream(compressed));
		Files.copy(uncompressed, out);
		out.close();

		when(sequenceFileRepository.save(sf)).thenReturn(sfUpdated);
		SingleEndSequenceFile so = new SingleEndSequenceFile(sf);

		sf.setFile(compressed);

		fileProcessor.process(so);

		ArgumentCaptor<SequenceFile> argument = ArgumentCaptor.forClass(SequenceFile.class);
		verify(sequenceFileRepository).save(argument.capture());
		SequenceFile modified = argument.getValue();

		verify(sequenceFileRepository).save(sf);
		String uncompressedFileContents = new String(Files.readAllBytes(modified.getFile()));
		assertEquals(FILE_CONTENTS, uncompressedFileContents,
				"uncompressed file and file in database should be the same.");
		Files.delete(uncompressed);
		assertTrue(Files.exists(compressed), "Original compressed file should not have been deleted.");
		Files.delete(compressed);
	}

	@Test
	public void handleCompressedFileWithoutGzExtension() throws IOException {
		// the file processor should decompress the file, then update the
		// sequence file in the database.
		SequenceFile sf = constructSequenceFile();
		SequenceFile sfUpdated = new SequenceFile();
		sfUpdated.setFile(sf.getFile());

		final Long id = 1L;
		sf.setId(id);

		// compress the file, update the sequence file reference
		Path uncompressed = sf.getFile();
		Path compressed = Files.createTempFile(null, null);
		GZIPOutputStream out = new GZIPOutputStream(Files.newOutputStream(compressed));
		Files.copy(uncompressed, out);
		out.close();

		sf.setFile(compressed);

		SingleEndSequenceFile so = new SingleEndSequenceFile(sf);

		fileProcessor.process(so);

		ArgumentCaptor<SequenceFile> argument = ArgumentCaptor.forClass(SequenceFile.class);
		verify(sequenceFileRepository).save(argument.capture());
		SequenceFile modified = argument.getValue();

		String uncompressedFileContents = new String(Files.readAllBytes(modified.getFile()));
		assertEquals(FILE_CONTENTS, uncompressedFileContents,
				"uncompressed file and file in database should be the same.");
		Files.delete(uncompressed);
		if (Files.exists(compressed)) {
			Files.delete(compressed);
		}
	}

	private SequenceFile constructSequenceFile() throws IOException {
		SequenceFile sf = new SequenceFile();
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FILE_CONTENTS.getBytes());
		sf.setFile(sequenceFile);
		return sf;
	}
}
