package ca.corefacility.bioinformatics.irida.ria.dialects.processors.icons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.google.common.base.Strings;
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
	Attribute to be used when merging files
	 */
	private static final String MERGE_ATTRIBUTE = "merge";
	private static final String MERGE_ICON = "compress";
	/*
	Attribute to delete an item.  This should be used when deleting an item from the UI.
	 */
	private static final String DELETE_ATTRIBUTE = "delete";
	private static final String DELETE_ICON = "trash-o";
	/*
	Attribute to be used any time there is something to save
	 */
	private static final String SAVE_ATTRIBUTE = "save";
	private static final String SAVE_ICON = "save";
	/*
	Attribute to be used when the copy action is required
	 */
	private static final String COPY_ATTRIBUTE = "copy";
	private static final String COPY_ICON = "copy";
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
	Attribute to display terminal icon
	 */
	private static final String TERMINAL_ATTRIBUTE = "terminal";
	private static final String TERMINAL_ICON = "terminal";
	/*
	Attribute be used to indicate for collapsible panels
	*/
	private static final String COLLAPSE_SHOW_ATTRIBUTE = "show";
	private static final String COLLAPSE_SHOW_ICON = "chevron-right";
	private static final String COLLAPSE_CLOSE_ATTRIBUTE = "hide";
	private static final String COLLAPSE_CLOSE_ICON = "chevron-down";

	/*
	If using multiple icons in a list (such as a side bar) add the 'fixed=""' attribute to append this class.
	This will line up the icons properly.
	 */
	private static final String FIXED_WIDTH_CLASS = "fa-fw";

	/*
	Map to convert the attribute to the icon name
	 */
	private static final Map<String, String> FA_ATTRIBUTE_TO_CLASS_MAP = new ImmutableMap.Builder<String, String>()
			.put(REMOVE_ATTRIBUTE, REMOVE_ICON)
			.put(DELETE_ATTRIBUTE, DELETE_ICON)
			.put(SAVE_ATTRIBUTE, SAVE_ICON)
			.put(MERGE_ATTRIBUTE, MERGE_ICON)
			.put(WARNING_ATTRIBUTE, WARNING_ICON)
			.put(IDENTIFIER_ATTRIBUTE, IDENTIFIER_ICON)
			.put(ORGANISM_ATTRIBUTE, ORGANISM_ICON)
			.put(CALENDAR_ATTRIBUTE, CALENDAR_ICON)
			.put(LOADING_ATTRIBUTE, LOADING_ICON)
			.put(DOWNLOAD_ATTRIBUTE, DOWNLOAD_ICON)
			.put(PIPELINE_TYPE_ATTRIBUTE, PIPELINE_TYPE_ICON)
			.put(PIPELINE_STATE_ATTRIBUTE, PIPELINE_STATE_ICON)
			.put(FILE_ATTRIBUTE, FILE_ICON)
			.put(COPY_ATTRIBUTE, COPY_ICON)
			.put(TERMINAL_ATTRIBUTE, TERMINAL_ICON)
			.put(COLLAPSE_SHOW_ATTRIBUTE, COLLAPSE_SHOW_ICON)
			.put(COLLAPSE_CLOSE_ATTRIBUTE, COLLAPSE_CLOSE_ICON)
			.build();

	/*
	Map to convert the size attribute to an icon size.
	 */
	private static final Map<String, String> ICON_SIZE = ImmutableMap.of(
			"lg", "fa-lg",
			"2x", "fa-2x",
			"3x", "fa-3x",
			"4x", "fa-4x",
			"5x", "fa-5x"
	);

	private List<String> classes;

	/**
	 * Used to build a {@link FontAwesome}
	 */
	public static class Builder {
		private List<String> classes;

		/**
		 * Creates a {@link Builder} with the type of icon to create
		 * @param type {@link String} type of icon to use.
		 * @throws IconNotFoundException
		 */
		public Builder(String type) throws IconNotFoundException {
			if (FA_ATTRIBUTE_TO_CLASS_MAP.containsKey(type)) {
				classes = new ArrayList<>();
				classes.add(ICON_BASE + FA_ATTRIBUTE_TO_CLASS_MAP.get(type));
			} else {
				throw new IconNotFoundException("Do not have icon registered for type: " + type);
			}
		}

		/**
		 * Sets the fixed with class if needed
		 *
		 * @param isFixed
		 * 		{@link Boolean} if it is to be fixed width
		 *
		 * @return {@link Builder}
		 */
		public Builder fixedWidth(boolean isFixed) {
			if (isFixed) {
				this.classes.add(FIXED_WIDTH_CLASS);
			}
			return this;
		}

		/**
		 * Sets the appropriate icon size for the {@link FontAwesome}
		 *
		 * @param size
		 * 		{@link String} desired icon size or null
		 *
		 * @return {@link Builder}
		 * @throws IconNotFoundException
		 */
		public Builder setIconSize(String size) throws IconNotFoundException {
			if (!Strings.isNullOrEmpty(size)) {
				if (ICON_SIZE.containsKey(size)) {
					this.classes.add(ICON_SIZE.get(size));
				} else {
					throw new IconNotFoundException("Icon size " + size + " is not available");
				}
			}
			return this;
		}

		/**
		 * Build the desired {@link FontAwesome}
		 *
		 * @return {@link FontAwesome}
		 */
		public FontAwesome build() {
			return new FontAwesome(this);
		}
	}

	protected FontAwesome() {
	}

	/**
	 * Builds a new {@link FontAwesome} with the given {@link Builder}
	 *
	 * @param builder
	 * 		The {@link Builder} to build the {@link FontAwesome}
	 */
	public FontAwesome(Builder builder) {
		this();
		this.classes = builder.classes;
	}

	/**
	 * Get the {@link Builder} for Font-Awesome Icons
	 *
	 * @param type
	 * 		{@link String} type of icon to create.
	 *
	 * @return {@link Builder} for icons
	 * @throws IconNotFoundException
	 */
	public static Builder builder(String type) throws IconNotFoundException {
		return new FontAwesome.Builder(type);
	}

	/**
	 * Get the class list in string form for the icon.
	 *
	 * @return {@link String} class list for the desired icon.
	 */
	public String getClassString() {
		return StringUtils.collectionToDelimitedString(classes, " ");
	}
}
