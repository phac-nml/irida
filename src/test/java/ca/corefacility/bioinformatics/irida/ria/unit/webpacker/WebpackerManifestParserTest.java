package ca.corefacility.bioinformatics.irida.ria.unit.webpacker;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.exceptions.WebpackParserException;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackEntry;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackerManifestParser;

public class WebpackerManifestParserTest {
	public static final String FILE_PATH = "src/test/resources/files/assets-manifest.json";
	public static final String BAD_ASSETS_FILE_PATH = "src/test/resources/files/assets-manifest_bad_assets.json";
	public static final String BAD_JAVASCRIPT_FILE_PATH = "src/test/resources/files/assets-manifest_file_error.json";

	/**
	 * The test file (copied from src/main/webapp/dist/assets-manifest.json on August 24, 2021), modified to remove
	 * unnecessary entries, and reduced size.
	 */
	public static final int NUM_ENTRIES = 7;

	@Test
	public void TestParseWebpackManifestFile() {
		Path path = Paths.get(FILE_PATH);
		WebpackerManifestParser parser = new WebpackerManifestParser(true);
		Map<String, WebpackEntry> entries = parser.parseWebpackManifestFile(path.toFile());
		Assert.assertEquals("Should be 7 entries in the manifest file", NUM_ENTRIES, entries.keySet()
				.size());
		entries.forEach((name, files) -> {
			Assert.assertTrue("Every entry should have a JavaScript file", files.getJavascript()
					.size() > 0);
		});
	}

	@Test(expected = WebpackParserException.class)
	public void TestParseBadWebpackManifestFile() {
		Path path = Paths.get(BAD_ASSETS_FILE_PATH);
		WebpackerManifestParser parser = new WebpackerManifestParser(true);
		Map<String, WebpackEntry> entries = parser.parseWebpackManifestFile(path.toFile());
	}

	@Test(expected = WebpackParserException.class)
	public void TestNoJavaScriptFilesInEntry() {
		Path path = Paths.get(BAD_JAVASCRIPT_FILE_PATH);
		WebpackerManifestParser parser = new WebpackerManifestParser(true);
		Map<String, WebpackEntry> entries = parser.parseWebpackManifestFile(path.toFile());
	}
}
