package ca.corefacility.bioinformatics.irida.ria.web.samples;

import org.springframework.web.multipart.MultipartFile;
import com.sksamuel.diffpatch.DiffMatchPatch;
import com.sksamuel.diffpatch.DiffMatchPatch.Diff;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
* 	Utility class for pairing up sequence files with
*	common prefixes and expected characters for forward
*	and reverse sequence files. 
*/

public class SamplePairer {

	private static DiffMatchPatch diff = new DiffMatchPatch();
	
	private static String[] forwardMatches = {"1", "f", "F"};
	private static String[] reverseMatches = {"2", "r", "R"};

	/**
	 * Get {@link Path}s to all paired up sequence files
	 *
	 * @param files
	 *            List of {@link MultipartFile}s uploaded
	 * @return List of {@link Path}s to paired sequence files.
	 */
	public static Map<String, List<Path>> getPairedFiles(List<MultipartFile> files) throws IOException {

		Map<String, List<Path>> pairedFilePaths = new HashMap<>();
		MultipartFile file1, file2;

		Collections.sort(files, (a, b) -> a.getOriginalFilename().compareTo(b.getOriginalFilename()));
		Iterator<MultipartFile> iterator = files.iterator();

		//match up paired files
		while (iterator.hasNext()) {
			file1 = iterator.next(); //TODO: fix up the sequence for skipping when a match is found
			if (iterator.hasNext()) {
				file2 = iterator.next();

				List<Diff> diffs = diff.diff_main(file1.getOriginalFilename(), file2.getOriginalFilename());

				//The size of the list will 4 when only 1 character differs between the two strings
				if (diffs.size() == 4) {
					String file1ID = diffs.get(1).text;
					String file2ID = diffs.get(2).text;
					if (Stream.of(forwardMatches).anyMatch(x -> file1ID.contains(x))
							&& Stream.of(reverseMatches).anyMatch(x -> file2ID.contains(x))) {
						Path[] filePathPair = {createSequenceFile(file1), createSequenceFile(file2)};
						pairedFilePaths.put(diffs.get(0).text, Arrays.asList(filePathPair));
						iterator.next(); //skip because there's a match
					}
				}
			}
		}
		return pairedFilePaths;
	}

	/**
	 * Get {@link Path}s to all single sequence files
	 *
	 * @param files
	 *            List of {@link MultipartFile}s uploaded
	 * @return List of {@link Path}s to single sequence files.
	 */
	public static List<Path> getSingleFiles(List<MultipartFile> files) throws IOException {

		List<Path> singleFilePaths = new ArrayList<>();

		return singleFilePaths;
	}

	/**
	 * Transfer sequence files into temporary directories when uploaded
	 *
	 * @param file
	 *            Single {@link MultipartFile}s uploaded to a sample
	 * @return {@link Path} to the uploaded sequence file.
	 */
	private static Path createSequenceFile(MultipartFile file) throws IOException {
		Path temp = Files.createTempDirectory(null);
		Path target = temp.resolve(file.getOriginalFilename());
		file.transferTo(target.toFile());
		return target;
	}


}

/*
TODO: Stuff to think about:
	should we sort the list of files, or just iterate through all of it?
	which would take longer?
 */

