package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.servlet.http.HttpServletRequest;

import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesColumnDefinitions;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;

/**
 * Annotation for {@link DataTablesParams}
 * This is used to handle an ajax request from a client for a <a href="https://datatables.net/">DataTables</a>
 * response.  This is used to intercept the {@link HttpServletRequest} and capture
 * <a href="https://datatables.net/manual/server-side">server side DataTables parameters</a>.
 *
 * This dependent on: {@link DataTablesRequestResolver}, {@link DataTablesParams}, {@link DataTablesColumnDefinitions}.
 *
 * Intended usage:
 * {@code public DataTablesResponse getDataTablesResponse(@DataTablesRequest DataTablesParams params) { ... }}
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataTablesRequest {
}
