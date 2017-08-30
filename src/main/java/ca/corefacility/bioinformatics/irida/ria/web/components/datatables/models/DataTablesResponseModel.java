package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Responsible for adding a DataTables rowId property to the response.  This is important
 * for being able to properly handle row selection with page refresh (and paging) in DataTables.
 * Also we cannot just use a regular identifier since this must be a valid HTML id, which must
 * not have an integer as its first character.
 *
 * @see <a href="https://datatables.net/reference/option/rowId">DataTable rowId</a>
 */
public interface DataTablesResponseModel {
    public static final String ROW_ID_PREFIX = "row_";
    public Long getId();

    @JsonProperty("DT_RowId")
    public default String getDT_RowId() {
        return ROW_ID_PREFIX + getId();
    }
}
