package ca.corefacility.bioinformatics.irida.service.impl.unit.util.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.service.util.SequenceFileUtilities;
import ca.corefacility.bioinformatics.irida.service.util.impl.BioJavaSequenceFileUtilitiesImpl;

public class SequenceFileUtilitiesTest {
	private SequenceFileUtilities sequenceFileUtilities;

	@Before
	public void setUp() {
		sequenceFileUtilities = new BioJavaSequenceFileUtilitiesImpl();
	}

	@Test
	public void testGetSequenceFileLength() throws URISyntaxException {
		Path file = Paths.get(getClass().getResource(
				"/ca/corefacility/bioinformatics/irida/service/testReference.fasta").toURI());
		Long sequenceFileLength = sequenceFileUtilities.getSequenceFileLength(file);
		assertEquals(new Long(4), sequenceFileLength);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetSequenceFileLengthBadFile() throws URISyntaxException, IOException {
		Path file = Files.createTempFile("seqfile", ".fasta");
		file.toFile().deleteOnExit();
		Long sequenceFileLength = sequenceFileUtilities.getSequenceFileLength(file);
		assertEquals(new Long(4), sequenceFileLength);
	}
}
