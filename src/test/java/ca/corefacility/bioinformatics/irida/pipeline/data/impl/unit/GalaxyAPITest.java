package ca.corefacility.bioinformatics.irida.pipeline.data.impl.unit;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.pipeline.data.impl.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.pipeline.data.impl.GalaxySample;
import ca.corefacility.bioinformatics.irida.pipeline.data.impl.LibraryUploadException;
import ca.corefacility.bioinformatics.irida.pipeline.data.impl.GalaxyAPI;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.FileLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryPermissions;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.github.jmchilton.blend4j.galaxy.beans.User;
import com.sun.jersey.api.client.ClientResponse;

public class GalaxyAPITest
{
	@Mock private WorkflowsClient workflowsClient;
	@Mock private LibrariesClient librariesClient;
	@Mock private LibraryContent libraryContent;
	@Mock private UsersClient usersClient;
	@Mock private RolesClient rolesClient;
	@Mock private GalaxyInstance galaxyInstance;
	@Mock private ClientResponse clientResponse;
	@Mock private ClientResponse okayResponse;
	@Mock private ClientResponse invalidResponse;
	@Mock private com.github.jmchilton.blend4j.galaxy.beans.Workflow blendWorkflow;
	
	final private String realAdminEmail = "admin@localhost";
	final private String invalidAdminEmail = "admin_invalid@localhost";
	final private String realUserEmail = "test@localhost";
	final private String fakeUserEmail = "fake@localhost";
	final private String realRoleId = "1";
	final private String adminRoleId = "0";
	
	private LibraryPermissions correctPermissions;
	
	private GalaxyAPI workflowRESTAPI;
	private File dataFile1;
	private File dataFile2;
	private List<File> dataFilesSingle;
	private List<File> dataFilesDouble;
	
	@Before
	public void setup() throws FileNotFoundException, URISyntaxException
	{		
		MockitoAnnotations.initMocks(this);
		
		User realUser = new User();
		realUser.setEmail(realUserEmail);
		
		User adminUser = new User();
		adminUser.setEmail(realAdminEmail);
		
		Role realRole = new Role();
		realRole.setName(realUserEmail);
		realRole.setId(realRoleId);
		
		Role adminRole = new Role();
		adminRole.setName(realAdminEmail);
		adminRole.setId(adminRoleId);
		
		List<User> userList = new ArrayList<User>();
		userList.add(realUser);
		userList.add(adminUser);
		
		List<Role> roleList = new ArrayList<Role>();
		roleList.add(realRole);
		roleList.add(adminRole);
		
		correctPermissions = new LibraryPermissionsTest();
		correctPermissions.getAccessInRoles().add(realRoleId);
		correctPermissions.getAccessInRoles().add(adminRoleId);
		correctPermissions.getAddInRoles().add(realRoleId);
		correctPermissions.getAddInRoles().add(adminRoleId);
		correctPermissions.getManageInRoles().add(realRoleId);
		correctPermissions.getManageInRoles().add(adminRoleId);
		correctPermissions.getModifyInRoles().add(realRoleId);
		correctPermissions.getModifyInRoles().add(adminRoleId);
		
		when(galaxyInstance.getWorkflowsClient()).thenReturn(workflowsClient);
		when(galaxyInstance.getLibrariesClient()).thenReturn(librariesClient);
		when(galaxyInstance.getUsersClient()).thenReturn(usersClient);
		when(galaxyInstance.getRolesClient()).thenReturn(rolesClient);
		
		when(usersClient.getUsers()).thenReturn(userList);
		when(rolesClient.getRoles()).thenReturn(roleList);
		
		when(okayResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.OK);
		when(invalidResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.FORBIDDEN);
		
		workflowRESTAPI = new GalaxyAPI(galaxyInstance, realAdminEmail);
		
		dataFile1 = new File(this.getClass().getResource("testData1.fastq").toURI());
		dataFile2 = new File(this.getClass().getResource("testData2.fastq").toURI());
		
		dataFilesSingle = new ArrayList<File>();
		dataFilesSingle.add(dataFile1);
		
		dataFilesDouble = new ArrayList<File>();
		dataFilesDouble.add(dataFile1);
		dataFilesDouble.add(dataFile2);
	}
	
	@Test
	public void testBuildGalaxyLibrary() throws URISyntaxException, CreateLibraryException
	{
		String expectedId = "1";
		
		String libraryName = "TestLibrary";
		Library returnedLibrary = new Library(libraryName);
		returnedLibrary.setId(expectedId);
		
		when(librariesClient.createLibrary(any(Library.class))).thenReturn(returnedLibrary);
		when(librariesClient.setLibraryPermissions(expectedId, correctPermissions)).thenReturn(okayResponse);
		
		assertEquals(expectedId, workflowRESTAPI.buildGalaxyLibrary(libraryName,realUserEmail));
		verify(librariesClient).createLibrary(any(Library.class));
		verify(usersClient, times(2)).getUsers();
		verify(rolesClient, times(2)).getRoles();
		verify(librariesClient).setLibraryPermissions(expectedId, correctPermissions);
	}
	
	@Test
	public void testBuildGalaxyLibraryFail() throws URISyntaxException, CreateLibraryException
	{
		String libraryName = "TestLibrary";
		
		when(librariesClient.createLibrary(any(Library.class))).thenReturn(null);
		
		assertNull(workflowRESTAPI.buildGalaxyLibrary(libraryName, realUserEmail));
		verify(usersClient, times(2)).getUsers();
		verify(rolesClient, times(2)).getRoles();
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testBuildGalaxyLibraryNoUser() throws URISyntaxException, CreateLibraryException
	{
		String expectedId = "1";
		
		String libraryName = "TestLibrary";
		Library returnedLibrary = new Library(libraryName);
		returnedLibrary.setId(expectedId);
		
		when(librariesClient.createLibrary(any(Library.class))).thenReturn(returnedLibrary);
		
		assertEquals(expectedId, workflowRESTAPI.buildGalaxyLibrary(libraryName,fakeUserEmail));
		verify(usersClient, times(2)).getUsers();
	}
	
	@Test(expected=RuntimeException.class)
	public void testSetupInvalidAdminEmail() throws URISyntaxException, CreateLibraryException
	{
		workflowRESTAPI = new GalaxyAPI(galaxyInstance, invalidAdminEmail);
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testBuildGalaxyLibraryNoRole() throws URISyntaxException, CreateLibraryException
	{
		String expectedId = "1";
		
		Role realRole = new Role();
		realRole.setName(fakeUserEmail);
		realRole.setId(realRoleId);
		List<Role> roleList = new ArrayList<Role>();
		roleList.add(realRole);
		
		String libraryName = "TestLibrary";
		Library returnedLibrary = new Library(libraryName);
		returnedLibrary.setId(expectedId);
		
		when(rolesClient.getRoles()).thenReturn(roleList);
		
		when(librariesClient.createLibrary(any(Library.class))).thenReturn(returnedLibrary);
		when(librariesClient.setLibraryPermissions(expectedId, correctPermissions)).thenReturn(okayResponse);
		
		assertEquals(expectedId, workflowRESTAPI.buildGalaxyLibrary(libraryName,realUserEmail));
		verify(usersClient, times(2)).getUsers();
		verify(rolesClient, times(2)).getRoles();
	}
	
	@Test(expected=CreateLibraryException.class)
	public void testBuildGalaxyLibraryNoSetPermissions() throws URISyntaxException, CreateLibraryException
	{
		String expectedId = "1";
		
		String libraryName = "TestLibrary";
		Library returnedLibrary = new Library(libraryName);
		returnedLibrary.setId(expectedId);
		
		when(librariesClient.createLibrary(any(Library.class))).thenReturn(returnedLibrary);
		when(librariesClient.setLibraryPermissions(expectedId, correctPermissions)).thenReturn(invalidResponse);
		
		assertEquals(expectedId, workflowRESTAPI.buildGalaxyLibrary(libraryName,realUserEmail));
		verify(usersClient, times(2)).getUsers();
		verify(rolesClient, times(2)).getRoles();
	}
	
	@Test
	public void testUploadSampleToLibrary() throws URISyntaxException, LibraryUploadException
	{
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		String libraryID = "1";
		String rootFolderID = "2";
		List<Library> actualLibraries = new ArrayList<Library>();
		Library library = new Library("testName");
		library.setId(libraryID);
		actualLibraries.add(library);
		LibraryFolder sampleFolder = new LibraryFolderTest();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(rootFolderID);
		
		when(librariesClient.getRootFolder(libraryID)).thenReturn(libraryContent);
		when(librariesClient.getLibraries()).thenReturn(actualLibraries);
		when(librariesClient.createFolder(libraryID, sampleFolder)).thenReturn(sampleFolder);
		when(libraryContent.getId()).thenReturn(rootFolderID);
		when(clientResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.OK);
		when(librariesClient.uploadFile(eq(libraryID), any(FileLibraryUpload.class))).thenReturn(clientResponse);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryID));
		verify(librariesClient).uploadFile(eq(libraryID), any(FileLibraryUpload.class));
		verify(librariesClient).createFolder(libraryID, sampleFolder);
	}
	
	@Test
	public void testUploadSamples() throws URISyntaxException, LibraryUploadException
	{
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		List<Library> actualLibraries = new ArrayList<Library>();
		String libraryID = "1";
		String rootFolderID = "2";
		String libraryName = "TestLibrary";
		Library returnedLibrary = new Library(libraryName);
		returnedLibrary.setId(libraryID);
		actualLibraries.add(returnedLibrary);
		
		LibraryFolder sampleFolder = new LibraryFolderTest();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(rootFolderID);
		
		when(librariesClient.getRootFolder(libraryID)).thenReturn(libraryContent);
		when(librariesClient.createFolder(libraryID, sampleFolder)).thenReturn(sampleFolder);
		when(librariesClient.createLibrary(any(Library.class))).thenReturn(returnedLibrary);
		when(librariesClient.getLibraries()).thenReturn(actualLibraries);
		when(libraryContent.getId()).thenReturn(rootFolderID);
		when(clientResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.OK);
		when(librariesClient.uploadFile(eq(libraryID), any(FileLibraryUpload.class))).thenReturn(clientResponse);
		when(librariesClient.setLibraryPermissions(libraryID, correctPermissions)).thenReturn(okayResponse);
		
		assertTrue(workflowRESTAPI.uploadSamples(samples, "testName", realUserEmail));
		verify(librariesClient).createLibrary(any(Library.class));
		verify(librariesClient).uploadFile(eq(libraryID), any(FileLibraryUpload.class));
		verify(librariesClient).createFolder(libraryID, sampleFolder);
		verify(usersClient, times(2)).getUsers();
		verify(rolesClient, times(2)).getRoles();
	}
	
	@Test
	public void testUploadMultiSampleToLibrary() throws URISyntaxException, LibraryUploadException
	{
		GalaxySample galaxySample1 = new GalaxySample("testData1", dataFilesSingle);		
		GalaxySample galaxySample2 = new GalaxySample("testData2", dataFilesSingle);
		
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample1);
		samples.add(galaxySample2);
		
		String libraryID = "1";
		String rootFolderID = "2";
		List<Library> actualLibraries = new ArrayList<Library>();
		Library library = new Library("testName");
		library.setId(libraryID);
		actualLibraries.add(library);
		
		LibraryFolder sampleFolder1 = new LibraryFolderTest();
		sampleFolder1.setName(galaxySample1.getSampleName());
		sampleFolder1.setFolderId(rootFolderID);
		
		LibraryFolder sampleFolder2 = new LibraryFolderTest();
		sampleFolder2.setName(galaxySample2.getSampleName());
		sampleFolder2.setFolderId(rootFolderID);
		
		when(librariesClient.getRootFolder(libraryID)).thenReturn(libraryContent);
		when(librariesClient.getLibraries()).thenReturn(actualLibraries);
		when(librariesClient.createFolder(libraryID, sampleFolder1)).thenReturn(sampleFolder1);
		when(librariesClient.createFolder(libraryID, sampleFolder2)).thenReturn(sampleFolder2);
		when(libraryContent.getId()).thenReturn(rootFolderID);
		when(clientResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.OK);
		when(librariesClient.uploadFile(eq(libraryID), any(FileLibraryUpload.class))).thenReturn(clientResponse);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryID));
		verify(librariesClient, times(2)).uploadFile(eq(libraryID), any(FileLibraryUpload.class));
		verify(librariesClient).createFolder(libraryID, sampleFolder1);
		verify(librariesClient).createFolder(libraryID, sampleFolder2);
	}
	
	@Test
	public void testUploadMultiFileSampleToLibrary() throws URISyntaxException, LibraryUploadException
	{
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesDouble);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		String libraryID = "1";
		String rootFolderID = "2";
		List<Library> actualLibraries = new ArrayList<Library>();
		Library library = new Library("testName");
		library.setId(libraryID);
		actualLibraries.add(library);
		LibraryFolder sampleFolder = new LibraryFolderTest();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(rootFolderID);
		
		when(librariesClient.getRootFolder(libraryID)).thenReturn(libraryContent);
		when(librariesClient.getLibraries()).thenReturn(actualLibraries);
		when(librariesClient.createFolder(libraryID, sampleFolder)).thenReturn(sampleFolder);
		when(libraryContent.getId()).thenReturn(rootFolderID);
		when(clientResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.OK);
		when(librariesClient.uploadFile(eq(libraryID), any(FileLibraryUpload.class))).thenReturn(clientResponse);
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, libraryID));
		verify(librariesClient, times(2)).uploadFile(eq(libraryID), any(FileLibraryUpload.class));
		verify(librariesClient).createFolder(libraryID, sampleFolder);
	}
	
	@Test
	public void testUploadFilesToLibraryFail() throws URISyntaxException, LibraryUploadException
	{
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);

		String libraryID = "1";
		String rootFolderID = "2";
		List<Library> actualLibraries = new ArrayList<Library>();
		Library library = new Library("testName");
		library.setId(libraryID);
		actualLibraries.add(library);
		LibraryFolder sampleFolder = new LibraryFolderTest();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(rootFolderID);
		
		when(librariesClient.getRootFolder(libraryID)).thenReturn(libraryContent);
		when(librariesClient.getLibraries()).thenReturn(actualLibraries);
		when(librariesClient.createFolder(libraryID, sampleFolder)).thenReturn(sampleFolder);
		when(libraryContent.getId()).thenReturn(rootFolderID);
		when(clientResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.FORBIDDEN);
		when(librariesClient.uploadFile(eq(libraryID), any(FileLibraryUpload.class))).thenReturn(clientResponse);
		
		assertFalse(workflowRESTAPI.uploadFilesToLibrary(samples, libraryID));
		verify(librariesClient).uploadFile(eq(libraryID), any(FileLibraryUpload.class));
		verify(librariesClient).createFolder(libraryID, sampleFolder);
	}
	
	@Test(expected=LibraryUploadException.class)
	public void testNoExistingLibrary() throws URISyntaxException, LibraryUploadException
	{
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);

		String libraryID = "1";
		List<Library> actualLibraries = new ArrayList<Library>();
		Library library = new Library("testName");
		library.setId(libraryID);
		actualLibraries.add(library);
		
		when(librariesClient.getLibraries()).thenReturn(actualLibraries);
		when(librariesClient.setLibraryPermissions(libraryID, correctPermissions)).thenReturn(okayResponse);
		
		workflowRESTAPI.uploadFilesToLibrary(samples, "2");
	}
	
	@Test(expected=LibraryUploadException.class)
	public void testNoRootFolder() throws URISyntaxException, LibraryUploadException
	{
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);

		String libraryID = "1";
		List<Library> actualLibraries = new ArrayList<Library>();
		Library library = new Library("testName");
		library.setId(libraryID);
		actualLibraries.add(library);
		
		when(librariesClient.getLibraries()).thenReturn(actualLibraries);
		when(libraryContent.getId()).thenReturn(null);
		
		workflowRESTAPI.uploadFilesToLibrary(samples, "1");
	}
	
	@Test(expected=LibraryUploadException.class)
	public void testNoCreateSampleFolder() throws URISyntaxException, LibraryUploadException
	{
		GalaxySample galaxySample = new GalaxySample("testData", dataFilesSingle);
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		samples.add(galaxySample);
		
		String libraryID = "1";
		String rootFolderID = "2";
		List<Library> actualLibraries = new ArrayList<Library>();
		Library library = new Library("testName");
		library.setId(libraryID);
		actualLibraries.add(library);
		LibraryFolder sampleFolder = new LibraryFolderTest();
		sampleFolder.setName(galaxySample.getSampleName());
		sampleFolder.setFolderId(rootFolderID);
		
		when(librariesClient.getRootFolder(libraryID)).thenReturn(libraryContent);
		when(librariesClient.getLibraries()).thenReturn(actualLibraries);
		when(librariesClient.createFolder(libraryID, sampleFolder)).thenReturn(null);
		
		workflowRESTAPI.uploadFilesToLibrary(samples, libraryID);
	}
	
	@Test
	public void testUploadNoFiles() throws URISyntaxException, LibraryUploadException
	{
		List<GalaxySample> samples = new ArrayList<GalaxySample>();
		
		assertTrue(workflowRESTAPI.uploadFilesToLibrary(samples, "1"));
	}
	
	/**
	 * Class used to implement equals() method to get testing to work.
	 * @author aaron
	 *
	 */
	private class LibraryFolderTest extends LibraryFolder
	{
		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (!(o instanceof LibraryFolder))
				return false;
			LibraryFolder l = (LibraryFolder)o;
			
			if (this.getName() != l.getName())
				return false;
			if (this.getName() == null)
				return false;
			if (! this.getName().equals(l.getName()))
				return false;
			
			if (this.getCreateType() != l.getCreateType())
				return false;
			if (this.getCreateType() == null)
				return false;
			if (! this.getCreateType().equals(l.getCreateType()))
				return false;
			
			if (this.getDescription() != l.getDescription())
				return false;
			if (this.getDescription() == null)
				return false;
			if (! this.getDescription().equals(l.getDescription()))
				return false;
			
			if (this.getFolderId() != l.getFolderId())
				return false;
			if (this.getFolderId() == null)
				return false;
			if (! this.getFolderId().equals(l.getFolderId()))
				return false;
			
			return true;
		}
	}
	
	/**
	 * Class used to implement equals() method to get testing to work.
	 * @author aaron
	 *
	 */
	private class LibraryPermissionsTest extends LibraryPermissions
	{
		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (!(o instanceof LibraryPermissions))
				return false;
			LibraryPermissions l = (LibraryPermissions)o;
			
			if (!this.getAccessInRoles().equals(l.getAccessInRoles()))
				return false;
			if (!this.getModifyInRoles().equals(l.getModifyInRoles()))
				return false;
			if (!this.getAddInRoles().equals(l.getAddInRoles()))
				return false;
			if (!this.getManageInRoles().equals(l.getManageInRoles()))
				return false;
			
			return true;
		}
	}
}
