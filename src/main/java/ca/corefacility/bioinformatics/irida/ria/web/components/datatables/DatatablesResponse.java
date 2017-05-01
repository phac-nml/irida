package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.util.List;

public class DatatablesResponse {
	private int draw;
	private Long recordsTotal;
	private Long recordsFiltered;
	private List<Object> data;

	public DatatablesResponse(int draw, long recordsTotal, long recordsFiltered, List<Object> data) {
		this.draw = draw;
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
		this.data = data;
	}

	public int getDraw() {
		return draw;
	}

	public long getRecordsTotal() {
		return recordsTotal;
	}

	public long getRecordsFiltered() {
		return recordsFiltered;
	}

	public List<Object> getData() {
		return data;
	}
}
