package ca.corefacility.bioinformatics.irida.ria.unit.webpacker;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackEntries;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackerManifestParser;

public class WebpackerManifestParserTest {
	public static final String FILE_PATH = "src/test/resources/files/assets-manifest.json";

	/**
	 * The test file (copied from src/main/webapp/dist/assets-manifest.json on August 24, 2021) has
	 * exactly 45 entries.
	 */
	public static final int NUM_ENTRIES = 45;

	@Test
	public void TestParseWebpackManifestFile() {
		Path path = Paths.get(FILE_PATH);
		WebpackEntries entries = WebpackerManifestParser.parseWebpackManifestFile(path.toFile());
		Assert.assertEquals("Should be 53 entries", NUM_ENTRIES, entries.keySet()
				.size());
		entries.forEach((name, files) -> {
			Assert.assertTrue("Every entry should have a JavaScript file", files.getJavascript()
					.size() > 0);
		});
	}
}
