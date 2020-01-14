package ca.corefacility.bioinformatics.irida.ria.unit.utilities.converters;

import ca.corefacility.bioinformatics.irida.ria.utilities.converters.StringToDateConverter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
		isoFormat.setTimeZone(TimeZone.getDefault());

		String unix_epoch_string = "1970-01-01";
		Date unix_epoch_date = null;
		try {
			unix_epoch_date = isoFormat.parse("1970-01-01");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String y2k_string = "2000-01-01";
		Date y2k_date = null;
		try {
			y2k_date = isoFormat.parse("2000-01-01");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		assertEquals("The beginning of the universe", unix_epoch_date, converter.convert(unix_epoch_string));
		assertEquals("New millenium", y2k_date, converter.convert(y2k_string));
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConversionsFail() {
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
		isoFormat.setTimeZone(TimeZone.getDefault());

		String empty_string = "";

		Date converted_empty_string = converter.convert(empty_string);
	}
}
