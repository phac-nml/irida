package ca.corefacility.bioinformatics.irida.ria.unit.utilities.converters;

import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

/**
 * Unit tests for File size conversions
 *
 */
public class FileSizeConverterTest {
	private Converter<Long, String> converter;

	@BeforeEach
	public void setUp() {
		converter = new FileSizeConverter();
	}

	@Test
	public void testConversions() {
		Long five_gb_in_bytes = 5368709120L;
		Long ten_five_gb_in_bytes = 11274289152L;
		Long thirtyfive_mb_in_bytes = 36700160L;
		Long ten_kb_in_bytes = 10240L;
		assertEquals("5.00 GB", converter.convert(five_gb_in_bytes), "Convert large number into GB");
		assertEquals("10.50 GB", converter.convert(ten_five_gb_in_bytes), "Should show partial for GB");
		assertEquals("35 MB", converter.convert(thirtyfive_mb_in_bytes), "Convert medium number into MB");
		assertEquals("10 KB", converter.convert(ten_kb_in_bytes), "Convert small number into KB");
	}
}
