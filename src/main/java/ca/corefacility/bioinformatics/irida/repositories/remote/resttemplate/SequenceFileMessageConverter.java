package ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.google.common.collect.Lists;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class FastqMessageConverter implements HttpMessageConverter<Path> {
	private static final Logger logger = LoggerFactory.getLogger(FastqMessageConverter.class);
	private String type = "application";
	private String subtype = "fastq";

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		logger.debug("Testing converter for class " + clazz + " and mediatype " + mediaType);
		if (mediaType != null && mediaType.getType().equals(type) && mediaType.getSubtype().equals(subtype)
				&& clazz.equals(Path.class)) {
			logger.debug("Conversion accepted");
			return true;
		}

		return false;

	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return Lists.newArrayList(new MediaType(type, subtype));
	}

	@Override
	public Path read(Class<? extends Path> clazz, HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {
		logger.debug("Converting  " + inputMessage + " to " + clazz);
		InputStream inputStream = inputMessage.getBody();
		Path createTempFile = Files.createTempFile("outfile", ".fastq");
		try (FileOutputStream outputStream = new FileOutputStream(createTempFile.toFile())) {
			IOUtils.copy(inputStream, outputStream);
		}

		return createTempFile;
	}

	@Override
	public void write(Path t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {
		throw new UnsupportedOperationException("FastqMessageConverter cannot be used for writing");

	}

}
