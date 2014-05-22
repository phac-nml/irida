package ca.corefacility.bioinformatics.irida.ria.dialect.onsen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class OnsenAttributeDialectTest {
	@Test
	public void testPrefix() {
		OnsenAttributeDialect dialect = new OnsenAttributeDialect();
		assertEquals(dialect.getPrefix(), "ons");
	}

	@Test
	public void testProcessors() {
		OnsenAttributeDialect dialect = new OnsenAttributeDialect();
		assertNotNull("processors", dialect.getProcessors());
		assertEquals(dialect.getProcessors().size(), 1);
	}
}
