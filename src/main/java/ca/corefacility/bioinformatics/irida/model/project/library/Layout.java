package ca.corefacility.bioinformatics.irida.model.project.library;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Describes the layout part of a {@link LibraryDescription} single or paired
 * end reads, and read lengths.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Embeddable
public class Layout {
	@Column(name = "read_lengths", nullable = false)
	private final Integer readLengths;

	@Enumerated(EnumType.STRING)
	@Column(name = "layout_type", nullable = false)
	private final LayoutType layoutType;

	public Layout(final Integer readLengths, final LayoutType layoutType) {
		this.readLengths = readLengths;
		this.layoutType = layoutType;
	}

	public Integer getReadLengths() {
		return readLengths;
	}

	public static enum LayoutType {
		SINGLE_END, PAIRED_END
	}
}
