package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploadWorker;

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
	 */
	@Before
	public void setup() throws MalformedURLException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
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
		worker.start();
		worker.join();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
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
		
		UploadFinishedRunnerTest finishedRunnerTest = new UploadFinishedRunnerTest();
		UploadExceptionRunnerTest exceptionRunnerTest = new UploadExceptionRunnerTest();
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		worker.runOnUploadFinished(finishedRunnerTest);
		worker.runOnUploadException(exceptionRunnerTest);
		
		worker.start();
		worker.join();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertEquals(uploadResult, finishedRunnerTest.getFinishedResult());
		assertEquals(uploadResult, worker.getUploadResult());
		assertNull(exceptionRunnerTest.getException());
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
		
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenThrow(uploadException);
		
		UploadFinishedRunnerTest finishedRunnerTest = new UploadFinishedRunnerTest();
		UploadExceptionRunnerTest exceptionRunnerTest = new UploadExceptionRunnerTest();
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		worker.runOnUploadFinished(finishedRunnerTest);
		worker.runOnUploadException(exceptionRunnerTest);
		
		worker.start();
		worker.join();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertEquals(uploadException, exceptionRunnerTest.getException());
		assertNull(worker.getUploadResult());
		assertNull(finishedRunnerTest.getFinishedResult());
	}
	
	/**
	 * Class for getting access to upload result on successfull upload.
	 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
	 *
	 */
	private class UploadFinishedRunnerTest implements UploadWorker.UploadFinishedRunner {
		private UploadResult result = null;
		
		@Override
		public void finish(UploadResult result) {
			this.result = result;
		}
		
		/**
		 * Gets the UploadResult on successfull upload.
		 * @return  The UploadResult.
		 */
		public UploadResult getFinishedResult() {
			return result;
		}
	}
	
	/**
	 * Class for getting access to exception on failed upload.
	 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
	 *
	 */
	private class UploadExceptionRunnerTest implements UploadWorker.UploadExceptionRunner {
		private UploadException exception = null;
		
		/**
		 * Gets the exception raised in the UploadWorker.
		 * @return
		 */
		public UploadException getException() {
			return exception;
		}

		@Override
		public void exception(UploadException uploadException) {
			this.exception = uploadException;
		}
	}
}
