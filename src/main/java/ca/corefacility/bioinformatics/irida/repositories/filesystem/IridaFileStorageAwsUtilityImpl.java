package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.enums.StorageType;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.analysis.FileChunkResponse;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

/**
 * Implementation of file utilities for aws storage
 */

public class IridaFileStorageAwsUtilityImpl implements IridaFileStorageUtility {
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageAwsUtilityImpl.class);

	private String bucketName;
	private BasicAWSCredentials awsCreds;
	private AmazonS3 s3;
	private final StorageType storageType = StorageType.AWS;

	@Autowired
	public IridaFileStorageAwsUtilityImpl(String bucketName, String bucketRegion, String accessKey, String secretKey) {
		this.awsCreds = new BasicAWSCredentials(accessKey, secretKey);
		this.s3 = AmazonS3ClientBuilder.standard()
				.withRegion(bucketRegion)
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
				.build();
		this.bucketName = bucketName;
	}

	/*
	This instantiation method is for TESTING ONLY. DO NOT USE IN PRODUCTION. USE THE METHOD ABOVE FOR PRODUCTION.
	 */
	public IridaFileStorageAwsUtilityImpl(AmazonS3 s3Client, String bucketName) {
		this.s3 = s3Client;
		this.bucketName = bucketName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IridaTemporaryFile getTemporaryFile(Path file) {
		try {
			logger.trace("Getting file from aws s3 [" + file.toString() + "]");
			Path tempDirectory = Files.createTempDirectory("aws-tmp-");
			Path tempFile = tempDirectory.resolve(file.getFileName().toString());

			try (S3Object s3Object = s3.getObject(bucketName, getAwsFileAbsolutePath(file));
					S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent()) {
				org.apache.commons.io.FileUtils.copyInputStreamToFile(s3ObjectInputStream, tempFile.toFile());
			} catch (AmazonServiceException e) {
				logger.error(e.getMessage());
				throw new StorageException("Unable to read object from aws s3 bucket", e);
			}

			return new IridaTemporaryFile(tempFile, tempDirectory);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			throw new StorageException("Unable to resolve temp file in temp directory", e);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new StorageException("Unable to create temp directory", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IridaTemporaryFile getTemporaryFile(Path file, String prefix) {
		try {
			logger.trace("Getting file from aws s3 [" + file.toString() + "]");
			Path tempDirectory = Files.createTempDirectory(prefix + "-aws-tmp-");
			Path tempFile = tempDirectory.resolve(file.getFileName().toString());

			try (S3Object s3Object = s3.getObject(bucketName, getAwsFileAbsolutePath(file));
					S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent()) {
				org.apache.commons.io.FileUtils.copyInputStreamToFile(s3ObjectInputStream, tempFile.toFile());
			} catch (AmazonServiceException e) {
				logger.error(e.getMessage());
				throw new StorageException("Unable to read object from aws s3 bucket", e);
			}

			return new IridaTemporaryFile(tempFile, tempDirectory);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			throw new StorageException("Unable to resolve temp file in temp directory", e);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new StorageException("Unable to create temp directory", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanupDownloadedLocalTemporaryFiles(IridaTemporaryFile iridaTemporaryFile) {
		try {
			if (iridaTemporaryFile.getFile() != null && Files.isRegularFile(iridaTemporaryFile.getFile())) {
				logger.trace(
						"Cleaning up temporary file downloaded from aws s3 [" + iridaTemporaryFile.getFile().toString()
								+ "]");
				Files.delete(iridaTemporaryFile.getFile());
			}
		} catch (IOException e) {
			logger.error("Unable to delete local file", e);
			throw new StorageException("Unable to delete local file", e);
		}

		try {
			if (iridaTemporaryFile.getDirectoryPath() != null && Files.isDirectory(
					iridaTemporaryFile.getDirectoryPath())) {
				logger.trace("Cleaning up temporary directory created for aws s3 temporary file ["
						+ iridaTemporaryFile.getDirectoryPath().toString() + "]");
				org.apache.commons.io.FileUtils.deleteDirectory(iridaTemporaryFile.getDirectoryPath().toFile());
			}
		} catch (IOException e) {
			logger.error("Unable to delete local directory", e);
			throw new StorageException("Unable to delete local directory", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeFile(Path source, Path target, Path sequenceFileDir, Path sequenceFileDirWithRevision) {
		try {
			logger.trace("Uploading file to s3 bucket: [" + target.getFileName() + "]");
			s3.putObject(bucketName, getAwsFileAbsolutePath(target), source.toFile());
			logger.trace(
					"File uploaded to s3 bucket: [" + s3.getUrl(bucketName, target.toAbsolutePath().toString()) + "]");
		} catch (AmazonServiceException e) {
			logger.error("Unable to upload file to s3 bucket: " + e);
			throw new StorageException("Unable to upload file s3 bucket", e);
		}

		try {
			// The source file is the temp file which is no longer required
			Files.deleteIfExists(source);
		} catch (IOException e) {
			logger.error("Unable to clean up source file", e);
			throw new StorageException("Unable to clean up source file", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getFileName(Path file) {
		String fileName = "";
		try (S3Object s3Object = s3.getObject(bucketName, getAwsFileAbsolutePath(file))) {
			// Since the file system is virtual the full file path is the file name.
			// We split it on "/" and get the last token which is the actual file name.
			String[] nameTokens = s3Object.getKey().split("/");
			fileName = nameTokens[nameTokens.length - 1];
		} catch (AmazonServiceException e) {
			logger.error("Couldn't find file [" + e + "]");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return fileName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean fileExists(Path file) {
		return s3.doesObjectExist(bucketName, getAwsFileAbsolutePath(file));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream getFileInputStream(Path file) {
		try {
			S3Object s3Object = s3.getObject(bucketName, getAwsFileAbsolutePath(file));
			return s3Object.getObjectContent();
		} catch (AmazonServiceException e) {
			logger.error("Couldn't read file from s3 bucket [" + e + "]");
			throw new StorageException("Unable to locate file in s3 bucket", e);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new StorageException("Unable to read file inputstream from s3 bucket", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isGzipped(Path file) throws IOException {
		try (InputStream inputStream = getFileInputStream(file)) {
			byte[] bytes = new byte[2];
			inputStream.read(bytes);
			return ((bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC
					>> 8)));
		}
	}

	/**
	 * Removes the leading "/" from the absolute path returns the rest of the path.
	 *
	 * @param file
	 * @return
	 */
	private String getAwsFileAbsolutePath(Path file) {
		String absolutePath = file.toAbsolutePath().toString();
		if (absolutePath.charAt(0) == '/') {
			absolutePath = file.toAbsolutePath().toString().substring(1);
		}
		return absolutePath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToFile(Path target, SequenceFile file) throws IOException {
		IridaTemporaryFile iridaTemporaryFile = getTemporaryFile(file.getFile());
		try (FileChannel out = FileChannel.open(target, StandardOpenOption.CREATE, StandardOpenOption.APPEND,
				StandardOpenOption.WRITE)) {
			try (FileChannel in = new FileInputStream(iridaTemporaryFile.getFile().toFile()).getChannel()) {
				for (long p = 0, l = in.size(); p < l; ) {
					p += in.transferTo(p, l - p, out);
				}
			} catch (IOException e) {
				throw new StorageException("Could not open input file for reading", e);
			}

		} catch (IOException e) {
			throw new StorageException("Could not open target file for writing", e);
		} finally {
			cleanupDownloadedLocalTemporaryFiles(iridaTemporaryFile);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFileExtension(List<? extends SequencingObject> sequencingObjects) throws IOException {
		String selectedExtension = null;
		for (SequencingObject object : sequencingObjects) {

			for (SequenceFile file : object.getFiles()) {
				String fileName = getFileName(file.getFile());

				Optional<String> currentExtensionOpt = VALID_CONCATENATION_EXTENSIONS.stream()
						.filter(e -> fileName.endsWith(e))
						.findFirst();

				if (!currentExtensionOpt.isPresent()) {
					throw new IOException("File extension is not valid " + fileName);
				}

				String currentExtension = currentExtensionOpt.get();

				if (selectedExtension == null) {
					selectedExtension = currentExtensionOpt.get();
				} else if (selectedExtension != currentExtensionOpt.get()) {
					throw new IOException(
							"Extensions of files do not match " + currentExtension + " vs " + selectedExtension);
				}
			}
		}

		return selectedExtension;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] readAllBytes(Path file) {
		byte[] bytes = new byte[0];
		try (S3Object s3Object = s3.getObject(bucketName, getAwsFileAbsolutePath(file));
				S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent()) {
			bytes = s3ObjectInputStream.readAllBytes();
		} catch (AmazonServiceException e) {
			logger.error(e.getMessage());
			throw new StorageException("Unable to read object from aws s3 bucket", e);
		} catch (IOException e) {
			logger.error("Couldn't get bytes from file [" + e + "]");
		}
		return bytes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getFileSizeBytes(Path file) {
		Long fileSize = 0L;

		try (S3Object s3Object = s3.getObject(bucketName, getAwsFileAbsolutePath(file))) {
			fileSize = s3Object.getObjectMetadata().getContentLength();
		} catch (AmazonServiceException e) {
			logger.error("Unable to get file size from s3 bucket: " + e);
		} catch (IOException e) {
			logger.error("Unable to close connection to s3object: " + e);
		}

		return fileSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileChunkResponse readChunk(Path file, Long seek, Long chunk) {
		List<String> bucketPermissions = getBucketPermissions();

		if(bucketPermissions.size() > 0) {
			/*
			 The range of bytes to read. Start at seek and get `chunk` amount of bytes from seek point.
			 However a smaller amount of bytes may be read, so we set the file pointer accordingly. The code
			 below uses getBucketAcl. So if bucket permissions aren't set then the else code is used.
			 */
			GetObjectRequest rangeObjectRequest = new GetObjectRequest(bucketName, getAwsFileAbsolutePath(file)).withRange(seek, chunk);
			try (S3Object s3Object = s3.getObject(rangeObjectRequest);
					S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent()) {
				byte[] bytes = s3ObjectInputStream.readAllBytes();
				return new FileChunkResponse(new String(bytes), seek + (bytes.length - 1));
			} catch (IOException e) {
				logger.error("Couldn't get chunk from s3 bucket", e);
			}
		} else {
			try (S3Object s3Object = s3.getObject(bucketName, getAwsFileAbsolutePath(file));
					S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent()) {
							byte[] bytes = new byte[seek.intValue() + chunk.intValue()];
							s3ObjectInputStream.readNBytes(bytes, 0, seek.intValue() + chunk.intValue());
							byte[] bytesForRange = Arrays.copyOfRange(bytes, seek.intValue(), seek.intValue() + chunk.intValue());
							return new FileChunkResponse(new String(bytesForRange), seek + (bytesForRange.length));
			} catch (IOException e) {
				logger.error("Couldn't get chunk from s3 bucket", e);
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkWriteAccess(Path baseDirectory) {
		// get list of bucket permissions
		List<String> bucketPermissions = getBucketPermissions();

		// If bucket permissions are available
		if (bucketPermissions.size() > 0) {
			// check read/write or full control permission
			if (!((bucketPermissions.contains("READ") && bucketPermissions.contains("WRITE"))
					|| (bucketPermissions.contains("FULL_CONTROL")))) {
				throw new StorageException("Unable to read and/or write to aws s3 bucket " + bucketName
						+ ". Please check bucket has both read and write permissions.");
			}
			return true;
		} else {
			// If bucket permissions are not available we try to read/write from/to bucket
			try {
				Path tempDirectory = Files.createTempDirectory(null);
				Path tempFile = tempDirectory.resolve("testAwsContainerReadWrite.txt");
				// write a line
				Files.write(tempFile, "AWS check read/write permissions.\n".getBytes(StandardCharsets.UTF_8));
				try {
					s3.putObject(bucketName, getAwsFileAbsolutePath(baseDirectory) + "/" + tempFile.getFileName(), tempFile.toFile());
					s3.deleteObject(bucketName, getAwsFileAbsolutePath(baseDirectory) + "/" + tempFile.getFileName());
					return true;
				} catch (AmazonServiceException e) {
					throw new StorageException("Unable to read and/or write to aws s3 bucket " + bucketName
							+ ". Please check bucket has both read and write permissions.", e);
				} finally {
					// Cleanup the temporary file on the server
					Files.delete(tempFile);
					org.apache.commons.io.FileUtils.deleteDirectory(tempDirectory.toFile());
				}
			} catch (IOException e) {
				throw new StorageException("Unable to clean up temporary file", e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStorageTypeLocal() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getStorageType() {
		return storageType.toString();
	}

	/**
	 * Gets the bucket permissions
	 * @return the permissions on the bucket
	 */
	private List<String> getBucketPermissions() {
		return s3.getBucketAcl(bucketName)
				.getGrantsAsList()
				.stream()
				.distinct()
				.map(t -> t.getPermission().toString())
				.collect(Collectors.toList());
	}
}
