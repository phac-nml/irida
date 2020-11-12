package ca.corefacility.bioinformatics.irida.ria.unit.web.files;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.files.ReferenceFileController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

/**
 * Unit Tests for @{link ReferenceFileController}
 *
 */
public class ReferenceFileControllerTest {
	// Controller
	private ReferenceFileController controller;

	// Services
	private ProjectService projectService;
	private ReferenceFileService referenceFileService;
	private MessageSource messageSource;
	private HttpServletResponse response;

	public static final String FILE_NAME = "test_file.fasta";
	public static final String FILE_PATH = "src/test/resources/files/test_file.fasta";
	public static final Long PROJECT_ID = 11L;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		referenceFileService = mock(ReferenceFileService.class);
		messageSource = mock(MessageSource.class);
		response = mock(HttpServletResponse.class);

		controller = new ReferenceFileController(projectService, referenceFileService, messageSource);
	}

	@Test
	public void testCreateNewReferenceFile() throws UnsupportedReferenceFileContentError, IOException {
		Path path = Paths.get(FILE_PATH);
		byte[] origBytes = Files.readAllBytes(path);
		MultipartFile mockMultipartFile = new MockMultipartFile(FILE_NAME, FILE_NAME, "octet-stream", origBytes);

		ReferenceFile referenceFile = new TestReferenceFile(path, 2L);

		controller.addIndependentReferenceFile(mockMultipartFile, response, Locale.ENGLISH);

		verify(referenceFileService).create(any(ReferenceFile.class));
		verify(referenceFileService, times(1)).create(any(ReferenceFile.class));
	}

	class TestReferenceFile extends ReferenceFile {
		private Long id;

		public TestReferenceFile(Path path, Long id) {
			super(path);
			this.id = id;
		}

		@Override
		public Long getId() {
			return this.id;
		}
	}

}
