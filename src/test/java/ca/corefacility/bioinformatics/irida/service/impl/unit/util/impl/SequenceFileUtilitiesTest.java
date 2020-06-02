package ca.corefacility.bioinformatics.irida.service.impl.unit.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;
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
		Long sequenceFileLength = sequenceFileUtilities.countSequenceFileLengthInBases(file);
		assertEquals("Should have 4 bases.", Long.valueOf(4), sequenceFileLength);
	}

	@Test
	public void testGetSequenceFileLengthBadFile() throws URISyntaxException, IOException {
		Path file = Files.createTempFile("seqfile", ".fasta");
		
		try {
			sequenceFileUtilities.countSequenceFileLengthInBases(file);
			fail("Should throw IllegalArgumentException for empty reference file.");
		} catch (final IllegalArgumentException e) {
			
		} catch (final Throwable t) {
			fail("Should throw IllegalArgumentException for empty reference file, but threw [" + t.getClass() + "] instead.");
		}
		
		Files.deleteIfExists(file);
	}
	
	@Test(expected = UnsupportedReferenceFileContentError.class)
	public void testGetSequenceFileLengthAmbiguousBases() throws URISyntaxException {
		Path file = Paths.get(getClass().getResource(
				"/ca/corefacility/bioinformatics/irida/service/testReferenceAmbiguous.fasta").toURI());
		sequenceFileUtilities.countSequenceFileLengthInBases(file);
	}
}
