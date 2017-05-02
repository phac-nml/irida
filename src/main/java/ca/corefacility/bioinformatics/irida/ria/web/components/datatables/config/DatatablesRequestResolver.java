package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

/**
 * Created by josh on 2017-05-01.
 */
public class DatatablesRequestResolver implements HandlerMethodArgumentResolver {
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		DatatablesRequest datatablesRequest = parameter.getParameterAnnotation(DatatablesRequest.class);
		if (datatablesRequest != null) {
			if (DatatablesCriterias.class.isAssignableFrom(parameter.getParameterType())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		DatatablesRequest datatablesRequest = parameter.getParameterAnnotation(DatatablesRequest.class);
		return null;
	}
}
