package ca.corefacility.bioinformatics.irida.ria.dialects.processors.icons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.google.common.collect.ImmutableMap;

/**
 * Font Awesome Icons (http://fortawesome.github.io/Font-Awesome/)
 */
public class FontAwesome {

	/*
	Font-Awesome base class
	 */
	private static final String ICON_BASE = "fa fa-";
	/*
	Attribute to remove an item from a list.
	 */
	private static final String REMOVE_ATTRIBUTE = "remove";
	private static final String REMOVE_ICON = ICON_BASE + "times";
	/*
	Attribute to delete an item.  This should be used when deleting an item from the UI.
	 */
	private static final String DELETE_ATTRIBUTE = "delete";
	private static final String DELETE_ICON = ICON_BASE + "trash-o";
	/*
	Attribute to be used in a warning message.
	 */
	private static final String WARNING_ATTRIBUTE = "warning";
	private static final String WARNING_ICON = ICON_BASE + "exclamation-triangle";
	/*
	Attribute to be used for an IRIDA 'thing' identifier
	 */
	private static final String IDENTIFIER_ATTRIBUTE = "id";
	private static final String IDENTIFIER_ICON = ICON_BASE + "barcode";
	/*
	Attribute to be used when referencing and organism
	 */
	private static final String ORGANISM_ATTRIBUTE = "organism";
	private static final String ORGANISM_ICON = ICON_BASE + "leaf";
	/*
	Attribute to be used when referring to a date
	 */
	private static final String CALENDAR_ATTRIBUTE = "date";
	private static final String CALENDAR_ICON = ICON_BASE + "calendar-o";
	/*
	Attribute to be used to add a loading indicator
	 */
	private static final String LOADING_ATTRIBUTE = "loading";
	private static final String LOADING_ICON = ICON_BASE + "fa-spinner fa-pulse";

	/*
	If using multiple icons in a list (such as a side bar) add the 'fixed=""' attribute to append this class.
	This will line up the icons properly.
	 */
	private static final String FIXED_WIDTH_CLASS = " fa-fw";

	private static final Map<String, String> FA_ICON_CLASS = new ImmutableMap.Builder<String, String>()
			.put(REMOVE_ATTRIBUTE, REMOVE_ICON)
			.put(DELETE_ATTRIBUTE, DELETE_ICON)
			.put(WARNING_ATTRIBUTE, WARNING_ICON)
			.put(IDENTIFIER_ATTRIBUTE, IDENTIFIER_ICON)
			.put(ORGANISM_ATTRIBUTE, ORGANISM_ICON)
			.put(CALENDAR_ATTRIBUTE, CALENDAR_ICON)
			.put(LOADING_ATTRIBUTE, LOADING_ICON)
			.build();

	private List<String> classes;

	public FontAwesome(String type) throws IconNotFoundException {
		if (FA_ICON_CLASS.containsKey(type)) {
			classes = new ArrayList<>();
			classes.add(FA_ICON_CLASS.get(type));
		} else {
			throw new IconNotFoundException("Do not have icon registered for type: " + type);
		}
	}
	
	public String getClassList() {
		return StringUtils.collectionToDelimitedString(classes, " ");
	}

	public void isFixedWidth() {
		classes.add(FIXED_WIDTH_CLASS);
	}
}
