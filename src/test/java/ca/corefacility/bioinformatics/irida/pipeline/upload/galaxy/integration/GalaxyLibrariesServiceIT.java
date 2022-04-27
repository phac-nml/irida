package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.annotation.GalaxyIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadErrorException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadTimeoutException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.DeleteGalaxyObjectFailedException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.InputFileType;
import ca.corefacility.bioinformatics.irida.pipeline.upload.DataStorage;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryDataset;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for dealing with Galaxy Libraries.
 */
@GalaxyIntegrationTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class GalaxyLibrariesServiceIT {

	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private GalaxyLibrariesService galaxyLibrariesService;

	private Path dataFile;
	private Path dataFile2;
	private Path dataFileCompressed;
	private Path dataFileFail;

	@Autowired
	private GalaxyInstance galaxyInstanceAdmin;
	private LibrariesClient librariesClient;

	private static final InputFileType FILE_TYPE = InputFileType.FASTQ_SANGER;

	/**
	 * Sets up variables for tests
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@BeforeEach
	public void setup() throws URISyntaxException, IOException {
		galaxyInstanceAdmin = localGalaxy.getGalaxyInstanceAdmin();
		librariesClient = galaxyInstanceAdmin.getLibrariesClient();

		dataFile = Paths.get(GalaxyLibrariesServiceIT.class.getResource("testData1.fastq").toURI());

		dataFile2 = Paths.get(GalaxyLibrariesServiceIT.class.getResource("testData2.fastq").toURI());

		dataFileCompressed = Paths.get(GalaxyLibrariesServiceIT.class.getResource("testData5.fastq.gz").toURI());

		dataFileFail = Paths.get(GalaxyLibrariesServiceIT.class.getResource("fail.fastq.gz").toURI());
	}

	/**
	 * Builds a library with the given name.
	 * 
	 * @param name The name of the new library.
	 * @return A library with the given name.
	 * @throws CreateLibraryException
	 */
	private Library buildEmptyLibrary(String name) throws CreateLibraryException {
		return galaxyLibrariesService.buildEmptyLibrary(new GalaxyProjectName(name));
	}

	/**
	 * Tests successful upload of a file to a Galaxy Library.
	 * 
	 * @throws UploadException
	 * @throws GalaxyDatasetException
	 */
	@Test
	public void testFileToLibrarySuccess() throws UploadException, GalaxyDatasetException {
		String filename = dataFile.toFile().getName();
		Library library = buildEmptyLibrary("testFileToLibrarySuccess");
		String datasetId = galaxyLibrariesService.fileToLibrary(dataFile, FILE_TYPE, library, DataStorage.LOCAL);
		assertNotNull(datasetId);
		LibraryDataset actualDataset = localGalaxy.getGalaxyInstanceAdmin()
				.getLibrariesClient()
				.showDataset(library.getId(), datasetId);
		assertNotNull(actualDataset);
		assertEquals(filename, actualDataset.getName());
		assertEquals(Long.toString(dataFile.toFile().length()), actualDataset.getFileSize());
	}

	/**
	 * Tests failure to upload of a file to a Galaxy Library.
	 * 
	 * @throws UploadException
	 * @throws GalaxyDatasetException
	 */
	@Test
	public void testFileToLibraryFailure() throws UploadException, GalaxyDatasetException {
		Library library = buildEmptyLibrary("testFileToLibraryFailure");
		library.setId("invalid");
		assertThrows(UploadException.class, () -> {
			galaxyLibrariesService.fileToLibrary(dataFile, FILE_TYPE, library, DataStorage.LOCAL);
		});
	}

	/**
	 * Tests successful upload of a list of files to a Galaxy Library.
	 * 
	 * @throws UploadException
	 * @throws GalaxyDatasetException
	 */
	@Test
	public void testFilesToLibraryWaitSuccess() throws UploadException, GalaxyDatasetException {
		Library library = buildEmptyLibrary("testFilesToLibraryWaitSuccess");
		Map<Path, String> datasetsMap = galaxyLibrariesService.filesToLibraryWait(
				Sets.newHashSet(dataFile, dataFile2, dataFileCompressed), library, DataStorage.LOCAL);
		assertNotNull(datasetsMap);
		assertEquals(3, datasetsMap.size());
		String datasetId1 = datasetsMap.get(dataFile);
		String datasetId2 = datasetsMap.get(dataFile2);
		String datasetIdCompressed = datasetsMap.get(dataFileCompressed);

		LibraryDataset actualDataset1 = localGalaxy.getGalaxyInstanceAdmin()
				.getLibrariesClient()
				.showDataset(library.getId(), datasetId1);
		assertNotNull(actualDataset1);
		assertEquals(actualDataset1.getDataTypeExt(), InputFileType.FASTQ_SANGER.toString(),
				"Invalid data type extension");

		LibraryDataset actualDataset2 = localGalaxy.getGalaxyInstanceAdmin()
				.getLibrariesClient()
				.showDataset(library.getId(), datasetId2);
		assertNotNull(actualDataset2);
		assertEquals(actualDataset2.getDataTypeExt(), InputFileType.FASTQ_SANGER.toString(),
				"Invalid data type extension");

		LibraryDataset actualDatasetCompressed = localGalaxy.getGalaxyInstanceAdmin()
				.getLibrariesClient()
				.showDataset(library.getId(), datasetIdCompressed);
		assertNotNull(actualDatasetCompressed);
		assertEquals(actualDatasetCompressed.getDataTypeExt(), InputFileType.FASTQ_SANGER_GZ.toString(),
				"Invalid data type extension");
	}

	/**
	 * Tests failure to upload a list of files to a Galaxy history through a Library.
	 * 
	 * @throws UploadException
	 * @throws GalaxyDatasetException
	 */
	@Test
	public void testFilesToLibraryWaitFail() throws UploadException, GalaxyDatasetException {
		Library library = buildEmptyLibrary("testFilesToLibraryToHistoryFail");
		library.setId("invalid");
		assertThrows(UploadException.class, () -> {
			galaxyLibrariesService.filesToLibraryWait(ImmutableSet.of(dataFile), library, DataStorage.LOCAL);
		});
	}

	/**
	 * Tests failure to upload to a library due to a timeout issue.
	 * 
	 * @throws UploadException
	 * @throws GalaxyDatasetException
	 */
	@Test
	public void testFilesToLibraryWaitFailTimeout() throws UploadException, GalaxyDatasetException {
		galaxyLibrariesService = new GalaxyLibrariesService(librariesClient, 1, 2, 1);

		Library library = buildEmptyLibrary("testFilesToLibraryWaitFailTimeout");
		assertThrows(UploadTimeoutException.class, () -> {
			galaxyLibrariesService.filesToLibraryWait(Sets.newHashSet(dataFile, dataFile2), library, DataStorage.LOCAL);
		});
	}

	/**
	 * Tests failure to upload to a library due to an error with the dataset upload.
	 * 
	 * @throws UploadException
	 * @throws GalaxyDatasetException
	 */
	@Test
	public void testFilesToLibraryWaitFailDatasetError() throws UploadException, GalaxyDatasetException {
		Library library = buildEmptyLibrary("testFilesToLibraryWaitFailDatasetError");
		assertThrows(UploadErrorException.class, () -> {
			galaxyLibrariesService.filesToLibraryWait(Sets.newHashSet(dataFileFail, dataFile2), library,
					DataStorage.LOCAL);
		});
	}

	/**
	 * Tests successfully deleting a data library.
	 * 
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testDeleteLibrarySuccess() throws ExecutionManagerException {
		Library library = buildEmptyLibrary("testDeleteLibrarySuccess");
		Map<Path, String> datasetsMap = galaxyLibrariesService.filesToLibraryWait(ImmutableSet.of(dataFile), library,
				DataStorage.LOCAL);
		String datasetId = datasetsMap.get(dataFile);
		assertNotNull(librariesClient.showDataset(library.getId(), datasetId), "Dataset not uploaded correctly");

		// Note: I cannot do much more to test deleting a library beyond making
		// sure no exception is thrown.
		// The Galaxy API still provides access to libraries even when deleted,
		// but sets a deleted status.
		// The status is not available in blend4j right now.
		galaxyLibrariesService.deleteLibrary(library.getId());
	}

	/**
	 * Tests failure to delete a data library.
	 * 
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testDeleteLibraryFail() throws ExecutionManagerException {
		assertThrows(DeleteGalaxyObjectFailedException.class, () -> {
			galaxyLibrariesService.deleteLibrary("invalid");
		});
	}
}
