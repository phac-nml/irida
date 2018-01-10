package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;

/**
 * This is returned to client in an {@link javax.servlet.http.HttpServletResponse} in the format the can be consumed
 * by jQuery DataTables.
 *
 * @see <a href="https://datatables.net">DataTables</a>
 */
public class DataTablesResponse {
	private DataTablesParams dataTablesParams;
	private Long recordsTotal;
	private Long recordsFiltered;
	private List<? extends DataTablesResponseModel> data;

	public DataTablesResponse(DataTablesParams dataTablesParams, Page<?> page, List<? extends DataTablesResponseModel> data) {
		this.dataTablesParams = dataTablesParams;
		this.recordsTotal = page.getTotalElements();
		this.recordsFiltered = page.getTotalElements();
		this.data = data;
	}

	public DataTablesResponse(DataTablesParams dataTablesParams, long recordsTotal,
			List<DataTablesResponseModel> data) {
		this.dataTablesParams = dataTablesParams;
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsTotal;
		this.data = data;
	}

	/**
	 * Get the draw counter. This is used by DataTables to ensure that the Ajax returns from server-side processing
	 * requests are drawn in sequence by DataTables.  Extracted directly from the initial
	 * {@code org.apache.http.HttpRequest}
	 *
	 * @return {@link Integer}
	 */
	public int getDraw() {
		return dataTablesParams.getDraw();
	}

	/**
	 * Total records, before filtering (i.e. the total number of records in the database)
	 *
	 * @return {@link Long}
	 */
	public long getRecordsTotal() {
		return recordsTotal;
	}

	/**
	 * Total records, after filtering (i.e. the total number of records after filtering has been applied
	 * - not just the number of records being returned for this page of data).
	 *
	 * @return {@link Long}
	 */
	public long getRecordsFiltered() {
		return recordsFiltered;
	}

	/**
	 * The data to be displayed in the table. This is an array of data source objects, one for each row,
	 * which will be used by DataTables.
	 *
	 * @return {@link List}
	 */
	public List<DataTablesResponseModel> getData() {
		return Lists.newArrayList(data);
	}
}
