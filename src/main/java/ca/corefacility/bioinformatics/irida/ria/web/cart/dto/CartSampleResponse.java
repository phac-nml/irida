package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

public class CartSampleResponse {
	private Long id;
	private String label;
	private Project project;

	public CartSampleResponse(ca.corefacility.bioinformatics.irida.model.project.Project project, Sample sample) {
		this.id = sample.getId();
		this.label = sample.getLabel();
		this.project = new Project(project);
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public Project getProject() {
		return project;
	}

	class Project {
		private Long id;
		private String label;

		Project(ca.corefacility.bioinformatics.irida.model.project.Project project) {
			this.id = project.getId();
			this.label = project.getLabel();
		}

		public Long getId() {
			return id;
		}

		public String getLabel() {
			return label;
		}
	}
}
