package ca.corefacility.bioinformatics.irida.model.irida;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import org.apache.commons.lang3.StringUtils;

/**
 * Interface describing a pair object storing forward and reverse
 * {@link SequenceFile}s
 */
public interface IridaSequenceFilePair {

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
		String[] filenames = {pair[0].getFile().getFileName().toString(), pair[1].getFile().getFileName().toString()};

		int index = StringUtils.indexOfDifference(filenames[0], filenames[1]);

		if (Stream.of(forwardMatches).anyMatch( x -> String.valueOf(filenames[0].charAt(index)).equals(x) )) {
			return pair[0];
		} else if (Stream.of(forwardMatches).anyMatch( x -> String.valueOf(filenames[1].charAt(index)).equals(x) )) {
			return pair[1];
		} else {
			throw new NoSuchElementException();
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

		String[] filenames = {pair[0].getFile().getFileName().toString(), pair[1].getFile().getFileName().toString()};

		int index = StringUtils.indexOfDifference(filenames[0], filenames[1]);

		if (Stream.of(reverseMatches).anyMatch( x -> String.valueOf(filenames[0].charAt(index)).equals(x) )) {
			return pair[0];
		} else if (Stream.of(reverseMatches).anyMatch( x -> String.valueOf(filenames[1].charAt(index)).equals(x) )) {
			return pair[1];
		} else {
			throw new NoSuchElementException();
		}
	}

	/**
	 * Get a Set of the {@link SequenceFile}s for this pair
	 * 
	 * @return Set of {@link SequenceFile}s
	 */
	public Set<? extends IridaSequenceFile> getFiles();
}
