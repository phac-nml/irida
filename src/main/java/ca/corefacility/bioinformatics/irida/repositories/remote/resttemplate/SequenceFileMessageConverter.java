package ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
 * Message converter for converting application/fastq HTTP responses to a Java
 * Path temporary file
 * 
 *
 */
public class SequenceFileMessageConverter implements HttpMessageConverter<Path> {
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileMessageConverter.class);

	public static final MediaType MEDIA_TYPE = new MediaType("application","fastq");

	private final Path tempDirectory;

	public SequenceFileMessageConverter(Path tempDirectory) {
		this.tempDirectory = tempDirectory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		logger.trace("Testing converter for class " + clazz.getName() + " and mediatype " + mediaType);
		if (mediaType != null && MEDIA_TYPE.includes(mediaType)
				&& clazz.equals(Path.class)) {
			logger.trace("SequenceFileMessageConverter can read this message");
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
		return Lists.newArrayList(MEDIA_TYPE);
	}

	@Override
	public Path read(Class<? extends Path> clazz, HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {
		logger.debug("Converting  response to " + clazz);
		InputStream inputStream = inputMessage.getBody();
		Path tempFile = Files.createTempFile(tempDirectory, "remote-file", ".fastq");
		try (OutputStream outputStream = Files.newOutputStream(tempFile)) {
			IOUtils.copy(inputStream, outputStream);
		}

		return tempFile;
	}

	@Override
	public void write(Path t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {
		throw new HttpMessageNotWritableException("SequenceFileMessageConverter cannot be used for writing");
	}

}
