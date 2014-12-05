package ca.corefacility.bioinformatics.irida.processing.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.impl.GzipFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;

/**
 * Tests for {@link GzipFileProcessor}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class GzipFileProcessorTest {

	private GzipFileProcessor fileProcessor;
	private SequenceFileRepository sequenceFileRepository;
	private static final String FILE_CONTENTS = ">test read\nACGTACTCATG";

	@Before
	public void setUp() {
		sequenceFileRepository = mock(SequenceFileRepository.class);
		fileProcessor = new GzipFileProcessor(sequenceFileRepository);
	}

	@Test
	public void handleUncompressedFile() throws IOException {
		// the file processor just shouldn't do *anything*.
		SequenceFile sf = constructSequenceFile();
		Path original = sf.getFile();

		when(sequenceFileRepository.findOne(any(Long.class))).thenReturn(sf);

		fileProcessor.process(1L);

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
		when(sequenceFileRepository.findOne(id)).thenReturn(sf);

		sf.setFile(compressed);

		fileProcessor.process(id);

		ArgumentCaptor<SequenceFile> argument = ArgumentCaptor.forClass(SequenceFile.class);
		verify(sequenceFileRepository).save(argument.capture());
		SequenceFile modified = argument.getValue();

		verify(sequenceFileRepository).save(sf);
		String uncompressedFileContents = new String(Files.readAllBytes(modified.getFile()));
		assertEquals("uncompressed file and file in database should be the same.", FILE_CONTENTS,
				uncompressedFileContents);
		Files.delete(uncompressed);
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

		when(sequenceFileRepository.findOne(id)).thenReturn(sf);

		fileProcessor.process(id);

		ArgumentCaptor<SequenceFile> argument = ArgumentCaptor.forClass(SequenceFile.class);
		verify(sequenceFileRepository).save(argument.capture());
		SequenceFile modified = argument.getValue();

		String uncompressedFileContents = new String(Files.readAllBytes(modified.getFile()));
		assertEquals("uncompressed file and file in database should be the same.", FILE_CONTENTS,
				uncompressedFileContents);
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
