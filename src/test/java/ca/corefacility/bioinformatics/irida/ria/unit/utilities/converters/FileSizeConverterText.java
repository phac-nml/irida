package ca.corefacility.bioinformatics.irida.ria.unit.utilities.converters;

import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for File size conversions
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class FileSizeConverterText {
	private Converter<Long, String> converter;

	@Before
	public void setUp() {
		converter = new FileSizeConverter();
	}

	@Test
	public void testConversions() {
		Long five_gb_in_bytes = 5368709120L;
		Long ten_five_gb_in_bytes = 11274289152L;
		Long thirtyfive_mb_in_bytes = 36700160L;
		Long ten_kb_in_bytes = 10240L;
		assertEquals("Convert large number into GB", "5.00 GB", converter.convert(five_gb_in_bytes));
		assertEquals("Should show partial for GB", "10.50 GB", converter.convert(ten_five_gb_in_bytes));
		assertEquals("Convert medium number into MB", "35 MB", converter.convert(thirtyfive_mb_in_bytes));
		assertEquals("Convert small number into KB", "10 KB", converter.convert(ten_kb_in_bytes));
	}
}
