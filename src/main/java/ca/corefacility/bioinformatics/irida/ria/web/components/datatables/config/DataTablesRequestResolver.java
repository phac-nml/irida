package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;

/**
 * Request resolver for {@link DataTablesParams}
 */
public class DataTablesRequestResolver implements HandlerMethodArgumentResolver {
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		DataTablesRequest dataTablesRequest = parameter.getParameterAnnotation(DataTablesRequest.class);
		if (dataTablesRequest != null) {
			if (DataTablesParams.class.isAssignableFrom(parameter.getParameterType())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
		return DataTablesParams.parseDataTablesParams(servletRequest);
	}
}
