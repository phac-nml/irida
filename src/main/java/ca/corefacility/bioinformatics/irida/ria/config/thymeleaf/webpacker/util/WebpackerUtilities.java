package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.util.ResourceUtils;

import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;

public class WebpackerUtilities {
	private static Map<String, Map<String, List<String>>> entryMap;
	private static String manifestChecksum = "";
	private static Boolean autoUpdatable = true;

	public static void setAutoUpdatable(Boolean updatable) {
		WebpackerUtilities.autoUpdatable = updatable;
	}

	public static Map<String, Map<String, List<String>>> getEntryMap() {
		try {
			if(entryMap == null || autoUpdatable){
				File manifestFile = ResourceUtils.getFile("file:src/main/webapp/dist/manifest.json");
				try (InputStream is = Files.newInputStream(manifestFile.toPath())) {
					String checksum = org.apache.commons.codec.digest.DigestUtils.sha256Hex(is);
					if (!checksum.equals(manifestChecksum)) {
						entryMap = createEntriesMapFromManifest(manifestFile);
						manifestChecksum = checksum;
					}
				} catch (IOException e) {
					throw new FileProcessorException("could not calculate checksum", e);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return entryMap;
	}

	private static Map<String, Map<String, List<String>>> createEntriesMapFromManifest(File file) {
		Map<String, Map<String, List<String>>> entries = new HashMap<>();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> manifest = objectMapper
					.readValue(file, new TypeReference<Map<String, Object>>() {
					});

			entries = (Map<String, Map<String, List<String>>>) manifest.get("entrypoints");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return entries;
	}

	/**
	 * Check if a translation file exists for a given JS bundle
	 *
	 * @param bundleName Name of the JS bundle
	 * @return true if the {@param bundleName} has a translation file
	 */
	public static boolean doesTranslationsFileExist(String bundleName) {
		try {
			return ResourceUtils.getFile("file:src/main/webapp/dist/i18n/" + bundleName + ".html").exists();
		} catch (FileNotFoundException e) {
			return false;
		}
	}
}
