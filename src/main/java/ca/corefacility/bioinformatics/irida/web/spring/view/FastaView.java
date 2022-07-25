package ca.corefacility.bioinformatics.irida.web.spring.view;

import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;

import com.google.common.net.HttpHeaders;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;

/**
 * Write out FASTA formatted sequence files to the client.
 */
public class FastaView extends AbstractView {

	public static final String DEFAULT_CONTENT_TYPE = "application/fasta";
	private static final Logger logger = LoggerFactory.getLogger(FastaView.class);

	/**
	 * Default constructor
	 */
	public FastaView() {

		setContentType(DEFAULT_CONTENT_TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		IridaSequenceFile sfr = (IridaSequenceFile) ((ResponseResource<?>) model
				.get(RESTGenericController.RESOURCE_NAME)).getResource();
		Path fileContent = sfr.getFile();
		String filename = fileContent.getFileName().toString();
		logger.trace("Sending file to client [" + filename + "]");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
		response.setHeader(HttpHeaders.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
		response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(IridaFiles.getFileSizeBytes(fileContent)));

		try (InputStream is = IridaFiles.getFileInputStream(fileContent);
				OutputStream os = response.getOutputStream();) {
			IOUtils.copy(is, os);
			os.flush();
		} catch (IOException e) {
			throw new IOException("Unable to read inputstream ", e);
		}
	}
}
