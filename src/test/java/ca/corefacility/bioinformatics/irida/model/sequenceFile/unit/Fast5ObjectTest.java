package ca.corefacility.bioinformatics.irida.model.sequenceFile.unit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.Fast5Object;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

import static org.junit.Assert.assertEquals;

/**
 * Test class ensuring fast5 files get created with their correct type
 */
public class Fast5ObjectTest {
	private static final String FILE_CONTENTS = "DATA CONTENTS";

	@Test
	public void testCreateZippedFile() throws IOException {
		Path zipFile = createZipFile();

		Fast5Object fast5Object = new Fast5Object(new SequenceFile(zipFile));

		assertEquals(Fast5Object.Fast5Type.ZIPPED, fast5Object.getFast5Type());
	}

	@Test
	public void testCreateSingleFile() throws IOException {
		Path zipFile = createSingleFile();

		Fast5Object fast5Object = new Fast5Object(new SequenceFile(zipFile));

		assertEquals(Fast5Object.Fast5Type.SINGLE, fast5Object.getFast5Type());
	}

	@Test
	public void testCreateUnknownFile() throws IOException {
		Path zipFile = createFile(".somethingelse");

		Fast5Object fast5Object = new Fast5Object(new SequenceFile(zipFile));

		assertEquals(Fast5Object.Fast5Type.UNKNOWN, fast5Object.getFast5Type());
	}

	private Path createZipFile() throws IOException {
		Path sequenceFile = createSingleFile();

		// compress the file
		Path compressed = Files.createTempFile(null, ".fast5.tar.gz");
		GZIPOutputStream out = new GZIPOutputStream(Files.newOutputStream(compressed));
		Files.copy(sequenceFile, out);
		out.close();

		return compressed;
	}

	private Path createSingleFile() throws IOException {
		return createFile(".fast5");
	}

	private Path createFile(String suffix) throws IOException {
		Path sequenceFile = Files.createTempFile(null, suffix);
		Files.write(sequenceFile, FILE_CONTENTS.getBytes());

		return sequenceFile;
	}
}
