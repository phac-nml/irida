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
 * Message markdownConverter for converting application/fastq HTTP responses to a Java
 * Path temporary file
 */
public class SequenceFileMessageConverter implements HttpMessageConverter<Path> {
	public static final List<MediaType> SUPPORTED_TYPES = Lists.newArrayList(new MediaType("application", "fastq"),
			new MediaType("application", "fasta"));
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileMessageConverter.class);
	private final String fileName;

	public SequenceFileMessageConverter(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		logger.trace("Testing markdownConverter for class " + clazz.getName() + " and mediatype " + mediaType);
		if (mediaType != null && clazz.equals(Path.class) && SUPPORTED_TYPES.stream()
				.anyMatch(t -> t.includes(mediaType))) {
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
		return SUPPORTED_TYPES;
	}

	@Override
	public Path read(Class<? extends Path> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		logger.debug("Converting  response to " + clazz);
		InputStream inputStream = inputMessage.getBody();
		Path fileDirectory = Files.createTempDirectory(null);
		Path tempFile = fileDirectory.resolve(fileName);
		tempFile = Files.createFile(tempFile);
		try (OutputStream outputStream = Files.newOutputStream(tempFile)) {
			long fileSize = IOUtils.copyLarge(inputStream, outputStream);

			long expectedSize = inputMessage.getHeaders()
					.getContentLength();
			if (fileSize != expectedSize) {
				throw new IOException(
						"Error when writing remote file [" + fileName + "], to path [" + tempFile + "], expectedSize ["
								+ expectedSize + "] != actual size [" + fileSize + "]");
			}
		}

		return tempFile;
	}

	@Override
	public void write(Path t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {
		throw new HttpMessageNotWritableException("SequenceFileMessageConverter cannot be used for writing");
	}

}
