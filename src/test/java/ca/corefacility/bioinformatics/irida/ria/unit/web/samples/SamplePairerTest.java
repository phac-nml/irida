package ca.corefacility.bioinformatics.irida.ria.unit.web.samples;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.ria.web.samples.SamplePairer;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * Tests for SamplePairer
 */
public class SamplePairerTest {

	public static final String[] MULTIPARTFILE_PATHS = {
			"src/test/resources/files/test_file_A.fastq",
			"src/test/resources/files/test_file_B.fastq",
			"src/test/resources/files/pairs/pair_test_R1_001.fastq",
			"src/test/resources/files/pairs/pair_test_R2_001.fastq",
			"src/test/resources/files/pairs/pair_test_1_001.fastq",
			"src/test/resources/files/pairs/pair_test_2_001.fastq",
			"src/test/resources/files/pairs/pair_test_L001_F.fastq",
			"src/test/resources/files/pairs/pair_test_L001_R.fastq" };

	/**
	 * Tests getting single files from a list of sequence files
	 */
	@Test
	public void testGetSingleFiles() throws IOException {
		List<MultipartFile> allFiles = createMultipartFileList(MULTIPARTFILE_PATHS);
		SamplePairer samplePairer = new SamplePairer(allFiles);
		assertEquals(2, samplePairer.getSingleFiles(allFiles).size(),
				"Single files not correctly organized/separated from paired files");
	}

	/**
	 * Tests getting paired files from a list of sequence files
	 */
	@Test
	public void testGetPairedFiles() throws IOException {
		List<MultipartFile> allFiles = createMultipartFileList(MULTIPARTFILE_PATHS);
		SamplePairer samplePairer = new SamplePairer(allFiles);

		Set<String> keys = samplePairer.getPairedFiles(allFiles).keySet();

		assertEquals(3, keys.size(), "Paired files not correctly organized into pairs by prefix");

		for (String s : keys) {
			assertEquals(2, samplePairer.getPairedFiles(allFiles).get(s).size(),
					"Pairs don't contain the right number of sequence files");
		}
	}

	/**
	 * Create a list of {@link MultipartFile}s
	 *
	 * @param list A list of paths to files.
	 * @return List of {@link MultipartFile}s
	 * @throws IOException
	 */
	private List<MultipartFile> createMultipartFileList(String[] list) throws IOException {
		List<MultipartFile> fileList = new ArrayList<>();
		for (String pathName : list) {
			Path path = Paths.get(pathName);
			byte[] bytes = Files.readAllBytes(path);
			fileList.add(new MockMultipartFile(path.getFileName().toString(), path.getFileName().toString(),
					"octet-stream", bytes));
		}
		return fileList;
	}

	@Test
	public void getPairedNameReversed() throws IOException {
		String[] reverse_files = {
				"src/test/resources/files/pairs/pair_test_R2_001.fastq",
				"src/test/resources/files/pairs/pair_test_R1_001.fastq" };

		List<MultipartFile> allFiles = createMultipartFileList(reverse_files);
		SamplePairer samplePairer = new SamplePairer(allFiles);

		Set<String> keys = samplePairer.getPairedFiles(allFiles).keySet();

		assertEquals(1, keys.size(), "Paired files not correctly organized into pairs by prefix");

		for (String s : keys) {
			assertEquals(2, samplePairer.getPairedFiles(allFiles).get(s).size(),
					"Pairs don't contain the right number of sequence files");
		}
	}
}
