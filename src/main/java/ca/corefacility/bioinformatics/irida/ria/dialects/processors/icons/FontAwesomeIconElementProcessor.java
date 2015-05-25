package ca.corefacility.bioinformatics.irida.ria.dialects.processors.icons;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.element.AbstractMarkupSubstitutionElementProcessor;

import com.google.common.collect.ImmutableList;

/**
 * Element processor to handle icons in the IRIDA UI.
 */
public class FontAwesomeIconElementProcessor extends AbstractMarkupSubstitutionElementProcessor {
	private static final Logger logger = LoggerFactory.getLogger(FontAwesomeIconElementProcessor.class);

	// This is required!  This determines the type of icon to add (see list of attributes below).
	private static final String TYPE_ATTRIBUTE = "type";
	// Optional.  This will add a class to give the icon a fixed width.  Use when creating lists which have icons.
	private static final String FIXED_ATTRIBUTE = "fixed";
	// Optional. THis will add a class to give the icon a larger size.
	private static final String SIZE_ATTRIBUTE = "size";

	//	DOM Element to create the icon in.
	private static final String ICON_CONTAINER = "span";

	public FontAwesomeIconElementProcessor() {
		super("icon");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Node> getMarkupSubstitutes(Arguments arguments, Element element) {
		// Create the DOM element for the icons
		final Element container = new Element(ICON_CONTAINER);
		/*
		Get the type of icon needed.  If the icon is not available you should not create the DOM element.
		 */
		final String type = element.getAttributeValue(TYPE_ATTRIBUTE);
		element.removeAttribute(TYPE_ATTRIBUTE);
		try {
			FontAwesome fontAwesome = new FontAwesome(type);

			if (element.hasAttribute(FIXED_ATTRIBUTE)) {
				fontAwesome.isFixedWidth();
				element.removeAttribute(FIXED_ATTRIBUTE);
			}

			if (element.hasAttribute(SIZE_ATTRIBUTE)) {
				fontAwesome.setIconSize(element.getAttributeValue(SIZE_ATTRIBUTE));
				element.removeAttribute(SIZE_ATTRIBUTE);
			}

			// Check to see if there are any classes already on the element
			String classes = "";
			if (element.hasAttribute("class")) {
				classes = element.getAttributeValue("classes");
			}
			classes += fontAwesome.getClassString();
			container.setAttribute("class", classes);

			// Copy over and other remaining attributes
			Map<String, Attribute> attrmap = element.getAttributeMap();
			for (String attr : attrmap.keySet()) {
				container.setAttribute(attr, attrmap.get(attr).getValue());
			}

			return ImmutableList.of(container);
		}
		catch (IconNotFoundException e){
			logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 */
	@Override
	public int getPrecedence() {
		return 1000;
	}
}
