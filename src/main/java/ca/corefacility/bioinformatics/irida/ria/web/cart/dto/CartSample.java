package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

/**
 * Used to represent a {@link ca.corefacility.bioinformatics.irida.model.sample.Sample} on the UI Cart Page.
 */
public class CartSample {
	private Long id;
	private String label;
	private Project project;

	public CartSample(ca.corefacility.bioinformatics.irida.model.project.Project project, CartSampleRequest sample) {
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

	/**
	 * The sample needs to know what project it is from.
	 */
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
