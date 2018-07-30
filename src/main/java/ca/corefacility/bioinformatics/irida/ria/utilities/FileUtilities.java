package ca.corefacility.bioinformatics.irida.ria.utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
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
	private static final Pattern regexExt = Pattern.compile("^.*\\.(\\w+)$");

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
		fileName = formatName(fileName);

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
	 * Utility method for download a zip file containing all output files from
	 * an analysis.
	 *  @param response
	 *            {@link HttpServletResponse}
	 * @param fileName
	 *            Name fo the file to create
	 * @param files
 *            Set of {@link AnalysisOutputFile}
	 */
	public static void createBatchAnalysisOutputFileZippedResponse(HttpServletResponse response, String fileName,
			Map<ProjectSampleAnalysisOutputInfo, AnalysisOutputFile> files) {
		/*
		 * Replacing spaces and commas as they cause issues with
		 * Content-disposition response header.
		 */
		fileName = formatName(fileName);
		logger.debug("Creating zipped file response. [" + fileName + "] with " + files.size() + " analysis output files.");

		// set the response headers before we do *ANYTHING* so that the filename
		// actually appears in the download dialog for zip file
		response.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName + EXTENSION_ZIP);
		response.setContentType(CONTENT_TYPE_APPLICATION_ZIP);

		try (ServletOutputStream responseStream = response.getOutputStream();
				ZipOutputStream outputStream = new ZipOutputStream(responseStream)) {
			for (Map.Entry<ProjectSampleAnalysisOutputInfo, AnalysisOutputFile> entry : files.entrySet()) {
				final AnalysisOutputFile file = entry.getValue();
				final ProjectSampleAnalysisOutputInfo outputInfo = entry.getKey();
				if (!Files.exists(file.getFile())) {
					response.setStatus(404);
					throw new FileNotFoundException("File '" + file.getFile().toFile().getAbsolutePath() + "' does not exist!");
				}
				// 1) Build a folder/file name
				// building similar filename for each analysis output file as:
				// resources/js/pages/projects/project-analysis-outputs.js#downloadSelected
				String outputFilename = file.getFile()
						.getFileName()
						.toString();
				// trying to pack as much useful info into the filename as possible!
				outputFilename = outputInfo.getSampleName() + "-sampleId-" + outputInfo.getSampleId() + "-analysisSubmissionId-" + outputInfo.getAnalysisSubmissionId() + "-" + outputFilename;
				// 2) Tell the zip stream that we are starting a new entry in
				// the archive.
				outputStream.putNextEntry(new ZipEntry(fileName + "/" + outputFilename));

				// 3) COPY all of thy bytes from the file to the output stream.
				Files.copy(file.getFile(), outputStream);

				// 4) Close the current entry in the archive in preparation for
				// the next entry.
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
	 * @param file
	 *            Set of {@link AnalysisOutputFile}
	 * @param fileName Filename
	 */
	public static void createSingleFileResponse(HttpServletResponse response, AnalysisOutputFile file, String fileName) {
		fileName = formatName(fileName);

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
	 * Utility method for download single file from an analysis.
	 *
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param file
	 *            Set of {@link AnalysisOutputFile}
	 */
	public static void createSingleFileResponse(HttpServletResponse response, AnalysisOutputFile file) {
		String fileName = file.getLabel();
		FileUtilities.createSingleFileResponse(response, file, fileName);
	}



	/**
	 * Method to remove unwanted characters from the filename.
	 *
	 * @param name
	 *            Name of the file
	 * @return The name with unwanted characters removed.
	 */
	private static String formatName(String name) {
		return name.replaceAll("\\s", "_").replaceAll(",", "");
	}

	/**
	 * Get file extension from filename.
	 * <p>
	 * Uses simple regex to parse file extension {@code ^.*\.(\w+)$}.
	 *
	 * @param filename Filename
	 * @return File extension if found; otherwise empty string
	 */
	public static String getFileExt(String filename) {
		Matcher matcher = regexExt.matcher(filename);
		String ext = "";
		if (matcher.matches()) {
			ext = matcher.group(1);
		}
		return ext.toLowerCase();
	}

	/**
	 * Read bytes of length {@code chunk} of a file starting at byte {@code seek}.
	 *
	 * @param raf   File reader
	 * @param seek  FilePointer position to start reading at
	 * @param chunk Number of bytes to read from file
	 * @return Chunk of file as String
	 * @throws IOException if error enountered while reading file
	 */
	public static String readChunk(RandomAccessFile raf, Long seek, Long chunk) throws IOException {
		raf.seek(seek);
		byte[] bytes = new byte[Math.toIntExact(chunk)];
		final int bytesRead = raf.read(bytes);
		if (bytesRead == -1) {
			return "";
		}
		return new String(bytes, 0, bytesRead, Charset.defaultCharset());
	}

	/**
	 * Read a specified number of lines from a file.
	 *
	 * @param reader File reader
	 * @param limit  Limit to the number of lines to read
	 * @param start  Optional line number to start reading at
	 * @param end    Optional line number to read up to
	 * @return Lines read from file
	 */
	public static List<String> readLinesLimit(BufferedReader reader, Long limit, Long start, Long end) {
		Long linesLimit = (limit != null) ? limit : 100L;
		start = (start == null) ? 0 : start;
		if (end != null && end > start) {
			linesLimit = end - start + 1;
		}
		return reader.lines()
				.skip(start == 0 ? 1L : start)
				.limit(linesLimit)
				.collect(Collectors.toList());
	}

	/**
	 * Read lines from file using a {@link RandomAccessFile}.
	 * <p>
	 * Use this method if preserving the {@link RandomAccessFile#getFilePointer()} for continuing reading is important.
	 * For most use cases, {@link FileUtilities#readLinesLimit(BufferedReader, Long, Long, Long)} will perform better
	 * due to bufffered reading.
	 *
	 * @param randomAccessFile File reader
	 * @param limit            Limit to the number of lines to read
	 * @return Lines read from file
	 * @throws IOException if error enountered while reading file
	 */
	public static List<String> readLinesFromFilePointer(RandomAccessFile randomAccessFile, Long limit)
			throws IOException {
		ArrayList<String> lines = new ArrayList<>();
		String line;
		while (lines.size() < limit && (line = randomAccessFile.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}
}
