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
	Attribute for adding items to the caart
	 */
	private static final String CART_ADD_ATTRIBUTE = "cart-plus";
	private static final String CART_ADD_ICON = "cart-plus";
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
	Attribute to be used when the move action is required
	 */
	private static final String MOVE_ATTRIBUTE = "move";
	private static final String MOVE_ICON = "truck";
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
	Attribute to be used for uploading
	 */
	private static final String UPLOAD_ATTRIBUTE = "upload";
	private static final String UPLOAD_ICON = "upload";
	/*
	Attribute for pipeline types
	 */
	private static final String PIPELINE_TYPE_ATTRIBUTE = "pipelineType";
	private static final String PIPELINE_TYPE_ICON = "cogs";
	
	/*
	Attribute for pipeline types
	 */
	private static final String PIPELINE_VERSION_ATTRIBUTE = "pipelineVersion";
	private static final String PIPELINE_VERSION_ICON = "code-fork";
	
	/*
	Attribute for pipeline state
	 */
	private static final String PIPELINE_STATE_ATTRIBUTE = "pipelineState";
	private static final String PIPELINE_STATE_ICON = "heartbeat";
	/*
	Attribute to be used for all files
	 */
	private static final String FILE_ATTRIBUTE = "file";
	private static final String FILE_ICON = "file-o";
	
	private static final String PROJECT_ATTRIBUTE = "project";
	private static final String PROJECT_ICON = "folder";
	
	/*
	Attribute to display terminal icon
	 */
	private static final String TERMINAL_ATTRIBUTE = "terminal";
	private static final String TERMINAL_ICON = "terminal";
	/*
	 * Attribute be used to indicate for collapsible panels
	 */
	private static final String COLLAPSE_SHOW_ATTRIBUTE = "show";
	private static final String COLLAPSE_SHOW_ICON = "chevron-right";
	private static final String COLLAPSE_CLOSE_ATTRIBUTE = "hide";
	private static final String COLLAPSE_CLOSE_ICON = "chevron-down";

	/*
	 * Attribute to display a sample flask
	 */
	private static final String SAMPLE_ATTRIBUTE = "sample";
	private static final String SAMPLE_ICON = "flask";

	/*
	 * Attribute for a user icon
	 */
	private static final String USER_ATTRIBUTE = "user";
	private static final String USER_ICON = "user";
	
	/*
	 * Attribute for a group icon
	 */
	private static final String GROUP_ATTRIBUTE = "group";
	private static final String GROUP_ICON = "users";

	/*
	 * Attribute for a banned icon
	 */
	private static final String BAN_ATTRIBUTE = "ban";
	private static final String BAN_ICON = "ban";

	/*
	 * Attribute for the popover question-circle
	 */
	private static final String QUESTIONCIRCLE_ATTRIBUTE = "question-circle";
	private static final String QUESTIONCIRCLE_ICON = "question-circle";

	/*
	 * Attribute for email
	 */
	private static final String EMAIL_ATTRIBUTE = "email";
	private static final String EMAIL_ICON = "envelope-o";

	/*
	 * Attribute for an external link
	 */
	public static final String EXTERNAL_LINK_ATTRIBUTE = "external-link";
	public static final String EXTERNAL_LINK_ICON = "external-link";

	public static final String ALIGN_ATTRIBUTE = "align";
	public static final String ALIGN_ICON = "align-right";

	/*
	 * Attribute for cloud upload
	 */
	private static final String CLOUD_ATTRIBUTE = "cloud";
	private static final String CLOUD_ICON = "cloud";

	/*
	 * Attribute for an dropdown carets
	 */
	private static final String CARET_DOWN_ATTRIBUTE = "caret-down";
	private static final String CARET_DOWN_ICON = "caret-down";

	/*
	 * Attribute for next and previous buttons.
	 */
	public static final String NEXT_ATTRIBUTE = "next";
	public static final String NEXT_ICON = "chevron-circle-right";
	public static final String PREVIOUS_ATTRIBUTE = "prev";
	public static final String PREVIOUS_ICON = "chevron-circle-left";

	/*
	* If using multiple icons in a list (such as a side bar) add the 'fixed=""'
    * attribute to append this class. This will line up the icons properly.
    */
	private static final String FIXED_WIDTH_CLASS = "fa-fw";

	/*
	 * Map to convert the attribute to the icon name
	 */
	// @formatter:off
	private static final Map<String, String> FA_ATTRIBUTE_TO_CLASS_MAP = new ImmutableMap.Builder<String, String>()
			.put(CART_ADD_ATTRIBUTE, CART_ADD_ICON)
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
			.put(UPLOAD_ATTRIBUTE, UPLOAD_ICON)
			.put(PIPELINE_TYPE_ATTRIBUTE, PIPELINE_TYPE_ICON)
			.put(PIPELINE_VERSION_ATTRIBUTE, PIPELINE_VERSION_ICON)
			.put(PIPELINE_STATE_ATTRIBUTE, PIPELINE_STATE_ICON)
			.put(FILE_ATTRIBUTE, FILE_ICON)
			.put(COPY_ATTRIBUTE, COPY_ICON)
			.put(MOVE_ATTRIBUTE, MOVE_ICON)
			.put(TERMINAL_ATTRIBUTE, TERMINAL_ICON)
			.put(COLLAPSE_SHOW_ATTRIBUTE, COLLAPSE_SHOW_ICON)
			.put(COLLAPSE_CLOSE_ATTRIBUTE, COLLAPSE_CLOSE_ICON)
			.put(SAMPLE_ATTRIBUTE, SAMPLE_ICON)
			.put(USER_ATTRIBUTE, USER_ICON)
			.put(BAN_ATTRIBUTE, BAN_ICON)
			.put(QUESTIONCIRCLE_ATTRIBUTE, QUESTIONCIRCLE_ICON)
			.put(EMAIL_ATTRIBUTE, EMAIL_ICON)
			.put(EXTERNAL_LINK_ATTRIBUTE, EXTERNAL_LINK_ICON)
			.put(PROJECT_ATTRIBUTE,PROJECT_ICON)
			.put(CLOUD_ATTRIBUTE, CLOUD_ICON)
			.put(CARET_DOWN_ATTRIBUTE, CARET_DOWN_ICON)
			.put(NEXT_ATTRIBUTE, NEXT_ICON)
			.put(PREVIOUS_ATTRIBUTE, PREVIOUS_ICON)
			.put(GROUP_ATTRIBUTE, GROUP_ICON)
			.put(ALIGN_ATTRIBUTE, ALIGN_ICON)
			.build();
	// @formatter:on

	/*
	Map to convert the size attribute to an icon size.
	 */
	// @formatter:off
	private static final Map<String, String> ICON_SIZE = ImmutableMap.of(
			"lg", "fa-lg",
			"2x", "fa-2x",
			"3x", "fa-3x",
			"4x", "fa-4x",
			"5x", "fa-5x"
	);
	// @formatter:on

	private List<String> classes;

	/**
	 * Used to build a {@link FontAwesome}
	 */
	public static class Builder {
		private List<String> classes;

		/**
		 * Creates a {@link Builder} with the type of icon to create
		 *
		 * @param type {@link String} type of icon to use.
		 * @throws IconNotFoundException if the icon type doesn't exist
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
		 * @param isFixed {@link Boolean} if it is to be fixed width
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
		 * @param size {@link String} desired icon size or null
		 * @return {@link Builder}
		 * @throws IconNotFoundException if the icon size isn't available
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
	 * @param builder The {@link Builder} to build the {@link FontAwesome}
	 */
	public FontAwesome(Builder builder) {
		this();
		this.classes = builder.classes;
	}

	/**
	 * Get the {@link Builder} for Font-Awesome Icons
	 *
	 * @param type {@link String} type of icon to create.
	 * @return {@link Builder} for icons
	 * @throws IconNotFoundException if the icon type doesn't exist
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
