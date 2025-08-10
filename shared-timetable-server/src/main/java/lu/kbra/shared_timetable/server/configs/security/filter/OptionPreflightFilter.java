package lu.kbra.shared_timetable.server.configs.security.filter;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lu.kbra.shared_timetable.server.STMain;

@Component
public class OptionPreflightFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = Logger.getLogger(OptionPreflightFilter.class.getName());

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			if (STMain.DEBUG) {
				LOGGER.info("Skipping preflight request to: " + request.getRequestURL());
			}
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}

		filterChain.doFilter(request, response);
	}

}
