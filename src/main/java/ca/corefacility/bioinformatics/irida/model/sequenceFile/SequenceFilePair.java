package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Stream;

/**
 * A pair of sequence files in forward/reverse orientation.
 */
@Entity
@Table(name = "sequence_file_pair")
@EntityListeners(AuditingEntityListener.class)
@Audited
public class SequenceFilePair extends SequencingObject {

	public static String[] forwardMatches = {"1", "f", "F"};
	public static String[] reverseMatches = {"2", "r", "R"};

	/**
	 * This must be a list due to a hibernate bug. See Gitlab issue 376
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Size(min = 2, max = 2)
	@CollectionTable(name = "sequence_file_pair_files", joinColumns = @JoinColumn(name = "pair_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"files_id" }, name = "UK_SEQUENCE_FILE_PAIR"))
	@Fetch(FetchMode.SELECT)
	private List<SequenceFile> files;

	public SequenceFilePair() {
		super();
		files = new ArrayList<>();
	}

	public SequenceFilePair(SequenceFile file1, SequenceFile file2) {
		this();
		files.add(file1);
		files.add(file2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFiles());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SequenceFilePair) {
			SequenceFilePair pair = (SequenceFilePair) obj;

			return super.equals(obj) && Objects.equals(getFiles(), pair.getFiles());
		}

		return false;
	}

	/**
	 * Gets the forward {@link SequenceFile} from the pair.
	 *
	 * @return The forward {@link SequenceFile} from the pair.
	 */
	public SequenceFile getForwardSequenceFile() {
		SequenceFile[] pair = getFiles().toArray(new SequenceFile[getFiles().size()]);
		String[] filenames = { pair[0].getFile().getFileName().toString(), pair[1].getFile().getFileName().toString() };

		int index = StringUtils.indexOfDifference(filenames[0], filenames[1]);

		if (Stream.of(forwardMatches).anyMatch(x -> String.valueOf(filenames[0].charAt(index)).equals(x))) {
			return pair[0];
		} else if (Stream.of(forwardMatches).anyMatch(x -> String.valueOf(filenames[1].charAt(index)).equals(x))) {
			return pair[1];
		} else {
			throw new NoSuchElementException();
		}
	}

	/**
	 * Gets the reverse {@link SequenceFile} from the pair.
	 *
	 * @return The reverse {@link SequenceFile} from the pair.
	 */
	public SequenceFile getReverseSequenceFile() {
		SequenceFile[] pair = getFiles().toArray(new SequenceFile[getFiles().size()]);

		String[] filenames = { pair[0].getFile().getFileName().toString(), pair[1].getFile().getFileName().toString() };

		int index = StringUtils.indexOfDifference(filenames[0], filenames[1]);

		if (Stream.of(reverseMatches).anyMatch(x -> String.valueOf(filenames[0].charAt(index)).equals(x))) {
			return (SequenceFile) pair[0];
		} else if (Stream.of(reverseMatches).anyMatch(x -> String.valueOf(filenames[1].charAt(index)).equals(x))) {
			return (SequenceFile) pair[1];
		} else {
			throw new NoSuchElementException();
		}
	}

	@JsonIgnore
	@Override
	public void setModifiedDate(Date modifiedDate) {
		throw new UnsupportedOperationException("Cannot update a sequence file pair");
	}

	@Override
	public String getLabel() {
		return toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Iterator<SequenceFile> iterator = files.iterator();
		builder.append(iterator.next().getLabel()).append(", ").append(iterator.next().getLabel());
		return builder.toString();
	}

	@Override
	public Set<SequenceFile> getFiles() {
		// returning an ImmutableSet to ensure it isn't changed
		return ImmutableSet.copyOf(files);
	}

	/**
	 * Set the {@link SequenceFile}s in this pair. Note it must contain 2 files.
	 * 
	 * @param files
	 *            The set of {@link SequenceFile}s
	 */
	public void setFiles(Set<SequenceFile> files) {
		if (files.size() != 2) {
			throw new IllegalArgumentException("SequenceFilePair must have 2 files");
		}

		this.files = Lists.newArrayList(files);
	}
}
