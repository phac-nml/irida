package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;

/**
 * Responsible for parsing the webpack manifest file and passing along the chunks for js, css, and html resources.
 * During development the manifest file is checked to ensure it has not been updated during a build, this will not
 * happen in production since the manifest file will never change.
 */
public class WebpackerManifestParser {
	private final Logger logger = LoggerFactory.getLogger(WebpackerManifestParser.class);
	private Map<String, WebpackEntry> entries;
	private String manifestChecksum = "";

	private final String ASSET_MANIFEST_FILE_PATH = "/dist/assets-manifest.json";
	private final boolean updatable;

	public WebpackerManifestParser(boolean autoUpdatable) {
		this.updatable = autoUpdatable;
	}

	/**
	 * Get a list of webpack chunks for a specific file type given an entry.
	 *
	 * @param context - the {@link ServletContext}
	 * @param entry   - the current webpack entry to get chunks for.
	 * @param type    - the type of resource files to get.
	 * @return List of chunks
	 */
	public List<String> getChunksForEntryType(ServletContext context, String entry, WebpackerTagType type) {
		Map<String, WebpackEntry> entries = getEntries(context);

		/*
		 * Ensure the entry actually exists in webpack.
		 */
		if (!entries.containsKey(entry)) {
			logger.debug(String.format("Webpack manifest does not have an entry for %s", entry));
			return null;
		}

		/*
		 * Ensure that the entry exists and has resources for the type wanted.
		 */
		if (!entries.containsKey(entry) && !entry.equals("vendor")) {
			logger.debug(String.format("For the entry %s, Webpack manifest does a %s file type", type, entry));
			return null;
		}

		switch (type) {
		case JS:
			return entries.get(entry).getJavascript();
		case CSS:
			return entries.get(entry).getCss();
		case HTML:
			return entries.get(entry).getHtml();
		default:
			return ImmutableList.of();
		}
	}

	/**
	 * Get the current manifest content. If this is running in development, check to ensure that the manifest file has
	 * not changed, if it has, parse the new manifest file.
	 *
	 * @param context
	 * @return {@link Map} of all entries and their corresponding chunks.
	 */
	private Map<String, WebpackEntry> getEntries(ServletContext context) {
		try {
			if (entries == null || updatable) {
				String path = context.getResource(ASSET_MANIFEST_FILE_PATH).getPath();
				File manifestFile = ResourceUtils.getFile(path);
				try (InputStream is = Files.newInputStream(manifestFile.toPath())) {
					String checksum = org.apache.commons.codec.digest.DigestUtils.sha256Hex(is);
					if (!checksum.equals(manifestChecksum)) {
						entries = parseWebpackManifestFile(manifestFile);
						manifestChecksum = checksum;
					}
				} catch (IOException e) {
					throw new FileProcessorException("could not calculate checksum", e);
				}
			}
		} catch (FileNotFoundException | MalformedURLException e) {
			logger.error("Cannot find webpack manifest file.");
		}
		return entries;
	}

	/**
	 * Parse the webpack manifest file
	 *
	 * @param file - the webpack manifest file.
	 * @return {@link Map} of all entries and their corresponding chunks.
	 */
	public Map<String, WebpackEntry> parseWebpackManifestFile(File file) {
		try {
			String contents = new String(Files.readAllBytes(Paths.get(file.toURI())));
			ObjectMapper objectMapper = new ObjectMapper();
			SimpleModule module = new SimpleModule();
			module.addDeserializer(WebpackAssetsManifest.class, new WebpackAssetsManifestDeserializer());
			objectMapper.registerModule(module);

			WebpackAssetsManifest assetsManifest = objectMapper.readValue(contents, WebpackAssetsManifest.class);
			return assetsManifest.getEntries();
		} catch (IOException e) {
			logger.error("Error reading webpack manifest file.");
		}
		return null;
	}
}
