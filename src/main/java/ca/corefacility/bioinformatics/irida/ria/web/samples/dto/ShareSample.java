package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

public class ShareSample {
	private Long id;
	private String label;
	private boolean owner;

	public ShareSample(Long id, String label, boolean owner) {
		this.id = id;
		this.label = label;
		this.owner = owner;
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public boolean isOwner() {
		return owner;
	}
}
