package lu.kbra.shared_timetable.server.configs.ws;

import java.util.Map;
import java.util.logging.Logger;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

	private static final Logger LOGGER = Logger.getLogger(AuthHandshakeInterceptor.class.getName());

	@Override
	public boolean beforeHandshake(
			ServerHttpRequest request,
			ServerHttpResponse response,
			WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		final ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
		final HttpServletRequest httpRequest = servletRequest.getServletRequest();

		if (httpRequest.getAttribute("user") != null) {
			attributes.put("user", httpRequest.getAttribute("user"));
		}
		attributes.put("auth", httpRequest.getAttribute("auth"));

		attributes.put("httpRequest.source", httpRequest.getRemoteAddr());

		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
	}

}
