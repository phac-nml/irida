package ca.corefacility.bioinformatics.irida.util.unit;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.StaticMessageSource;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.util.IridaPluginMessageSource;

public class IridaPluginMessageSourceTest {

	private IridaPluginMessageSource iridaMessageSourceSingle;
	private IridaPluginMessageSource iridaMessageSourceMultiple;

	@Before
	public void setup() {
		StaticMessageSource testSource1 = new StaticMessageSource();
		testSource1.addMessage("key1", Locale.ENGLISH, "message1");

		StaticMessageSource testSource2 = new StaticMessageSource();
		testSource2.addMessage("key2", Locale.ENGLISH, "message2");

		iridaMessageSourceSingle = new IridaPluginMessageSource(Lists.newArrayList(testSource1));

		iridaMessageSourceMultiple = new IridaPluginMessageSource(Lists.newArrayList(testSource1, testSource2));
	}

	@Test
	public void testGetMessageSinglePlugin() {
		String message = iridaMessageSourceSingle.getMessage("key1", null, Locale.ENGLISH);
		assertEquals("Invalid message", message, "message1");
	}

	@Test
	public void testGetMessageSinglePluginWithDefault() {
		String message = iridaMessageSourceSingle.getMessage("key2", null, "default", Locale.ENGLISH);
		assertEquals("Invalid message", message, "default");
	}

	@Test(expected = NoSuchMessageException.class)
	public void testGetMessageSinglePluginNoMessage() {
		iridaMessageSourceSingle.getMessage("key2", null, Locale.ENGLISH);
	}

	@Test
	public void testGetMessageMultiplePlugin() {
		String message1 = iridaMessageSourceMultiple.getMessage("key1", null, Locale.ENGLISH);
		String message2 = iridaMessageSourceMultiple.getMessage("key2", null, Locale.ENGLISH);
		assertEquals("Invalid message", message1, "message1");
		assertEquals("Invalid message", message2, "message2");
	}

	@Test
	public void testGetMessageMultiplePluginWithDefault() {
		String message1 = iridaMessageSourceMultiple.getMessage("key1", null, Locale.ENGLISH);
		String message2 = iridaMessageSourceMultiple.getMessage("key3", null, "default", Locale.ENGLISH);
		assertEquals("Invalid message", message1, "message1");
		assertEquals("Invalid message", message2, "default");
	}

	@Test(expected = NoSuchMessageException.class)
	public void testGetMessageMultiplePluginNoMessage() {
		iridaMessageSourceMultiple.getMessage("key3", null, Locale.ENGLISH);
	}

	@Test(expected = NoSuchMessageException.class)
	public void testGetMessageMultiplePluginNoMessageLocale() {
		iridaMessageSourceMultiple.getMessage("key1", null, Locale.FRENCH);
	}

	@Test
	public void testGetMessageSinglePluginWithParent() {
		StaticMessageSource parentSource = new StaticMessageSource();
		parentSource.addMessage("key2", Locale.ENGLISH, "message2");
		iridaMessageSourceSingle.setParentMessageSource(parentSource);

		String message = iridaMessageSourceSingle.getMessage("key2", null, Locale.ENGLISH);
		assertEquals("Invalid message", message, "message2");
	}
}
