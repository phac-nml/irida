package ca.corefacility.bioinformatics.irida.ria.dialects.processors.icons;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.element.AbstractMarkupSubstitutionElementProcessor;

import com.google.common.collect.ImmutableList;

/**
 * Element processor to handle icons in the IRIDA UI.
 */
public class IridaIconElementProcessor extends AbstractMarkupSubstitutionElementProcessor {
	private static final Logger logger = LoggerFactory.getLogger(IridaIconElementProcessor.class);

	// This is required!  This determines the type of icon to add (see list of attributes below).
	private static final String TYPE_ATTRIBUTE = "type";
	// Optional.  This will add a class to give the icon a fixed width.  Use when creating lists which have icons.
	private static final String FIXED_ATTRIBUTE = "fixed";

	//	DOM Element to create the icon in.
	private static final String ICON_CONTAINER = "span";

	public IridaIconElementProcessor() {
		super("icon");
	}

	@Override protected List<Node> getMarkupSubstitutes(Arguments arguments, Element element) {
		// Create the DOM element for the icons
		final Element container = new Element(ICON_CONTAINER);
		/*
		Get the type of icon needed.  If the icon is not available you should not create the DOM element.
		 */
		final String type = element.getAttributeValue(TYPE_ATTRIBUTE);
		try {
			FontAwesome fontAwesome = new FontAwesome(type);

			if (element.hasAttribute(FIXED_ATTRIBUTE)) {
				fontAwesome.isFixedWidth();
			}

			container.setAttribute("class", fontAwesome.getClassList());
			return ImmutableList.of(container);
		}
		catch (IconNotFoundException e){
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override public int getPrecedence() {
		return 1000;
	}
}
