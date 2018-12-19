package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

public class CartSample {
	private Long id;
	private String label;
	private boolean editable;
	private Project project;

	public CartSample(ca.corefacility.bioinformatics.irida.model.project.Project project, CartSampleRequest sample) {
		this.id = sample.getId();
		this.label = sample.getLabel();
		this.editable = sample.isEditable();
		this.project = new Project(project);
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public boolean isEditable() {
		return editable;
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
