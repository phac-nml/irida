package ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.http.MockHttpInputMessage;

public class SequenceFileMessageConverterTest {
	private SequenceFileMessageConverter converter;
	private String fileName = "testFile";

	@Before
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

	@Test(expected = HttpMessageNotWritableException.class)
	public void testWrite() throws HttpMessageNotWritableException, IOException {
		converter.write(null, null, null);
	}

	@Test
	public void testRead() throws HttpMessageNotReadableException, IOException {
		String message = "Some fastq file";
		HttpInputMessage inputMessage = new MockHttpInputMessage(message.getBytes());
		Path read = converter.read(Path.class, inputMessage);
		assertTrue(Files.exists(read));

		byte[] fileBytes = Files.readAllBytes(read);
		assertEquals(message, new String(fileBytes));

		Files.delete(read);
	}

	@Test
	public void testGetMediaTypes() {
		List<MediaType> supportedMediaTypes = converter.getSupportedMediaTypes();
		assertEquals(1, supportedMediaTypes.size());
		MediaType next = supportedMediaTypes.iterator().next();
		assertEquals("application", next.getType());
		assertEquals("fastq", next.getSubtype());
	}
}
