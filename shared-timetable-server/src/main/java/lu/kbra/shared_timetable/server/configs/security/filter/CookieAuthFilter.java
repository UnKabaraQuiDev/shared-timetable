package lu.kbra.shared_timetable.server.configs.security.filter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lu.kbra.shared_timetable.server.db.datas.UserData;
import lu.kbra.shared_timetable.server.services.UserService;
import lu.kbra.shared_timetable.server.utils.HandlerMethodResolver;
import lu.kbra.shared_timetable.server.utils.SpringUtils;
import lu.rescue_rush.spring.ws_ext.server.annotations.AllowAnonymous;

@Component
public class CookieAuthFilter extends OncePerRequestFilter {

	@Autowired
	private UserService userService;

	@Autowired
	private HandlerMethodResolver handlerMethodResolver;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final boolean optionalFilter = handlerMethodResolver.resolve(request).hasAnnotation(AllowAnonymous.class);

		if (SpringUtils.isContextUser()) {
			request.setAttribute("user", SpringUtils.getContextUser());
			request.setAttribute("auth", SecurityContextHolder.getContext().getAuthentication());

			filterChain.doFilter(request, response);
			return;
		}

		final Cookie[] cookies = request.getCookies();
		final HandlerMethod handler = (HandlerMethod) request.getAttribute("handler");
		final String requestURI = request.getRequestURI();

		if (cookies != null) {
			final Optional<UserData> user = userService.cookies(cookies);

			if (user.isPresent()) { // even if the filter is optional, we still auth the user and attach it to the
				// session
				SpringUtils.setContextUser(user.get());
				request.setAttribute("user", user);
				request.setAttribute("auth", SecurityContextHolder.getContext().getAuthentication());

				filterChain.doFilter(request, response);
				return;

			} else if (optionalFilter) { // if the user isn't found BUT the filter is optional, we don't care LOOL

				filterChain.doFilter(request, response);
				return;
			}

			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
			return;

		} else if (optionalFilter) { // if the user isn't found (no cookies >_<) BUT the filter is optional, we don't
										// care LOOL
			filterChain.doFilter(request, response);
			return;
		}

		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token.");
	}

}
