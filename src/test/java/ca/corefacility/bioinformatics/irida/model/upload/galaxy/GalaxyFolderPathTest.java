package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests for {@link GalaxyFolderPath}.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyFolderPathTest {
	
	/**
	 * Tests for case of null path name.
	 */
	@Test(expected=NullPointerException.class)
	public void testNullName() {
		new GalaxyFolderPath(null);
	}
	
	/**
	 * Tests for case of too short path name.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testShortName() {
		new GalaxyFolderPath("a");
	}
	
	/**
	 * Tests for a valid name.
	 */
	@Test
	public void testValidName() {
		new GalaxyFolderPath("Abc123_-/");
	}
	
	/**
	 * Tests for a blacklist of invalid characters.
	 */
	@Test
	public void testBlackList() {
		for (char c : GalaxyFolderPath.BLACKLIST) {
			try {
				new GalaxyFolderPath("ATLEAST3" + c);
				fail("failed to throw exception with invalid character \"" + c + "\"");
			} catch (IllegalArgumentException e){}
		}
	}
}
