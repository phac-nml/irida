package ca.corefacility.bioinformatics.irida.model.run.library;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

/**
 * The strategy part of a {@link LibraryDescription}, describing insert size
 * statistics and the protocol used.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Embeddable
public class Strategy {
	@Column(name = "mode_insert_size", nullable = false)
	private final Integer modeInsertSize;
	@Column(name = "min_insert_size", nullable = false)
	private final Integer minInsertSize;
	@Column(name = "max_insert_size", nullable = false)
	private final Integer maxInsertSize;
	@Lob
	@Column(name = "protocol", nullable = false)
	private final String protocol;

	/**
	 * For Hibernate.
	 */
	@SuppressWarnings("unused")
	private Strategy() {
		this.modeInsertSize = null;
		this.minInsertSize = null;
		this.maxInsertSize = null;
		this.protocol = null;
	}

	public Strategy(final Integer modeInsertSize, final Integer minInsertSize, final Integer maxInsertSize,
			final String protocol) {
		this.minInsertSize = minInsertSize;
		this.modeInsertSize = modeInsertSize;
		this.maxInsertSize = maxInsertSize;
		this.protocol = protocol;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof Strategy) {
			final Strategy s = (Strategy) o;
			return Objects.equals(modeInsertSize, s.modeInsertSize) && Objects.equals(minInsertSize, s.minInsertSize)
					&& Objects.equals(maxInsertSize, s.maxInsertSize) && Objects.equals(protocol, s.protocol);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(modeInsertSize, minInsertSize, maxInsertSize, protocol);
	}

	public Integer getModeInsertSize() {
		return modeInsertSize;
	}

	public Integer getMinInsertSize() {
		return minInsertSize;
	}

	public Integer getMaxInsertSize() {
		return maxInsertSize;
	}

	public String getProtocol() {
		return protocol;
	}
}
