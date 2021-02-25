package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto;

/**
 * UI Model for the minimum and maximum coverage and genome size for a project
 */
public class Coverage {
	private int minimum;
	private int maximum;
	private Long genomeSize;

	public Coverage() {
	}

	public Coverage(int minimum, int maximum, Long genomeSize) {
		this.minimum = minimum;
		this.maximum = maximum;
		this.genomeSize = genomeSize;
	}

	public int getMinimum() {
		return minimum;
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public Long getGenomeSize() {
		return genomeSize;
	}

	public void setGenomeSize(Long genomeSize) {
		this.genomeSize = genomeSize;
	}
}
