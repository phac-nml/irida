package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectReferenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import com.github.jsonldjava.shaded.com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UIProjectReferenceFileServiceTest {

	// Services
	private ProjectService projectService;
	private ReferenceFileService referenceFileService;
	private MessageSource messageSource;
	private IridaFileStorageUtility iridaFileStorageUtility;

	public static final Long FILE_ID = 1L;
	public static final String FILE_NAME = "test_file.fasta";
	public static final String FILE_PATH = "src/test/resources/files/test_file.fasta";
	public static final Long PROJECT_ID = 11L;
	private static final Logger logger = LoggerFactory.getLogger(UIProjectReferenceFileServiceTest.class);

	private UIProjectReferenceFileService uiProjectReferenceFileService;

	@BeforeEach
	public void setUp() {
		projectService = mock(ProjectService.class);
		referenceFileService = mock(ReferenceFileService.class);
		messageSource = mock(MessageSource.class);
		iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl(true);
		IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);

		// Set up the reference file
		Path path = Paths.get(FILE_PATH);
		ReferenceFile file = new ReferenceFile(path);
		when(referenceFileService.read(FILE_ID)).thenReturn(file);

		uiProjectReferenceFileService = new UIProjectReferenceFileService(projectService, referenceFileService,
				messageSource, iridaFileStorageUtility);
		IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);
	}

	@Test
	public void testCreateNewReferenceFile() throws UnsupportedReferenceFileContentError, IOException {
		Path path = Paths.get(FILE_PATH);
		byte[] origBytes = Files.readAllBytes(path);
		List<MultipartFile> mockMultipartFiles = ImmutableList.of(
				new MockMultipartFile(FILE_NAME, FILE_NAME, "octet-stream", origBytes));
		Project project = new Project("foo");
		ReferenceFile referenceFile = new TestReferenceFile(path, 2L);

		when(projectService.read(PROJECT_ID)).thenReturn(project);
		when(projectService.addReferenceFileToProject(eq(project), any(ReferenceFile.class))).thenReturn(
				new ProjectReferenceFileJoin(project, referenceFile));

		uiProjectReferenceFileService.addReferenceFileToProject(PROJECT_ID, mockMultipartFiles, Locale.ENGLISH);

		verify(projectService).read(PROJECT_ID);
		verify(projectService).addReferenceFileToProject(eq(project), any(ReferenceFile.class));
	}

	@Test
	public void testDeleteReferenceFile() {
		logger.debug("Testing delete reference file");
		Project project = TestDataFactory.constructProject();
		ReferenceFile file = TestDataFactory.constructReferenceFile();

		when(projectService.read(project.getId())).thenReturn(project);
		when(referenceFileService.read(file.getId())).thenReturn(file);

		uiProjectReferenceFileService.deleteReferenceFile(file.getId(), project.getId(), Locale.US);

		verify(projectService, times(1)).removeReferenceFileFromProject(project, file);

	}

	@Test
	public void testDownloadReferenceFile() throws IOException {
		logger.debug("Testing download reference file");
		MockHttpServletResponse response = new MockHttpServletResponse();

		uiProjectReferenceFileService.downloadReferenceFile(FILE_ID, response);
		assertTrue(response.containsHeader("Content-Disposition"),
				"Response should contain a \"Content-Disposition\" header.");
		assertEquals("attachment; filename=\"test_file.fasta\"", response.getHeader("Content-Disposition"),
				"Content-Disposition should include the file name");

		Path path = Paths.get(FILE_PATH);
		byte[] origBytes = Files.readAllBytes(path);
		byte[] responseBytes = response.getContentAsByteArray();
		assertArrayEquals(origBytes, responseBytes, "Response contents the correct file content");
	}

	@Test
	public void testGetReferenceFileForProject() {
		logger.debug("Testing getting reference file(s) for project");
		Project project = TestDataFactory.constructProject();
		ReferenceFile file = TestDataFactory.constructReferenceFile();

		when(projectService.read(project.getId())).thenReturn(project);
		when(referenceFileService.read(file.getId())).thenReturn(file);

		uiProjectReferenceFileService.getReferenceFilesForProject(project.getId());

		verify(projectService, times(1)).read(project.getId());
		verify(referenceFileService, times(1)).getReferenceFilesForProject(project);
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
