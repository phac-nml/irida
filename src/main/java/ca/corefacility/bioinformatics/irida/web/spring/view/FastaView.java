package ca.corefacility.bioinformatics.irida.web.spring.view;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractView;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;

import com.google.common.net.HttpHeaders;

/**
 * Write out FASTA formatted sequence files to the client.
 *
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
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	IridaSequenceFile sfr = (IridaSequenceFile) model.get(RESTGenericController.RESOURCE_NAME);
        Path fileContent = sfr.getFile();
        String filename = fileContent.getFileName().toString();
        logger.trace("Sending file to client [" + filename + "]");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        response.setHeader(HttpHeaders.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(Files.size(fileContent)));
        OutputStream os = response.getOutputStream();
        Files.copy(fileContent, os);
        os.flush();
        os.close();
    }
}
