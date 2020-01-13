package ca.corefacility.bioinformatics.irida.ria.unit.utilities.converters;

import ca.corefacility.bioinformatics.irida.ria.utilities.converters.StringToDateConverter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for File size conversions
 *
 */
public class StringToDateConverterTest {
	private Converter<String, Date> converter;

	@Before
	public void setUp() {
		converter = new StringToDateConverter();
	}

	@Test
	public void testConversions() {
		String unix_epoch_string = "1970-01-01";
		Date unix_epoch_date = new Date(28800000L);
		String y2k_string = "2000-01-01";
		Date y2k_date = new Date(946713600000L);

		assertEquals("The beginning of the universe", unix_epoch_date, converter.convert(unix_epoch_string));
		assertEquals("New millenium", y2k_date, converter.convert(y2k_string));
	}
}
