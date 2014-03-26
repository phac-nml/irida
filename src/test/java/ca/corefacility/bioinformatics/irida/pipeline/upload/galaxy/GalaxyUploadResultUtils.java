package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;

/**
 * Utility classes for tests for Galaxy uploading.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyUploadResultUtils {
	
	/**
	 * Class for getting access to upload result on successfull upload.
	 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
	 *
	 */
	public static class UploadFinishedRunnerTest implements UploadWorker.UploadFinishedRunner {
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
	public static class UploadExceptionRunnerTest implements UploadWorker.UploadExceptionRunner {
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
