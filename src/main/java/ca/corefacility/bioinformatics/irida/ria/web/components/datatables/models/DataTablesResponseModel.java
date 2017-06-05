package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models;

public abstract class DataTablesResponseModel {
    public static final String ROW_ID_PREFIX = "row_";
    private String DT_RowId;

    public String getDT_RowId() {
        return DT_RowId;
    }

    public void setDT_RowId(Long identifier) {
        this.DT_RowId = ROW_ID_PREFIX + identifier;
    }
}
