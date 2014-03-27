package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

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

import com.sun.jersey.api.client.ClientHandlerException;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

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

import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploadWorker;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.UploadEventListenerTracker;

/**
 * Unit tests for GalaxyUploadWorker.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyUploadWorkerTest {
	private GalaxyAccountEmail userName;
	private GalaxyProjectName dataLocation;
	private List<UploadSample> samples;

	@Mock
	private GalaxyAPI galaxyAPI;
	
	@Mock
	private GalaxyUploadResult uploadResult;
	
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
	 * @throws GalaxyUserNotFoundException
	 * @throws NoLibraryFoundException
	 * @throws GalaxyUserNoRoleException
	 * @throws NoGalaxyContentFoundException
	 */
	@Test
	public void testUpload() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenReturn(uploadResult);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		worker.run();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
	}
	
	/**
	 * Tests attaching a SampleProgressListener.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws GalaxyUserNotFoundException
	 * @throws NoLibraryFoundException
	 * @throws GalaxyUserNoRoleException
	 * @throws NoGalaxyContentFoundException
	 */
	@Test
	public void testSampleProgressListenerAttach() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		UploadEventListenerTracker eventListener = new UploadEventListenerTracker();
		worker.addUploadEventListener(eventListener);
		
		verify(galaxyAPI).addUploadEventListener(eventListener);
	}
	
	/**
	 * Tests attaching an invalid SampleProgressListener.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws GalaxyUserNotFoundException
	 * @throws NoLibraryFoundException
	 * @throws GalaxyUserNoRoleException
	 * @throws NoGalaxyContentFoundException
	 */
	@Test(expected=NullPointerException.class)
	public void testSampleProgressListenerAttachInvalid() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		worker.addUploadEventListener(null);
	}
	
	/**
	 * Tests successful upload and running of finished method.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws GalaxyUserNotFoundException
	 * @throws NoLibraryFoundException
	 * @throws GalaxyUserNoRoleException
	 * @throws NoGalaxyContentFoundException
	 */
	@Test
	public void testUploadSuccess() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenReturn(uploadResult);
		
		UploadEventListenerTracker eventListener = new UploadEventListenerTracker();
		
		LinkedList<UploadResult> expectedUploadResults = new LinkedList<UploadResult>();
		expectedUploadResults.add(uploadResult);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		worker.addUploadEventListener(eventListener);
		
		worker.run();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertEquals(expectedUploadResults, eventListener.getResults());
		assertEquals(uploadResult, worker.getUploadResult());
		assertEquals(0, eventListener.getExceptions().size());
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
	 * @throws GalaxyUserNotFoundException
	 * @throws NoLibraryFoundException
	 * @throws GalaxyUserNoRoleException
	 * @throws NoGalaxyContentFoundException
	 */
	@Test
	public void testUploadSuccessSeparateThread() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenReturn(uploadResult);
		
		UploadEventListenerTracker eventListener = new UploadEventListenerTracker();
		
		LinkedList<UploadResult> expectedUploadResults = new LinkedList<UploadResult>();
		expectedUploadResults.add(uploadResult);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		worker.addUploadEventListener(eventListener);
		
		Thread t = new Thread(worker);
		t.start();
		t.join();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertEquals(expectedUploadResults, eventListener.getResults());
		assertEquals(uploadResult, worker.getUploadResult());
		assertEquals(0, eventListener.getExceptions().size());
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
	 * @throws GalaxyUserNotFoundException
	 * @throws NoLibraryFoundException
	 * @throws GalaxyUserNoRoleException
	 * @throws NoGalaxyContentFoundException
	 */
	@Test
	public void testUploadException() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
		UploadException uploadException = new LibraryUploadException("exception");
		UploadEventListenerTracker eventListener = new UploadEventListenerTracker();
		
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenThrow(uploadException);
		
		List<UploadException> expectedUploadExceptions = new LinkedList<UploadException>();
		expectedUploadExceptions.add(uploadException);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		worker.addUploadEventListener(eventListener);
		
		worker.run();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertEquals(expectedUploadExceptions, eventListener.getExceptions());
		assertTrue(worker.exceptionOccured());
		assertEquals(uploadException, worker.getUploadException());
		assertEquals(0, eventListener.getResults().size());
		assertNull(worker.getUploadResult());
	}
	
	/**
	 * Tests failed upload and running of exception methods in separate thread.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws GalaxyUserNotFoundException
	 * @throws NoLibraryFoundException
	 * @throws GalaxyUserNoRoleException
	 * @throws NoGalaxyContentFoundException
	 */
	@Test
	public void testUploadExceptionMultiThread() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
		UploadException uploadException = new LibraryUploadException("exception");
		UploadEventListenerTracker eventListener = new UploadEventListenerTracker();
		
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenThrow(uploadException);
		
		List<UploadException> expectedUploadExceptions = new LinkedList<UploadException>();
		expectedUploadExceptions.add(uploadException);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		worker.addUploadEventListener(eventListener);
		
		Thread t = new Thread(worker);
		t.start();
		t.join();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertEquals(expectedUploadExceptions, eventListener.getExceptions());
		assertTrue(worker.exceptionOccured());
		assertEquals(uploadException, worker.getUploadException());
		assertEquals(0, eventListener.getResults().size());
		assertNull(worker.getUploadResult());
	}
	
	/**
	 * Tests failed upload for connection exception.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws GalaxyUserNotFoundException
	 * @throws NoLibraryFoundException
	 * @throws GalaxyUserNoRoleException
	 * @throws NoGalaxyContentFoundException
	 */
	@Test
	public void testUploadNoGalaxyConnection() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
		UploadEventListenerTracker eventListener = new UploadEventListenerTracker();
		
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenThrow(
				new ClientHandlerException("error connecting"));
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		worker.addUploadEventListener(eventListener);
		
		worker.run();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertEquals(1,eventListener.getExceptions().size());
		assertNull(worker.getUploadResult());
		assertEquals(0,eventListener.getProgressUpdates().size());
		assertEquals(0,eventListener.getResults().size());
	}
}
