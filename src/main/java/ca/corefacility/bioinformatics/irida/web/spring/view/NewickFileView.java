package ca.corefacility.bioinformatics.irida.web.spring.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractView;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;

import com.google.common.net.HttpHeaders;

/**
 * Write out Newick formatted tree files to the client.
 */
public class NewickFileView extends AbstractView {
	public static final String DEFAULT_CONTENT_TYPE = "application/newick";
	private static final Logger logger = LoggerFactory.getLogger(NewickFileView.class);

	/**
	 * Default constructor
	 */
	public NewickFileView() {
		setContentType(DEFAULT_CONTENT_TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		AnalysisOutputFile sfr = (AnalysisOutputFile) ((ResponseResource) model.get(
				RESTGenericController.RESOURCE_NAME)).getResource();
		Path fileContent = sfr.getFile();
		String filename = fileContent.getFileName()
				.toString();
		logger.trace("Sending file to client [" + filename + "]");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
		response.setHeader(HttpHeaders.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
		response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(sfr.getFileSizeBytes()));

		try(InputStream is = sfr.getFileInputStream()) {
			OutputStream os = response.getOutputStream();
			IOUtils.copy(is, os);
			os.flush();
			os.close();
		}catch (IOException e) {
			throw new IOException("Unable to read inputstream ", e);
		}
	}
}
