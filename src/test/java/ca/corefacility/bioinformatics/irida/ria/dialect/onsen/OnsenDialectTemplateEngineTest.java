package ca.corefacility.bioinformatics.irida.ria.dialect.onsen;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.MessageResolution;
import org.thymeleaf.templateresolver.FileTemplateResolver;

/**
 * Overall test for the OnsenDialect using a real html page.
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class OnsenDialectTemplateEngineTest {

	@Test
	public void test() {
		FileTemplateResolver templateResolver = new FileTemplateResolver();
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);

		AbstractMessageResolver messageResolver = new DummyMessageResolverExtension();
		templateEngine.setMessageResolver(messageResolver);
		templateEngine.addDialect(new OnsenAttributeDialect());

		// Cannot use @{...} syntax without an IWebContext interface, therefore
		// facking it
		// wit ${baseUrl ...}
		Map<String, String> model = new HashMap<>();
		model.put("baseUrl", "");
		String process = templateEngine.process("src/test/resources/test.html", new Context(Locale.CANADA, model));
		Document document = Jsoup.parse(process);
		Element element = document.getElementsByTag("body").get(0);

		assertEquals(element.attr("page"), "/dashboard/view/main");
	}

	private final class DummyMessageResolverExtension extends AbstractMessageResolver {
		@Override
		public MessageResolution resolveMessage(Arguments arguments, String key, Object[] messageParameters) {
			return key.equals("page") ? new MessageResolution("/dashboard/view/main") : null;
		}
	}
}
