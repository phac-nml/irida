package ca.corefacility.bioinformatics.irida.model.project.library;

import java.util.Objects;

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

	/**
	 * For hibernate.
	 */
	@SuppressWarnings("unused")
	private Layout() {
		this.readLengths = null;
		this.layoutType = null;
	}

	public Layout(final Integer readLengths, final LayoutType layoutType) {
		this.readLengths = readLengths;
		this.layoutType = layoutType;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof Layout) {
			final Layout l = (Layout) o;
			return Objects.equals(readLengths, l.readLengths) && Objects.equals(layoutType, l.layoutType);
		}

		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(readLengths, layoutType);
	}

	public LayoutType getLayoutType() {
		return layoutType;
	}

	public Integer getReadLengths() {
		return readLengths;
	}

	public static enum LayoutType {
		SINGLE_END, PAIRED_END
	}
}
