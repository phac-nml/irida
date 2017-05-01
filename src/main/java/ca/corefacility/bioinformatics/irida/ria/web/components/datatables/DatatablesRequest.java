package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

public class DatatablesRequest {
	private int start;
	private int length;
	private int draw;

	public DatatablesRequest(int start, int length, int draw) {
		this.start = start;
		this.length = length;
		this.draw = draw;
	}

	public int getDraw() {
		return draw;
	}

	public int getCurrentPage() {
		return (int) Math.floor(start / length);
	}
}
