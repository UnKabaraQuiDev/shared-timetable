package lu.kbra.shared_timetable.server.configs.security.filter;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lu.kbra.shared_timetable.server.STMain;
import lu.kbra.shared_timetable.server.utils.HandlerMethodResolver;
import lu.kbra.shared_timetable.server.utils.HandlerMethodResolver.AbstractRequestHandler;
import lu.kbra.shared_timetable.server.utils.SpringUtils;
import lu.pcy113.pclib.PCUtils;
import lu.rescue_rush.spring.ws_ext.AllowAnonymous;

@Component
public class AllowAnonymousFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = Logger.getLogger(AllowAnonymousFilter.class.getName());

	@Autowired
	private HandlerMethodResolver handlerMethodResolver;

	@Autowired
	private SecurityContextRepository securityContextRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final AbstractRequestHandler<?> handler = handlerMethodResolver.resolve(request);
		final String sessionId = request.getSession(true).getId();

		if (SpringUtils.isContextUser()) { // already auth.
			if (STMain.DEBUG) {
				LOGGER.info("User is already auth for: " + request.getRequestURI());
			}

			filterChain.doFilter(request, response);
			return;
		}

		if (handler.hasAnnotation(AllowAnonymous.class)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			if (STMain.DEBUG) {
				LOGGER.info("Anonymous allowed for: " + request.getRequestURI());
			}

			if (auth == null) {
				auth = new AnonymousAuthenticationToken(sessionId, PCUtils.hashMap("initiator", request.getRequestURI()),
						AuthorityUtils.createAuthorityList("ANONYMOUS"));
				SecurityContextHolder.getContext().setAuthentication(auth);
				securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
			}

			request.setAttribute("auth", auth);

			filterChain.doFilter(request, response);
			return;
		} else if (SpringUtils.isAnonymous()) { // Reject anonymous access if not allowed
			if (STMain.DEBUG) {
				LOGGER.warning("Anonymous not allowed for: " + request.getRequestURI());
			}
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorized only.");
			return;
		}

		// user is authenticated
		filterChain.doFilter(request, response);
	}
}
