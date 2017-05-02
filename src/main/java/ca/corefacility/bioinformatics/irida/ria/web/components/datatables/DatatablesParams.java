package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

public class DatatablesParams {
	private Integer start;
	private Integer length;
	private Integer draw;

	public DatatablesParams(Integer start, Integer length, Integer draw) {
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
