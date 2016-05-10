package ca.corefacility.bioinformatics.irida.model.irida;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.sksamuel.diffpatch.DiffMatchPatch;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Interface describing a pair object storing forward and reverse
 * {@link SequenceFile}s
 */
public interface IridaSequenceFilePair {

	public static DiffMatchPatch diff = new DiffMatchPatch();
	public static String[] forwardMatches = {"1", "f", "F"};
	public static String[] reverseMatches = {"2", "r", "R"};

	/**
	 * Get the pair's identifier
	 * 
	 * @return Long id
	 */
	public Long getId();

	/**
	 * Get the forward oriented {@link SequenceFile}
	 * 
	 * @return Forward {@link SequenceFile}
	 */
	@JsonIgnore
	public default IridaSequenceFile getForwardSequenceFile() {
		IridaSequenceFile[] pair = getFiles().toArray(new IridaSequenceFile[getFiles().size()]);
		List<DiffMatchPatch.Diff> diffs = diff.diff_main(pair[0].getFile().toString(), pair[1].getFile().toString());
		if (Stream.of(forwardMatches).anyMatch(x -> diffs.get(diffs.size()-3).text.equals(x))) {
			return pair[0];
		} else {
			return pair[1];
		}
	}

	/**
	 * Get the reverse oriented {@link SequenceFile}
	 * 
	 * @return reverse {@link SequenceFile}
	 */
	@JsonIgnore
	public default IridaSequenceFile getReverseSequenceFile() {
		IridaSequenceFile[] pair = getFiles().toArray(new IridaSequenceFile[getFiles().size()]);
		List<DiffMatchPatch.Diff> diffs = diff.diff_main(pair[0].getFile().toString(), pair[1].getFile().toString());
		if (Stream.of(reverseMatches).anyMatch(x -> diffs.get(diffs.size()-3).text.equals(x))) {
			return pair[0];
		} else {
			return pair[1];
		}
	}

	/**
	 * Get a Set of the {@link SequenceFile}s for this pair
	 * 
	 * @return Set of {@link SequenceFile}s
	 */
	public Set<? extends IridaSequenceFile> getFiles();
}
