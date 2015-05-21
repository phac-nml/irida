package ca.corefacility.bioinformatics.irida.ria.dialects.processors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.element.AbstractMarkupSubstitutionElementProcessor;

import com.google.common.collect.ImmutableList;

/**
 * Element processor to handle icons in the IRIDA UI.
 */
public class IridaIconElementProcessor extends AbstractMarkupSubstitutionElementProcessor {
	// ATTRIBUTES
	// =================================================================================================================

	// This is required!  This determines the type of icon to add (see list of attributes below).
	private static final String TYPE_ATTRIBUTE = "type";
	// Optional.  This will add a class to give the icon a fixed width.  Use when creating lists which have icons.
	private static final String FIXED_ATTRIBUTE = "fixed";

	/*
	Attribute to remove an item from a list.
	 */
	private static final String ATTRIBUTE_REMOVE = "remove";
	/*
	Attribute to delete an item.  This should be used when deleting an item from the UI.
	 */
	private static final String ATTRIBUTE_DELETE = "delete";
	/*
	Attribute to be used in a warning message.
	 */
	private static final String ATTRIBUTE_WARNING = "warning";
	/*
	Attribute to be used for an IRIDA 'thing' identifier
	 */
	private static final String ATTRIBUTE_IDENTIFIER = "id";
	/*
	Attribute to be used when referencing and organism
	 */
	private static final String ATTRIBUTE_ORGANISM = "organism";
	/*
	Attribute to be used when referring to a date
	 */
	private static final String ATTRIBUTE_CALENDAR = "date";
	/*
	Attribute to be used to add a loading indicator
	 */
	private static final String ATTRIBUTE_LOADING = "loading";

	// OTHER
	// =================================================================================================================
	/*
	DOM Element to create the icon in.
	 */
	private static final String ICON_CONTAINER = "span";
	/*
	Font-Awesome base class
	 */
	private static final String ICON_BASE = "fa fa-";
	/*
	If using multiple icons in a list (such as a side bar) add the 'fixed=""' attribute to append this class.
	This will line up the icons properly.
	 */
	private static final String FIXED_WIDTH_CLASS = " fa-fw";
	/*
	Translation map between type passed on the element and the font-awesome class.
	 */
	private final static Map<String, String> ICON_CLASS_MAP = new HashMap<>();

	public IridaIconElementProcessor() {
		super("icon");

		ICON_CLASS_MAP.put(ATTRIBUTE_REMOVE, "times");
		ICON_CLASS_MAP.put(ATTRIBUTE_DELETE, "trash-o");
		ICON_CLASS_MAP.put(ATTRIBUTE_WARNING, "exclamation-triangle");
		ICON_CLASS_MAP.put(ATTRIBUTE_IDENTIFIER, "barcode");
		ICON_CLASS_MAP.put(ATTRIBUTE_ORGANISM, "leaf");
		ICON_CLASS_MAP.put(ATTRIBUTE_CALENDAR, "calendar-o");
		ICON_CLASS_MAP.put(ATTRIBUTE_LOADING, "fa-spinner fa-pulse");
	}

	@Override protected List<Node> getMarkupSubstitutes(Arguments arguments, Element element) {
		/*
		Create the element
		 */
		final Element container = new Element(ICON_CONTAINER);

		/*
		Get the type of icon needed.
		 */
		final String type = element.getAttributeValue(TYPE_ATTRIBUTE);
		String classString = "";
		if (ICON_CLASS_MAP.containsKey(type)) {
			classString = ICON_BASE + ICON_CLASS_MAP.get(type);
		}

		final Boolean isFixedWidth = element.hasAttribute(FIXED_ATTRIBUTE);
		if (isFixedWidth) {
			classString += FIXED_WIDTH_CLASS;
		}

		container.setAttribute("class", classString);
		return ImmutableList.of(container);
	}

	@Override public int getPrecedence() {
		return 1000;
	}
}
