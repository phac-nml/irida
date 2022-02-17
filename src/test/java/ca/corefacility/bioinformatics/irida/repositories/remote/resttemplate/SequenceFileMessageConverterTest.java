package ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.http.MockHttpInputMessage;

import static org.junit.jupiter.api.Assertions.*;

public class SequenceFileMessageConverterTest {
	private SequenceFileMessageConverter converter;
	private String fileName = "testFile";

	@BeforeEach
	public void setUp() {
		converter = new SequenceFileMessageConverter(fileName);
	}

	@Test
	public void canConvertFastqToPath() {
		Class<Path> clazz = Path.class;
		MediaType mediaType = new MediaType("application", "fastq");
		assertTrue(converter.canRead(clazz, mediaType));
	}

	@Test
	public void canConvertFastqToString() {
		Class<String> clazz = String.class;
		MediaType mediaType = new MediaType("application", "fastq");
		assertFalse(converter.canRead(clazz, mediaType));
	}

	@Test
	public void canConvertJsonToPath() {
		Class<Path> clazz = Path.class;
		MediaType mediaType = MediaType.APPLICATION_JSON;
		assertFalse(converter.canRead(clazz, mediaType));
	}

	@Test
	public void testCanWrite() {
		Class<Path> clazz = Path.class;
		MediaType mediaType = new MediaType("application", "fastq");
		assertFalse(converter.canWrite(clazz, mediaType));
	}

	@Test
	public void testWrite() throws HttpMessageNotWritableException, IOException {
		assertThrows(HttpMessageNotWritableException.class, () -> {
			converter.write(null, null, null);
		});
	}

	@Test
	public void testRead() throws HttpMessageNotReadableException, IOException {
		String message = "Some fastq file";
		byte[] messageBytes = message.getBytes();
		HttpInputMessage inputMessage = new MockHttpInputMessage(messageBytes);
		inputMessage.getHeaders()
				.add("Content-Length", Long.toString(messageBytes.length));
		Path read = converter.read(Path.class, inputMessage);
		assertTrue(Files.exists(read));

		byte[] fileBytes = Files.readAllBytes(read);
		assertEquals(message, new String(fileBytes));

		Files.delete(read);
	}

	@Test
	public void testReadPartialFile() throws HttpMessageNotReadableException, IOException {
		String message = "Some fastq file";
		byte[] messageBytes = message.getBytes();
		HttpInputMessage inputMessage = new MockHttpInputMessage(messageBytes);
		inputMessage.getHeaders()
				.add("Content-Length", Long.toString(messageBytes.length + 1));
		assertThrows(IOException.class, () -> {
			converter.read(Path.class, inputMessage);
		});
	}

	@Test
	public void testGetMediaTypes() {
		List<MediaType> supportedMediaTypes = converter.getSupportedMediaTypes();
		assertEquals(3, supportedMediaTypes.size());

		assertTrue(supportedMediaTypes.stream()
				.anyMatch(m -> m.includes(new MediaType("application", "fastq"))));
		assertTrue(supportedMediaTypes.stream()
				.anyMatch(m -> m.includes(new MediaType("application", "fasta"))));
		assertTrue(supportedMediaTypes.stream()
				.anyMatch(m -> m.includes(new MediaType("application", "fast5"))));
	}
}
