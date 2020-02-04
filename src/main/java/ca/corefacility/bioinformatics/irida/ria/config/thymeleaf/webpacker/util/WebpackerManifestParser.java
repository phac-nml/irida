package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;

/**
 * Responsible for parsing the webpack manifest file and passing along the chunks for
 * js, css, and html resources.  During development the manifest file is checked to ensure
 * it has not been updated during a build, this will not happen in production since the
 * manifest file will never change.
 */
public class WebpackerManifestParser {
	private static final Logger logger = LoggerFactory.getLogger(WebpackerManifestParser.class);
	private static Map<String, Map<String, List<String>>> entryMap;
	private static String manifestChecksum = "";
	private static Boolean autoUpdatable = true;

	/**
	 * Allows the UI configuration to determine during runtime if we are in a production or development
	 * environment.
	 *
	 * @param updatable - should the manifest file be checked to see if it has been updated.
	 */
	public static void setAutoUpdatable(Boolean updatable) {
		WebpackerManifestParser.autoUpdatable = updatable;
	}

	/**
	 * Get a list of webpack chunks for a specific file type given an entry.
	 *
	 * @param entry - the current webpack entry to get chunks for.
	 * @param type  - the type of resource files to get.
	 * @return List of chunks
	 */
	public static List<String> getChunksForEntryType(
			ServletContext context, String entry, WebpackerTagType type) {
		Map<String, Map<String, List<String>>> entries = getEntryMap(context);

		/*
		 * Ensure the entry actually exists in webpack.
		 */
		if (!entries.containsKey(entry)) {
			logger.debug(String.format("Webpack manifest does not have an entry for %s", entry));
			return null;
		}

		/*
		 * Entry that the entry has resources for the type wanted.
		 */
		if (!entries.get(entry).containsKey(type.toString()) && !entry.equals("vendor")) {
			logger.debug(
					String.format("For the entry %s, Webpack manifest does a %s file type", type.toString(), entry));
			return null;
		}

		return entries.get(entry).get(type.toString());
	}

	/**
	 * Get the current manifest content.  If this is running in development, check to ensure that the
	 * manifest file has not changed, if it has, parse the new manifest file.
	 *
	 * @return {@link Map} of all entries and their corresponding chunks.
	 */
	private static Map<String, Map<String, List<String>>> getEntryMap(ServletContext context) {
		try {
			if (WebpackerManifestParser.entryMap == null || autoUpdatable) {
				String path = context.getResource("/dist/manifest.json")
						.getPath();
				File manifestFile = ResourceUtils.getFile(path);
				try (InputStream is = Files.newInputStream(manifestFile.toPath())) {
					String checksum = org.apache.commons.codec.digest.DigestUtils.sha256Hex(is);
					if (!checksum.equals(WebpackerManifestParser.manifestChecksum)) {
						WebpackerManifestParser.entryMap = parseWebpackManifestFile(manifestFile);
						WebpackerManifestParser.manifestChecksum = checksum;
					}
				} catch (IOException e) {
					throw new FileProcessorException("could not calculate checksum", e);
				}
			}
		} catch (FileNotFoundException | MalformedURLException e) {
			logger.error("Cannot find webpack manifest file.");
		}
		return WebpackerManifestParser.entryMap;
	}

	/**
	 * Parse the webpack manifest file
	 *
	 * @param file - the webpack manifest file.
	 * @return {@link Map} of all entries and their corresponding chunks.
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Map<String, List<String>>> parseWebpackManifestFile(File file) {
		Map<String, Map<String, List<String>>> newEntries = new HashMap<>();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> manifest = objectMapper.readValue(file, new TypeReference<Map<String, Object>>() {
			});

			newEntries = (Map<String, Map<String, List<String>>>) manifest.get("entrypoints");
		} catch (IOException e) {
			logger.error("Error reading webpack manifest file.");
		}
		return newEntries;
	}
}
