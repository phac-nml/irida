package ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto;

/**
 * Response class for easily formatting analysis templates for the project settings page
 */
public class TemplateResponseDTO {

	private Long id;
	private String name;
	private String analysisType;

	public TemplateResponseDTO(Long id, String name, String analysisType) {
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
