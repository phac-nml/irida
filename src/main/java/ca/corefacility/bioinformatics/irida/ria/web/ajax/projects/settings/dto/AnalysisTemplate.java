package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto;

/**
 * Response class for easily formatting analysis templates for the project settings page
 */
public class AnalysisTemplate {

	private final Long id;
	private final String name;
	private final String analysisType;
	private final boolean enabled;
	private final String statusMessage;

	public AnalysisTemplate(Long id, String name, String analysisType, boolean enabled, String statusMessage) {
		this.id = id;
		this.name = name;
		this.analysisType = analysisType;
		this.statusMessage = statusMessage;
		this.enabled = enabled;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAnalysisType() {
		return analysisType;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getStatusMessage() {
		return statusMessage;
	}
}
