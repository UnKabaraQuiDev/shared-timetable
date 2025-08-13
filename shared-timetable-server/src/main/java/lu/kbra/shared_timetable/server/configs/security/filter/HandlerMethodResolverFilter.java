package lu.kbra.shared_timetable.server.configs.security.filter;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lu.kbra.shared_timetable.server.STMain;
import lu.kbra.shared_timetable.server.utils.HandlerMethodResolver;
import lu.kbra.shared_timetable.server.utils.HandlerMethodResolver.AbstractRequestHandler;

@Component
public class HandlerMethodResolverFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = Logger.getLogger(HandlerMethodResolverFilter.class.getName());

	@Autowired
	private HandlerMethodResolver handlerMethodResolver;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String requestURI = request.getRequestURI();
		final AbstractRequestHandler<?> handler = handlerMethodResolver.resolve(request);

		if (handler == null) {
			if (STMain.DEBUG) {
				LOGGER.warning("Unknown endpoint: " + requestURI);
			}
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint: '" + requestURI + "'.");
			return;
		}

		filterChain.doFilter(request, response);
	}

}
