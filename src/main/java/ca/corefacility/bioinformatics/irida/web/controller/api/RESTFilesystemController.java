package ca.corefacility.bioinformatics.irida.web.controller.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;

import com.google.common.net.HttpHeaders;
import io.swagger.v3.oas.annotations.Operation;

/**
 * REST controller to handle IRIDA file storage requests
 */
public class RESTFilesystemController {
	private static final Logger logger = LoggerFactory.getLogger(RESTFilesystemController.class);
	private IridaFileStorageUtility iridaFileStorageUtility;

	@Autowired
	public RESTFilesystemController(IridaFileStorageUtility iridaFileStorageUtility) {
		this.iridaFileStorageUtility = iridaFileStorageUtility;
	}

	/**
	 * Get file storage type
	 *
	 * @return the storage type being used by IRIDA
	 */
	@Operation(operationId = "getFileStorageType", summary = "Gets the file storage type for the running irida instance",
			description = "Gets the file storage type for the running irida instance.", tags = "iridaFileStorageUtilityStorageType")
	@RequestMapping(value = "/api/get-file-storage-type", method = RequestMethod.GET)
	public ResponseResource<String> getIridaFileStorageType() {
		logger.trace("Getting the file storage type for the instance of IRIDA");
		String storageType = iridaFileStorageUtility.getStorageType();

		ResponseResource<String> responseObject = new ResponseResource<>(storageType);
		return responseObject;
	}

	/**
	 * Get file size
	 *
	 * @param filePath The path to the file
	 * @return the file size
	 */
	@Operation(operationId = "getFileSize", summary = "Gets the file size",
			description = "Gets the file size.", tags = "iridaFileStorageUtilityFileSize")
	@RequestMapping(value = "/api/files/get-file-size", method = RequestMethod.GET)
	public ResponseResource<Long> getFileSize(@RequestParam String filePath) {
		logger.trace("Getting the file size");
		try {
			Long fileSize = iridaFileStorageUtility.getFileSizeBytes(Paths.get(filePath));
			ResponseResource<Long> responseObject = new ResponseResource<>(fileSize);
			return responseObject;
		} catch (StorageException e) {
			throw new StorageException("Unable to get file size as the file was not found on storage device", e);
		}
	}

	/**
	 * Get if file exists
	 *
	 * @param filePath The path to the file
	 * @return if the file exists or not
	 */
	@Operation(operationId = "getFileExists", summary = "Gets if the file exists",
			description = "Gets if the file exists.", tags = "iridaFileStorageUtilityFileExists")
	@RequestMapping(value = "/api/files/get-file-exists", method = RequestMethod.GET)
	public ResponseResource<Boolean> getFileExists(@RequestParam String filePath) {
		logger.trace("Getting the file size");
		try {
			boolean fileExists = iridaFileStorageUtility.fileExists(Paths.get(filePath));
			ResponseResource<Boolean> responseObject = new ResponseResource<>(fileExists);
			return responseObject;
		} catch (StorageException e) {
			throw new StorageException("Unable to find file on storage device", e);
		}
	}

	/**
	 * Get file contents
	 *
	 * @param filePath The path to the file
	 */
	@Operation(operationId = "getFileContents", summary = "Gets the file contents",
			description = "Gets the contents for the file.", tags = "iridaFileStorageUtilityFileContents")
	@RequestMapping(value = "/api/files/get-file-contents", method = RequestMethod.GET)
	public void getFileContents(@RequestParam String filePath, HttpServletResponse response) {
		logger.trace("Getting file contents");

		String filename = iridaFileStorageUtility.getFileName(Paths.get(filePath));
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
		response.setHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_OCTET_STREAM));
		response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(iridaFileStorageUtility.getFileSizeBytes(Paths.get(filePath))));

		try (InputStream is = iridaFileStorageUtility.getFileInputStream(Paths.get(filePath));
				OutputStream os = response.getOutputStream();) {
			IOUtils.copy(is, os);
			os.flush();
		} catch (IOException | StorageException e) {
			throw new StorageException("Unable to read file stream", e);
		}
	}
}
