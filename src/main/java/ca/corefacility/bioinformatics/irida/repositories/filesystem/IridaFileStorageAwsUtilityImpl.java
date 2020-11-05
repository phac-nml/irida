package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.util.FileUtils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

/**
 * Component implementation of file utitlities for aws storage
 */
@Component
public class IridaFileStorageAwsUtilityImpl implements IridaFileStorageUtility{
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageAwsUtilityImpl.class);

	private String bucketName;
	private AmazonS3 s3;

	@Autowired
	public IridaFileStorageAwsUtilityImpl(String bucketName){
		this.s3 = AmazonS3ClientBuilder.standard().build();
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

			S3Object s3Object = s3.getObject(bucketName, getAwsFileAbsolutePath(file));
			S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();

			org.apache.commons.io.FileUtils.copyInputStreamToFile(s3ObjectInputStream, tempFile.toFile());

			s3ObjectInputStream.close();
			s3Object.close();

			return new IridaTemporaryFile(tempFile, tempDirectory);

		} catch (AmazonServiceException e) {
			logger.error(e.getErrorMessage());
			throw new StorageException(e.getMessage());
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			throw new StorageException(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new StorageException(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanupDownloadedLocalTemporaryFiles(IridaTemporaryFile iridaTemporaryFile) {
		try {
			if(iridaTemporaryFile.getFile() != null && Files.isRegularFile(iridaTemporaryFile.getFile())) {
				logger.trace("Cleaning up temporary file downloaded from aws s3 [" + iridaTemporaryFile.getFile().toString() + "]");
				Files.delete(iridaTemporaryFile.getFile());
			}
		} catch (IOException e) {
			logger.error("Unable to delete local file", e);
			throw new StorageException(e.getMessage());
		}

		try {
			if(iridaTemporaryFile.getDirectoryPath() != null && Files.isDirectory(iridaTemporaryFile.getDirectoryPath())) {
				logger.trace("Cleaning up temporary directory created for aws s3 temporary file [" + iridaTemporaryFile.getDirectoryPath().toString() + "]");
				org.apache.commons.io.FileUtils.deleteDirectory(iridaTemporaryFile.getDirectoryPath().toFile());
			}
		} catch (IOException e) {
			logger.error("Unable to delete local directory", e);
			throw new StorageException(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFileSize(Path file) {
		String fileSize = "N/A";
		try {
			S3Object s3Object = s3.getObject(bucketName, getAwsFileAbsolutePath(file));
			fileSize = FileUtils.humanReadableByteCount(s3Object.getObjectMetadata()
					.getContentLength(), true);
			try {
				s3Object.close();
			} catch (IOException e) {
				logger.error("Unable to close connection to s3object: " + e);
			}
		} catch (AmazonServiceException e) {
			logger.error("Unable to get file size from s3 bucket: " + e);
		}

		return fileSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeFile(Path source, Path target, Path sequenceFileDir, Path sequenceFileDirWithRevision) {
		try {
			logger.trace("Uploading file to s3 bucket: [" + target.getFileName() + "]");
			s3.putObject(bucketName, getAwsFileAbsolutePath(target), source.toFile());
			logger.trace("File uploaded to s3 bucket: [" + s3.getUrl(bucketName, target.toAbsolutePath().toString()) + "]");
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
	@Override
	public boolean storageTypeIsLocal(){
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getFileName(Path file) {
		String fileName = "";
		try {
			S3Object s3Object = s3.getObject(bucketName, getAwsFileAbsolutePath(file));
			// Since the file system is virtual the full file path is the file name.
			// We split it on "/" and get the last token which is the actual file name.
			String[] nameTokens = s3Object.getKey()
					.split("/");
			fileName = nameTokens[nameTokens.length - 1];
			s3Object.close();
		} catch (AmazonServiceException e) {
			logger.error("Couldn't find file [" + e + "]");
		} catch (IOException e) {
			logger.error("Unable to close connection to s3object: " + e);
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
			logger.trace("Opening input stream to file in s3 bucket [" + file.toString() + "]");
			return s3Object.getObjectContent();
		} catch (AmazonServiceException e) {
			logger.error("Couldn't read file from s3 bucket [" + e + "]");
			throw new StorageException("Unable to locate file in s3 bucket", e);
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
			return ((bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
					&& (bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8)));
		}
	}

	/**
	 * Removes the leading "/" from the absolute path
	 * returns the rest of the path.
	 *
	 * @param file
	 * @return
	 */
	private String getAwsFileAbsolutePath(Path file) {
		String absolutePath = file.toAbsolutePath().toString();
		if(absolutePath.charAt(0) == '/') {
			absolutePath = file.toAbsolutePath()
					.toString()
					.substring(1);
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
							"Extensions of files do not match " + currentExtension + " vs "
									+ selectedExtension);
				}
			}
		}

		return selectedExtension;
	}
}
