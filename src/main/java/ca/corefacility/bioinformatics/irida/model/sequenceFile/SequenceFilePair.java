package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import com.sksamuel.diffpatch.DiffMatchPatch;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFilePair;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;

@Entity
@Table(name = "sequence_file_pair")
@EntityListeners(AuditingEntityListener.class)
@Audited
public class SequenceFilePair extends SequencingObject implements IridaSequenceFilePair {

	private static final Logger logger = LoggerFactory.getLogger(SequenceFilePair.class);

	public static DiffMatchPatch diff = IridaSequenceFilePair.diff;
	public static String[] forwardMatches = IridaSequenceFilePair.forwardMatches;
	public static String[] reverseMatches = IridaSequenceFilePair.reverseMatches;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Size(min = 2, max = 2)
	@CollectionTable(name = "sequence_file_pair_files", joinColumns = @JoinColumn(name = "pair_id"), uniqueConstraints = @UniqueConstraint(columnNames = { "files_id" }, name = "UK_SEQUENCE_FILE_PAIR"))
	private Set<SequenceFile> files;

	public SequenceFilePair() {
		super();
		files = new HashSet<>();
	}

	public SequenceFilePair(SequenceFile file1, SequenceFile file2) {
		this();
		files.add(file1);
		files.add(file2);
	}

	/**
	 * Gets the forward {@link SequenceFile} from the pair.
	 * 
	 * @return The forward {@link SequenceFile} from the pair.
	 */
	public SequenceFile getForwardSequenceFile() {
		IridaSequenceFile[] pair = getFiles().toArray(new IridaSequenceFile[getFiles().size()]);
		List<DiffMatchPatch.Diff> diffs = diff.diff_main(pair[0].getFile().toString(), pair[1].getFile().toString());
		if (Stream.of(forwardMatches).anyMatch(x -> diffs.get(diffs.size()-3).text.equals(x))) {
			return (SequenceFile) pair[0];
		} else {
			return (SequenceFile) pair[1];
		}
	}

	/**
	 * Gets the reverse {@link SequenceFile} from the pair.
	 * 
	 * @return The reverse {@link SequenceFile} from the pair.
	 */
	public SequenceFile getReverseSequenceFile() {
		IridaSequenceFile[] pair = getFiles().toArray(new IridaSequenceFile[getFiles().size()]);
		List<DiffMatchPatch.Diff> diffs = diff.diff_main(pair[0].getFile().toString(), pair[1].getFile().toString());
		if (Stream.of(reverseMatches).anyMatch(x -> diffs.get(diffs.size()-3).text.equals(x))) {
			return (SequenceFile) pair[0];
		} else {
			return (SequenceFile) pair[1];
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

		this.files = files;
	}
}
