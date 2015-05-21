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
	private static final String REMOVE_ICON = "times";
	/*
	Attribute to delete an item.  This should be used when deleting an item from the UI.
	 */
	private static final String DELETE_ATTRIBUTE = "delete";
	private static final String DELETE_ICON = "trash-o";
	/*
	Attribute to be used in a warning message.
	 */
	private static final String WARNING_ATTRIBUTE = "warning";
	private static final String WARNING_ICON = "exclamation-triangle";
	/*
	Attribute to be used for an IRIDA 'thing' identifier
	 */
	private static final String IDENTIFIER_ATTRIBUTE = "id";
	private static final String IDENTIFIER_ICON = "barcode";
	/*
	Attribute to be used when referencing and organism
	 */
	private static final String ORGANISM_ATTRIBUTE = "organism";
	private static final String ORGANISM_ICON = "leaf";
	/*
	Attribute to be used when referring to a date
	 */
	private static final String CALENDAR_ATTRIBUTE = "date";
	private static final String CALENDAR_ICON = "calendar-o";
	/*
	Attribute to be used to add a loading indicator
	 */
	private static final String LOADING_ATTRIBUTE = "loading";
	private static final String LOADING_ICON = "spinner fa-pulse";
	/*
	Attribute to be used for all download actions
	 */
	private static final String DOWNLOAD_ATTRIBUTE = "download";
	private static final String DOWNLOAD_ICON = "download";
	/*
	Attribute for pipeline types
	 */
	private static final String PIPELINE_TYPE_ATTRIBUTE = "pipelineType";
	private static final String PIPELINE_TYPE_ICON = "cogs";
	/*
	Attribute for pipeline state
	 */
	private static final String PIPELINE_STATE_ATTRIBUTE = "pipelineState";
	private static final String PIPELINE_STATE_ICON = "history";
	/*
	Attribute to be used for all files
	 */
	private static final String FILE_ATTRIBUTE = "file";
	private static final String FILE_ICON = "file-o";

	/*
	If using multiple icons in a list (such as a side bar) add the 'fixed=""' attribute to append this class.
	This will line up the icons properly.
	 */
	private static final String FIXED_WIDTH_CLASS = "fa-fw";

	private static final Map<String, String> FA_ATTRIBUTE_TO_CLASS_MAP = new ImmutableMap.Builder<String, String>()
			.put(REMOVE_ATTRIBUTE, REMOVE_ICON)
			.put(DELETE_ATTRIBUTE, DELETE_ICON)
			.put(WARNING_ATTRIBUTE, WARNING_ICON)
			.put(IDENTIFIER_ATTRIBUTE, IDENTIFIER_ICON)
			.put(ORGANISM_ATTRIBUTE, ORGANISM_ICON)
			.put(CALENDAR_ATTRIBUTE, CALENDAR_ICON)
			.put(LOADING_ATTRIBUTE, LOADING_ICON)
			.put(DOWNLOAD_ATTRIBUTE, DOWNLOAD_ICON)
			.put(PIPELINE_TYPE_ATTRIBUTE, PIPELINE_TYPE_ICON)
			.put(PIPELINE_STATE_ATTRIBUTE, PIPELINE_STATE_ICON)
			.put(FILE_ATTRIBUTE, FILE_ICON)
			.build();

	private List<String> classes;

	public FontAwesome(String type) throws IconNotFoundException {
		if (FA_ATTRIBUTE_TO_CLASS_MAP.containsKey(type)) {
			classes = new ArrayList<>();
			classes.add(ICON_BASE + FA_ATTRIBUTE_TO_CLASS_MAP.get(type));
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
