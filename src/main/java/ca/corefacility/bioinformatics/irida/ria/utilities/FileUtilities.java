package ca.corefacility.bioinformatics.irida.ria.utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Download a zip archive of all output files within an
 * {@link AnalysisSubmission}
 *
 */
public class FileUtilities {
	private static final Logger logger = LoggerFactory.getLogger(FileUtilities.class);
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String ATTACHMENT_FILENAME = "attachment;filename=";
	public static final String CONTENT_TYPE_APPLICATION_ZIP = "application/zip";
	public static final String CONTENT_TYPE_TEXT = "text/plain";
	public static final String EXTENSION_ZIP = ".zip";

	/**
	 * Utility method for download a zip file containing all output files from
	 * an analysis.
	 *
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param fileName
	 *            Name fo the file to create
	 * @param files
	 *            Set of {@link AnalysisOutputFile}
	 */
	public static void createAnalysisOutputFileZippedResponse(HttpServletResponse response, String fileName,
			Set<AnalysisOutputFile> files) {
		/*
		 * Replacing spaces and commas as they cause issues with
		 * Content-disposition response header.
		 */
		fileName = fileName.replaceAll(" ", "_");
		fileName = fileName.replaceAll(",", "");

		logger.debug("Creating zipped file response. [" + fileName + "]");

		// set the response headers before we do *ANYTHING* so that the filename
		// actually appears in the download dialog
		response.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName + EXTENSION_ZIP);
		// for zip file
		response.setContentType(CONTENT_TYPE_APPLICATION_ZIP);

		try (ServletOutputStream responseStream = response.getOutputStream();
				ZipOutputStream outputStream = new ZipOutputStream(responseStream)) {

			for (AnalysisOutputFile file : files) {
				if (!Files.exists(file.getFile())) {
					response.setStatus(404);
					throw new FileNotFoundException();
				}
				// 1) Build a folder/file name
				fileName = formatName(fileName);
				StringBuilder zipEntryName = new StringBuilder(fileName);
				zipEntryName.append("/").append(file.getLabel());

				// 2) Tell the zip stream that we are starting a new entry in
				// the archive.
				outputStream.putNextEntry(new ZipEntry(zipEntryName.toString()));

				// 3) COPY all of thy bytes from the file to the output stream.
				Files.copy(file.getFile(), outputStream);

				// 4) Close the current entry in the archive in preparation for
				// the next entry.
				outputStream.closeEntry();

				ObjectMapper objectMapper = new ObjectMapper();
				byte[] bytes = objectMapper.writeValueAsBytes(file);
				outputStream.putNextEntry(new ZipEntry(zipEntryName.toString() + "-prov.json"));
				outputStream.write(bytes);
				outputStream.closeEntry();
			}

			// Tell the output stream that you are finished downloading.
			outputStream.finish();
			outputStream.close();
		} catch (IOException e) {
			// this generally means that the user has cancelled the download
			// from their web browser; we can safely ignore this
			logger.debug("This *probably* means that the user cancelled the download, "
					+ "but it might be something else, see the stack trace below for more information.", e);
		} catch (Exception e) {
			logger.error("Download failed...", e);
		}
	}

	/**
	 * Utility method for download single file from an analysis.
	 *
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param files
	 *            Set of {@link AnalysisOutputFile}
	 */
	public static void createSingleFileResponse(HttpServletResponse response, AnalysisOutputFile file) {
		String fileName = file.getLabel();

		fileName = fileName.replaceAll(" ", "_");
		fileName = fileName.replaceAll(",", "");

		// set the response headers before we do *ANYTHING* so that the filename
		// actually appears in the download dialog
		response.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName);
		response.setContentType(CONTENT_TYPE_TEXT);

		try (ServletOutputStream outputStream = response.getOutputStream()) {
			Files.copy(file.getFile(), response.getOutputStream());
		} catch (IOException e) {
			// this generally means that the user has cancelled the download
			// from their web browser; we can safely ignore this
			logger.debug("This *probably* means that the user cancelled the download, "
					+ "but it might be something else, see the stack trace below for more information.", e);
		} catch (Exception e) {
			logger.error("Download failed...", e);
		}
	}

	/**
	 * Method to remove unwanted characters from the filename.
	 *
	 * @param name
	 *            Name of the file
	 * @return The name with unwanted characters removed.
	 */
	private static String formatName(String name) {
		return name.replace(" ", "_");
	}
}
