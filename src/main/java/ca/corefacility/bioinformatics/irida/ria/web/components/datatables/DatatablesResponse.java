package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.util.List;

import org.springframework.data.domain.Page;

public class DatatablesResponse {
	private DatatablesParams datatablesParams;
	private Long recordsTotal;
	private Long recordsFiltered;
	private List<Object> data;

	public DatatablesResponse(DatatablesParams datatablesParams, Page<?> page, List<Object> data) {
		this.datatablesParams = datatablesParams;
		this.recordsTotal = page.getTotalElements();
		this.recordsFiltered = page.getTotalElements();
		this.data = data;
	}

	public int getDraw() {
		return datatablesParams.getDraw();
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
