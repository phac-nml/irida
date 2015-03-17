package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.ChangeLibraryPermissionsException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNoRoleException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.LibraryUploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyContentFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoLibraryFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyUploadResult;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploadWorker;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploaderAPI;

import com.sun.jersey.api.client.ClientHandlerException;

/**
 * Unit tests for GalaxyUploadWorker.
 *
 */
public class GalaxyUploadWorkerTest {
	private GalaxyAccountEmail userName;
	private GalaxyProjectName dataLocation;
	private List<UploadSample> samples;

	@Mock
	private GalaxyUploaderAPI galaxyAPI;
	
	@Mock
	private GalaxyUploadResult uploadResult;
	
	private static float delta = 0.00001f;
	
	/**
	 * Setup objects for test.
	 * @throws MalformedURLException
	 * @throws NoGalaxyContentFoundException 
	 * @throws GalaxyUserNoRoleException 
	 * @throws NoLibraryFoundException 
	 * @throws GalaxyUserNotFoundException 
	 * @throws ChangeLibraryPermissionsException 
	 * @throws CreateLibraryException 
	 * @throws LibraryUploadException 
	 * @throws ConstraintViolationException 
	 * @throws URISyntaxException 
	 */
	@Before
	public void setup() throws MalformedURLException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException, URISyntaxException {
		MockitoAnnotations.initMocks(this);

		userName = new GalaxyAccountEmail("admin@localhost");
		dataLocation = new GalaxyProjectName("Test");
		
		samples = new ArrayList<UploadSample>();
	}
	
	/**
	 * Tests general upload without overriding behaviours on finished upload or on exceptions.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws NoLibraryFoundException
	 * @throws NoGalaxyContentFoundException
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test
	public void testUpload() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, NoLibraryFoundException, NoGalaxyContentFoundException, ExecutionManagerObjectNotFoundException {
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenReturn(uploadResult);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		assertEquals(0.0f, worker.getProportionComplete(), delta);
		
		worker.run();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
	}
	
	/**
	 * Tests successful upload and running of finished method.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws NoLibraryFoundException
	 * @throws NoGalaxyContentFoundException
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test
	public void testUploadSuccess() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, NoLibraryFoundException, NoGalaxyContentFoundException, ExecutionManagerObjectNotFoundException {
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenReturn(uploadResult);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		assertEquals(0.0f, worker.getProportionComplete(), delta);
		
		worker.run();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertTrue(worker.isFinished());
		assertEquals(uploadResult, worker.getUploadResult());
		assertFalse(worker.exceptionOccured());
		assertNull(worker.getUploadException());
	}
	
	/**
	 * Tests successful upload and running of finished method in separate thread.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws NoLibraryFoundException
	 * @throws NoGalaxyContentFoundException
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test
	public void testUploadSuccessSeparateThread() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, NoLibraryFoundException, NoGalaxyContentFoundException, ExecutionManagerObjectNotFoundException {
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenReturn(uploadResult);
				
		LinkedList<UploadResult> expectedUploadResults = new LinkedList<UploadResult>();
		expectedUploadResults.add(uploadResult);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		assertEquals(0.0f, worker.getProportionComplete(), delta);
		
		Thread t = new Thread(worker);
		t.start();
		t.join();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertTrue(worker.isFinished());
		assertEquals(uploadResult, worker.getUploadResult());
		assertFalse(worker.exceptionOccured());
		assertNull(worker.getUploadException());
	}
	
	/**
	 * Tests failed upload and running of exception methods.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws NoLibraryFoundException
	 * @throws NoGalaxyContentFoundException
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test
	public void testUploadException() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, NoLibraryFoundException, NoGalaxyContentFoundException, ExecutionManagerObjectNotFoundException {
		UploadException uploadException = new LibraryUploadException("exception");
		
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenThrow(uploadException);
		
		List<UploadException> expectedUploadExceptions = new LinkedList<UploadException>();
		expectedUploadExceptions.add(uploadException);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		assertEquals(0.0f, worker.getProportionComplete(), delta);
		
		worker.run();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertTrue(worker.isFinished());
		assertTrue(worker.exceptionOccured());
		assertEquals(uploadException, worker.getUploadException());
		assertNull(worker.getUploadResult());
	}
	
	/**
	 * Tests failed upload and running of exception methods in separate thread.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws NoLibraryFoundException
	 * @throws NoGalaxyContentFoundException
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test
	public void testUploadExceptionMultiThread() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, NoLibraryFoundException, NoGalaxyContentFoundException, ExecutionManagerObjectNotFoundException {
		UploadException uploadException = new LibraryUploadException("exception");
		
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenThrow(uploadException);
		
		List<UploadException> expectedUploadExceptions = new LinkedList<UploadException>();
		expectedUploadExceptions.add(uploadException);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		assertEquals(0.0f, worker.getProportionComplete(), delta);
		
		Thread t = new Thread(worker);
		t.start();
		t.join();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertTrue(worker.exceptionOccured());
		assertEquals(uploadException, worker.getUploadException());
		assertNull(worker.getUploadResult());
	}
	
	/**
	 * Tests failed upload for connection exception.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws NoLibraryFoundException
	 * @throws NoGalaxyContentFoundException
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test
	public void testUploadNoGalaxyConnection() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, NoLibraryFoundException, NoGalaxyContentFoundException, ExecutionManagerObjectNotFoundException {		
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenThrow(
				new ClientHandlerException("error connecting"));
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		assertEquals(0.0f, worker.getProportionComplete(), delta);
		
		worker.run();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertNull(worker.getUploadResult());
	}
}
