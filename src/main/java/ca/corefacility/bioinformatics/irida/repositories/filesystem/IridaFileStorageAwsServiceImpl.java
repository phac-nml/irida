package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

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
public class IridaFileStorageAwsServiceImpl implements IridaFileStorageService{
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageAwsServiceImpl.class);

	private String bucketName;
	private BasicAWSCredentials awsCreds;
	private AmazonS3 s3;

	@Autowired
	public IridaFileStorageAwsServiceImpl(String bucketName, String bucketRegion, String accessKey, String secretKey){
		this.awsCreds = new BasicAWSCredentials(accessKey, secretKey);
		this.s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(bucketRegion))
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
		this.bucketName = bucketName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getTemporaryFile(Path file) {
		File fileToProcess = null;

		try {

			String tmpDir = "/tmp/" + new Date().toString().replaceAll("\\W", "");

			// Since the file system is virtual the full file path is the file name.
			// We split it on "/" and get the last token which is the actual file name.
			String [] nameTokens = file.toAbsolutePath().toString().split("/");
			String fileName = nameTokens[nameTokens.length-1];

			S3Object s3Object = s3.getObject(bucketName, getAwsFileAbsolutePath(file));
			S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();

			fileToProcess = new File(tmpDir+fileName);

			// Stream the file from the bucket into a local file
			FileOutputStream fileOutputStream = new FileOutputStream(fileToProcess);
			logger.trace("Downloading s3 object to: " + fileToProcess.getAbsolutePath());

			byte[] read_buf = new byte[1024];
			int read_len = 0;
			while ((read_len = s3ObjectInputStream.read(read_buf)) > 0) {
				fileOutputStream.write(read_buf, 0, read_len);
			}
			s3ObjectInputStream.close();
			fileOutputStream.close();
			s3Object.close();
		} catch (AmazonServiceException e) {
			logger.error(e.getErrorMessage());
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		return fileToProcess;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getFileSize(Path file) {
		Long fileSize = 0L;
		try {
			S3Object s3Object = s3.getObject(bucketName, getAwsFileAbsolutePath(file));
			fileSize = s3Object.getObjectMetadata()
					.getContentLength();
			s3Object.close();
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
	public void writeFile(Path source, Path target, Path sequenceFileDir, Path sequenceFileDirWithRevision) {
		try {
			logger.trace("Uploading file to s3 bucket: [" + target.getFileName() + "]");
			s3.putObject(bucketName, getAwsFileAbsolutePath(target), source.toFile());
			logger.trace("File uploaded to s3 bucket: [" + s3.getUrl(bucketName, target.toAbsolutePath().toString()) + "]");
		} catch (AmazonServiceException e) {
			logger.error("Unable to upload file to s3 bucket: " + e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteFile() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void downloadFile() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void downloadFiles() {
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
		InputStream inputStream = null;
		try {
			S3Object s3Object = s3.getObject(bucketName, getAwsFileAbsolutePath(file));
			inputStream = s3Object.getObjectContent();
		} catch (AmazonServiceException e) {
			logger.error(e.getErrorMessage());
		}
		return inputStream;
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
	 * {@inheritDoc}
	 */
	@Override
	public void appendToFile(Path target, SequenceFile file) throws ConcatenateException {
		try (FileChannel out = FileChannel.open(target, StandardOpenOption.CREATE, StandardOpenOption.APPEND,
				StandardOpenOption.WRITE)) {
			try (FileChannel in = new FileInputStream(getTemporaryFile(file.getFile())).getChannel()) {
				for (long p = 0, l = in.size(); p < l; ) {
					p += in.transferTo(p, l - p, out);
				}
			} catch (IOException e) {
				throw new ConcatenateException("Could not open input file for reading", e);
			}

		} catch (IOException e) {
			throw new ConcatenateException("Could not open target file for writing", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFileExtension(List<? extends SequencingObject> toConcatenate) throws ConcatenateException {
		String selectedExtension = null;
		for (SequencingObject object : toConcatenate) {

			for (SequenceFile file : object.getFiles()) {
				String fileName = getFileName(file.getFile());

				Optional<String> currentExtensionOpt = VALID_EXTENSIONS.stream()
						.filter(e -> fileName.endsWith(e))
						.findFirst();

				if (!currentExtensionOpt.isPresent()) {
					throw new ConcatenateException("File extension is not valid " + fileName);
				}

				String currentExtension = currentExtensionOpt.get();

				if (selectedExtension == null) {
					selectedExtension = currentExtensionOpt.get();
				} else if (selectedExtension != currentExtensionOpt.get()) {
					throw new ConcatenateException(
							"Extensions of files to concatenate do not match " + currentExtension + " vs "
									+ selectedExtension);
				}
			}
		}

		return selectedExtension;
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
}
