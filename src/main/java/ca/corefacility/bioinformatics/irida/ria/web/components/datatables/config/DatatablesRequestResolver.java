package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DatatablesParams;

/**
 * Created by josh on 2017-05-01.
 */
public class DatatablesRequestResolver implements HandlerMethodArgumentResolver {
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		DatatablesRequest datatablesRequest = parameter.getParameterAnnotation(DatatablesRequest.class);
		if (datatablesRequest != null) {
			if (DatatablesParams.class.isAssignableFrom(parameter.getParameterType())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
		return DatatablesParams.parseDatatablesParams(servletRequest);
	}
}
