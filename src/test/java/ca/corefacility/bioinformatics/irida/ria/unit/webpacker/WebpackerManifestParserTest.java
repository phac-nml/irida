package ca.corefacility.bioinformatics.irida.ria.unit.webpacker;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackEntries;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackerManifestParser;

public class WebpackerManifestParserTest {
	public static final String FILE_PATH = "src/test/resources/files/assets-manifest.json";


	@Test
	public void TestParseWebpackManifestFile() {
		Path path = Paths.get(FILE_PATH);
		WebpackEntries entries = WebpackerManifestParser.parseWebpackManifestFile(path.toFile());
		String goo = "bar";
	}
}
