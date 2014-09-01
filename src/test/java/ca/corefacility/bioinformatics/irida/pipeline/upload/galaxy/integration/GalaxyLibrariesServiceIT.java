package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.workflow.InputFileType;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader.DataStorage;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyRoleSearch;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryDataset;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Tests for dealing with Galaxy Libraries.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class, NonWindowsLocalGalaxyConfig.class, WindowsLocalGalaxyConfig.class  })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class GalaxyLibrariesServiceIT {
	
	@Autowired
	private LocalGalaxy localGalaxy;
	
	private GalaxyLibrariesService galaxyLibrariesService;
	
	private Path dataFile;
	@SuppressWarnings("unused")
	private Path dataFile2;
	
	private GalaxyInstance galaxyInstanceAdmin;
	
	private static final InputFileType FILE_TYPE = InputFileType.FASTQ_SANGER;
	
	/**
	 * Sets up variables for tests
	 * @throws URISyntaxException 
	 */
	@Before
	public void setup() throws URISyntaxException {
		galaxyInstanceAdmin = localGalaxy.getGalaxyInstanceAdmin();
		LibrariesClient librariesClient = galaxyInstanceAdmin.getLibrariesClient();
		
		galaxyLibrariesService = new GalaxyLibrariesService(librariesClient);
		
		dataFile = Paths.get(GalaxyAPIIT.class.getResource(
				"testData1.fastq").toURI());
		
		dataFile2 = Paths.get(GalaxyAPIIT.class.getResource(
				"testData2.fastq").toURI());			
	}
	
	/**
	 * Builds a library with the given name.
	 * @param name  The name of the new library.
	 * @return  A library with the given name.
	 * @throws CreateLibraryException
	 */
	private Library buildEmptyLibrary(String name) throws CreateLibraryException {
		LibrariesClient librariesClient = galaxyInstanceAdmin.getLibrariesClient();
		GalaxyRoleSearch galaxyRoleSearch = new GalaxyRoleSearch(galaxyInstanceAdmin.getRolesClient(),
				localGalaxy.getGalaxyURL());
		GalaxyLibraryBuilder libraryBuilder = new GalaxyLibraryBuilder(librariesClient, galaxyRoleSearch,
				localGalaxy.getGalaxyURL());
		
		return libraryBuilder.buildEmptyLibrary(new GalaxyProjectName(name));
	}
	
	/**
	 * Tests successful upload of a file to a Galaxy Library.
	 * 
	 * @throws UploadException
	 * @throws GalaxyDatasetException
	 */
	@Test
	public void testFileToLibrarySuccess()
			throws UploadException, GalaxyDatasetException {
		String filename = dataFile.toFile().getName();
		Library library = buildEmptyLibrary("testFileToLibrarySuccess");
		String datasetId = galaxyLibrariesService.fileToLibrary(dataFile,
				FILE_TYPE, library, DataStorage.LOCAL);
		assertNotNull(datasetId);
		LibraryDataset actualDataset = localGalaxy.getGalaxyInstanceAdmin()
				.getLibrariesClient().showDataset(library.getId(), datasetId);
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
	@Test(expected = UploadException.class)
	public void testFileToLibraryFailure()
			throws UploadException, GalaxyDatasetException {
		Library library = buildEmptyLibrary("testFileToLibraryFailure");
		library.setId("invalid");
		galaxyLibrariesService.fileToLibrary(dataFile,
				FILE_TYPE, library, DataStorage.LOCAL);
	}
}
