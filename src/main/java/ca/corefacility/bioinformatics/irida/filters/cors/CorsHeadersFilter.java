/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.corefacility.bioinformatics.irida.filters.cors;

import com.google.common.base.Strings;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adds Cross Origin Resource Sharing (CORS) headers to all requests submitted.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class CorsHeadersFilter implements Filter {
    
    private static final Logger log = LoggerFactory.getLogger(CorsHeadersFilter.class);

    @Override
    public void init(FilterConfig fc) throws ServletException {
        
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            // This header is required for cross-domain requests
            httpResponse.addHeader("Access-Control-Allow-Origin", "*");
            httpResponse.addHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            httpResponse.addHeader("Access-Control-Allow-Headers", "x-requested-with, X-XSRF-TOKEN, Authorization");
        }
        
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String origin = httpRequest.getHeader("Origin");
            String requestMethods = httpRequest.getHeader("Access-Control-Request-Method");
            String method = httpRequest.getMethod();
            // If the Origin header isn't present, this is not a preflight request
            if (!Strings.isNullOrEmpty(method) && method.equals("OPTIONS") &&
                    !Strings.isNullOrEmpty(origin) && !Strings.isNullOrEmpty(requestMethods)) {
                // This is a CORS request, don't send it up the filter chain.
                log.debug("Not forwarding CORS preflight request up the filter chain.");
            } else {
                log.debug("Sending non-preflight request up the filter chain.");
                log.debug("origin: {}, requestMethods: {}, method: {}", origin, requestMethods, method);
                fc.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {
        
    }
}
