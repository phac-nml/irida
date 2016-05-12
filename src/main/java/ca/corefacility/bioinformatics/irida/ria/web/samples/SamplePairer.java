package ca.corefacility.bioinformatics.irida.ria.web.samples;

import org.springframework.web.multipart.MultipartFile;
import com.sksamuel.diffpatch.DiffMatchPatch;
import com.sksamuel.diffpatch.DiffMatchPatch.Diff;

import java.io.IOException;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFilePair;

/**
* 	Utility class for pairing up sequence files with
*	common prefixes and expected characters for forward
*	and reverse sequence files. 
*/

public class SamplePairer {

	private static DiffMatchPatch diff = new DiffMatchPatch();
	
	private static String[] forwardMatches = IridaSequenceFilePair.forwardMatches;
	private static String[] reverseMatches = IridaSequenceFilePair.reverseMatches;

	/**
	 * Organize files according to whether they should be paired up
	 *
	 * @param files
	 *            List of {@link MultipartFile}s uploaded
	 * @return Map of {@link Path}s to uploaded sequence files,
	 * 			where the key is the common prefix of two paired files,
	 * 			or the full file name of a single sequence file
	 */
	private static Map<String, List<MultipartFile>> organizeFiles(List<MultipartFile> files) throws IOException {

		Map<String, List<MultipartFile>> filePaths = new HashMap<>();
		MultipartFile file1, file2;

		Collections.sort(files, (a, b) -> a.getOriginalFilename().compareTo(b.getOriginalFilename()));
		Iterator<MultipartFile> iter = files.iterator();

		if (iter.hasNext()) {
			file1 = iter.next();

			//match up paired files
			if (iter.hasNext()) {
				while (iter.hasNext()) {
					file2 = iter.next();

					Boolean isPair = true;
					List<Diff> diffs = diff.diff_main(file1.getOriginalFilename(), file2.getOriginalFilename());

					//The size of the list is 4 when only 1 character differs between the two strings
					//if the two files should be paired, this would be the list of diffs between the filenames:
					//		diffs[0] = common prefix
					//		diffs[1] = unique character 1 (e.g. "1" or "f")
					//		diffs[2] = unique character 2 (e.g. "2" or "r")
					//		diffs[3] = common suffix (e.g. ".fastq")
					if (diffs.size() == 4) {
						String file1ID = diffs.get(1).text;
						String file2ID = diffs.get(2).text;
						if (Stream.of(forwardMatches).anyMatch(x -> file1ID.contains(x))
								&& Stream.of(reverseMatches).anyMatch(x -> file2ID.contains(x))) {
							MultipartFile[] filePathPair = {file1, file2};
							filePaths.put(diffs.get(0).text, Arrays.asList(filePathPair));
							if (iter.hasNext()) {
								file1 = iter.next(); //skip next because there's a match
							} else {
								file1 = null;
								break;
							}
						} else {
							isPair = false;
						}
					} else {
						isPair = false;
					}

					if (!isPair) {
						MultipartFile[] filePath = {file1};
						filePaths.put(file1.getOriginalFilename(), Arrays.asList(filePath));
						file1 = file2;
					}
				
				}
			}
			if (!iter.hasNext() && file1 != null) {
				MultipartFile[] filePath = {file1};
				filePaths.put(file1.getOriginalFilename(), Arrays.asList(filePath));
			}
		}
		return filePaths;
	}

	/**
	 * Get {@link Path}s to all paired sequence files
	 *
	 * @param files
	 *            List of {@link MultipartFile}s uploaded
	 * @return Map of {@link Path}s to paired sequence files.
	 */
	public static Map<String, List<MultipartFile>> getPairedFiles(List<MultipartFile> files) throws IOException {

		Map<String, List<MultipartFile>> pairedFiles = new HashMap<>();

		Map<String, List<MultipartFile>> allFiles = organizeFiles(files);

		for (String key : allFiles.keySet()) {
			List<MultipartFile> item = allFiles.get(key);
			if (item.size() > 1) {
				pairedFiles.put(key, item);
			}
		}

		return pairedFiles;
	}

	/**
	 * Get {@link Path}s to all single sequence files
	 *
	 * @param files
	 *            List of {@link MultipartFile}s uploaded
	 * @return List of {@link Path}s to single sequence files.
	 */
	public static List<MultipartFile> getSingleFiles(List<MultipartFile> files) throws IOException {

		List<MultipartFile> singleFilePaths = new ArrayList<>();

		Map<String, List<MultipartFile>> allFiles = organizeFiles(files);

		for (String key : allFiles.keySet()) {
			List<MultipartFile> item = allFiles.get(key);
			if (item.size() == 1) {
				singleFilePaths.add(item.get(0));
			}
		}

		return singleFilePaths;
	}
}
