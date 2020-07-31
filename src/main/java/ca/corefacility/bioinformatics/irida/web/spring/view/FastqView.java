package ca.corefacility.bioinformatics.irida.web.spring.view;

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

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;

import com.google.common.net.HttpHeaders;

/**
 * Write out FASTQ formatted sequence files to the client.
 *
 */
public class FastqView extends AbstractView {
    public static final String DEFAULT_CONTENT_TYPE = "application/fastq";
    private static final Logger logger = LoggerFactory.getLogger(FastqView.class);

    /**
     * Default constructor
     */
    public FastqView() {
        setContentType(DEFAULT_CONTENT_TYPE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	SequenceFile sfr = (SequenceFile) model.get(RESTGenericController.RESOURCE_NAME);
        Path fileContent = sfr.getFile();
        String filename = fileContent.getFileName().toString();
        logger.trace("Sending file to client [" + filename + "]");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        response.setHeader(HttpHeaders.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(sfr.getFileSizeBytes()));
        OutputStream os = response.getOutputStream();
        InputStream is = sfr.getFileInputStream();
        IOUtils.copy(is, os);
        is.close();
        os.flush();
        os.close();
    }
}
