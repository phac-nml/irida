package ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto;

/**
 * Response class for easily formatting analysis templates for the project settings page
 */
public class TemplateResponse {

	private Long id;
	private String name;
	private String analysisType;

	public TemplateResponse(Long id, String name, String analysisType) {
		this.id = id;
		this.name = name;
		this.analysisType = analysisType;
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

}
