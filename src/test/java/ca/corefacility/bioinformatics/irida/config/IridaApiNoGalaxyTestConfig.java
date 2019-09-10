package ca.corefacility.bioinformatics.irida.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.AsyncResult;

import ca.corefacility.bioinformatics.irida.config.workflow.IridaWorkflowsTestConfig;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyCleanupAsync;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.DirectoryLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.FileLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.FilesystemPathsLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.GalaxyObject;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContentsProvenance;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDeleteResponse;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryExport;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryDataset;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryPermissions;
import com.github.jmchilton.blend4j.galaxy.beans.Tool;
import com.github.jmchilton.blend4j.galaxy.beans.ToolExecution;
import com.github.jmchilton.blend4j.galaxy.beans.ToolInputs;
import com.github.jmchilton.blend4j.galaxy.beans.ToolSection;
import com.github.jmchilton.blend4j.galaxy.beans.UrlLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionDescription;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
import com.google.common.util.concurrent.MoreExecutors;
import com.sun.jersey.api.client.ClientResponse;

/**
 * Config name for test services that need to be setup.
 *
 *
 */
@Configuration
@Import({ IridaWorkflowsTestConfig.class })
@Profile("test")
public class IridaApiNoGalaxyTestConfig {
	private static final Logger logger = LoggerFactory.getLogger(IridaApiNoGalaxyTestConfig.class);

	/**
	 * @return An ExecutorService executing code in the same thread for testing
	 *         purposes.
	 */
	@Bean
	public Executor uploadExecutor() {
		return MoreExecutors.directExecutor();
	}

	@Bean
	public AnalysisExecutionServiceGalaxyCleanupAsync analysisExecutionServiceGalaxyCleanupAsync() {
		return new AnalysisExecutionServiceGalaxyCleanupAsync(null, null, null, null) {

			@Override
			public Future<AnalysisSubmission> cleanupSubmission(AnalysisSubmission analysisSubmission)
					throws ExecutionManagerException {
				logger.info("\"Cleaning\" up submission [" + analysisSubmission + "] (but secretly doing nothing!)");
				return new AsyncResult<AnalysisSubmission>(analysisSubmission);
			}
		};
	}

	@Bean
	public GalaxyHistoriesService galaxyHistoriesService(HistoriesClient historiesClient, ToolsClient toolsClient,
			GalaxyLibrariesService librariesService) {
		return new GalaxyHistoriesService(historiesClient, toolsClient, librariesService);
	}

	@Bean
	public GalaxyLibrariesService galaxyLibrariesService(LibrariesClient librariesClient) {
		return new GalaxyLibrariesService(librariesClient, 5, 60, 1);
	}

	@Bean
	public LibrariesClient librariesClient() {
		return new LibrariesClientStub();
	}

	@Bean
	public HistoriesClient historiesClient() {
		return new HistoriesClientStub();
	}

	@Bean
	public ToolsClient toolsClient() {
		return new ToolsClientStub();
	}

	/**
	 * Stub class for tests.
	 */
	private static class LibrariesClientStub implements LibrariesClient {

		@Override
		public LibraryFolder createFolder(String arg0, LibraryFolder arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse createFolderRequest(String arg0, LibraryFolder arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Library createLibrary(Library arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse createLibraryRequest(Library arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<Library> getLibraries() {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<LibraryContent> getLibraryContents(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public LibraryContent getRootFolder(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse setLibraryPermissions(String arg0, LibraryPermissions arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public LibraryDataset showDataset(String arg0, String arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse showDatasetRequest(String arg0, String arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse uploadFile(String arg0, FileLibraryUpload arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse uploadFileFromUrl(String arg0, FilesystemPathsLibraryUpload arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse uploadFileFromUrlRequest(String arg0, UrlLibraryUpload arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public GalaxyObject uploadFilesystemPaths(String arg0, FilesystemPathsLibraryUpload arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse uploadFilesystemPathsRequest(String arg0, FilesystemPathsLibraryUpload arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse uploadServerDirectoryRequest(String arg0, DirectoryLibraryUpload arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse deleteLibraryRequest(String libraryId) {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Stub class for tests.
	 */
	private static class ToolsClientStub implements ToolsClient {

		@Override
		public ToolExecution create(History arg0, ToolInputs arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse fileUploadRequest(String arg0, String arg1, String arg2, File arg3) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<ToolSection> getTools() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Tool showTool(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ToolExecution upload(FileUploadRequest arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse uploadRequest(FileUploadRequest arg0) {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Stub class for tests.
	 */
	private static class HistoriesClientStub implements HistoriesClient {

		@Override
		public History create(History arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public CollectionResponse createDatasetCollection(String arg0, CollectionDescription arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse createDatasetCollectionRequest(String arg0, CollectionDescription arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public HistoryDetails createHistoryDataset(String arg0, HistoryDataset arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse createRequest(History arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void downloadDataset(String arg0, String arg1, File arg2) throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
		public HistoryExport exportHistory(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<History> getHistories() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Dataset showDataset(String arg0, String arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public CollectionResponse showDatasetCollection(String arg0, String arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public HistoryDetails showHistory(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<HistoryContents> showHistoryContents(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse showHistoryRequest(String arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public HistoryContentsProvenance showProvenance(String arg0, String arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ClientResponse deleteHistoryRequest(String historyId) {
			throw new UnsupportedOperationException();
		}

		@Override
		public HistoryDeleteResponse deleteHistory(String historyId) {
			throw new UnsupportedOperationException();
		}
	}
}
