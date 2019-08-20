package ca.corefacility.bioinformatics.irida.ria.web.utilities;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Utilities class to help with date conversions.
 */
public class DateUtilities {


	/**
	 * Get the milliseconds between two {@link Date}s
	 *
	 * @param start {@link Date}
	 * @param end   {@link Date}
	 * @return {@link Long} milliseconds
	 */
	public static Long getDurationInMilliseconds(Date start, Date end) {
		Instant startInstant = start.toInstant();
		Instant endInstant = end.toInstant();
		Duration duration = Duration.between(startInstant, endInstant)
				.abs();
		return duration.toMillis();
	}
}
