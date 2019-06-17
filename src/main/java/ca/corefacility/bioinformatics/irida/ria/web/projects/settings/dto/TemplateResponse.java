package ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto;

/**
 * Response class for easily formatting analysis templates for the project settings page
 */
public class TemplateResponse {

	private Long id;
	private String name;
	private String analysisType;
	private boolean enabled;
	private String statusMessage;

	public TemplateResponse(Long id, String name, String analysisType, boolean enabled, String statusMessage) {
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
