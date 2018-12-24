package ca.corefacility.bioinformatics.irida.service.export;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 * A class with a main method for manual tests of connection speed to NCBI.
 */
public class FTPClientNCBIManualTestConnectionSpeed {

	private static final String NCBI_FTP_ADDRESS = "ftp-private.ncbi.nlm.nih.gov";
	private static final int NCBI_FTP_PORT = 21;

	/**
	 * NCBI FTP connectiond details Please create an account with NCBI and go to
	 * <https://submit.ncbi.nlm.nih.gov/subs/sra/>, selecting the option to enable
	 * FTP upload. Then fill out your details here.
	 */
	private static final String NCBI_FTP_USERNAME = "[USERNAME]";
	private static final String NCBI_FTP_PASSWORD = "[PASSWORD]";
	private static final String ACCOUNT_FOLDER = "[ACCOUNT_FOLDER]";

	/**
	 * Please fill out the file to upload below.
	 */
	private static final Path file = Paths.get("/tmp/100MB");

	private static final String TARGET_SUBFOLDER = "test";
	private static final String UPLOAD_FOLDER = ACCOUNT_FOLDER + "/" + TARGET_SUBFOLDER;

	public static void main(String[] args) throws SocketException, IOException {
		FTPClient ftp = new FTPClient();

		ftp.connect(NCBI_FTP_ADDRESS, NCBI_FTP_PORT);
		ftp.login(NCBI_FTP_USERNAME, NCBI_FTP_PASSWORD);
		ftp.makeDirectory(UPLOAD_FOLDER);
		ftp.changeWorkingDirectory(UPLOAD_FOLDER);

		ftp.setFileType(FTP.BINARY_FILE_TYPE);				

		String fileName = file.toFile().getName();

		if (!file.toFile().exists()) {
			throw new IllegalArgumentException("Error: file [" + file + "] does not exist");
		}

		InputStream stream = Files.newInputStream(file);
		System.out.println("Uploading [" + file + "] to " + NCBI_FTP_ADDRESS + "/" + UPLOAD_FOLDER + "/" + fileName);

		long start = System.currentTimeMillis();
		boolean success = ftp.storeFile(fileName, stream);
		long end = System.currentTimeMillis();

		if (!success) {
			System.out.println("Error: could not upload file: " + ftp.getReplyString());
		} else {
			ftp.deleteFile(fileName);

			float sizeMB = file.toFile().length() / 1024f / 1024f;

			float timeSeconds = (end - start) / 1000f;
			float rateMBS = sizeMB / timeSeconds;

			System.out.println(
					String.format("Uploaded file [%s] %.2f MB in %.2f seconds (%.2f MB/s), using bufferSize [%d]", file,
							sizeMB, timeSeconds, rateMBS, ftp.getBufferSize()));
		}
	}
}
