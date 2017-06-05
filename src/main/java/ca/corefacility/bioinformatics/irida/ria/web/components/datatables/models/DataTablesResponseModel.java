package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for adding a DataTables rowId property to the response.  This is important
 * for being able to properly handle row selection with page refresh (and paging) in DataTables.
 * Also we cannot just use a regular identifier since this must be a valid HTML id, which must
 * not have an integer as its first character.
 * Require on client side to add within the DataTables configuration object:
 * {
 *     ...
 *     rowId: 'dt_rowId'
 * }
 *
 * @see <a href="https://datatables.net/reference/option/rowId">DataTable rowId</a>
 */
public abstract class DataTablesResponseModel {
    private static final Logger logger = LoggerFactory.getLogger(DataTablesResponseModel.class);
    public static final String ROW_ID_PREFIX = "row_";
    private String DT_RowId;

    public DataTablesResponseModel(Object object) {
        Class<?> clazz = object.getClass();
        try {
            Class[] noparams = {};
            Method method = clazz.getDeclaredMethod("getId", noparams);
            this.DT_RowId = ROW_ID_PREFIX + method.invoke(object);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            logger.error("Cannot find method 'getId()' on object used to create DataTables row");
        }
    }

    public String getDT_RowId() {
        return DT_RowId;
    }
}
