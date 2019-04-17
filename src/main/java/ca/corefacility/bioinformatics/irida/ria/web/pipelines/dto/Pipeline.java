package ca.corefacility.bioinformatics.irida.ria.web.pipelines.dto;

import java.util.UUID;

/**
 * Analysis Workflow pipeline for consumption by the UI.
 */
public class Pipeline {
	private String name;
	private String description;
	private UUID id;
	private String styleName;

	public Pipeline(String name, String description, UUID id, String styleName) {
		this.name = name;
		this.description = description;
		this.id = id;
		this.styleName = styleName;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public UUID getId() {
		return id;
	}

	public String getStyleName() {
		return styleName;
	}
}
