package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.dialects.login;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

import com.google.common.base.Strings;

public class LoginAttributeTagProcessor extends AbstractAttributeTagProcessor {
	private static final String ATTR_NAME = "page";
	private static final int PRECEDENCE = 1000;

	public LoginAttributeTagProcessor(final String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE, true);
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag iProcessableElementTag,
			AttributeName attributeName, String attributeValue,
			IElementTagStructureHandler iElementTagStructureHandler) {
		final IEngineConfiguration configuration = context.getConfiguration();

		/*
		 * Obtain the Thymeleaf Standard Expression parser
		 */
		final IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);

		/*
		 * Parse the attribute value as a Thymeleaf Standard Expression
		 */
		final IStandardExpression expression = parser.parseExpression(context, attributeValue);

		// If this is defined, then it should be the path to an external html file.
		String page = (String) expression.execute(context);

		if (Strings.isNullOrEmpty(page)) {
			return;
		}
		Path path = Paths.get(page);
		if (!Files.exists(path)) {
			return;
		}

		try {
			Stream<String> lines = Files.lines(path);
			String content = lines.collect(Collectors.joining("\n"));
			iElementTagStructureHandler.setBody(content, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		final IModelFactory modelFactory = context.getModelFactory();
		final IModel model = modelFactory.createModel();

	}
}
