package lu.kbra.shared_timetable.client.network;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.annotation.PostConstruct;

@Component
public class PacketExecutorRegistry {

	private static final Logger LOGGER = Logger.getLogger(PacketExecutorRegistry.class.getName());

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WSExtPacketExecutor wsExtPacketExecutor;

	@Autowired
	private WSClient wsClient;

	private final Map<String, Method> targetMethods = new HashMap<>();
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@PostConstruct
	private void init() {
		wsExtPacketExecutor.setWsClient(wsClient);

		for (Method m : wsExtPacketExecutor.getClass().getDeclaredMethods()) {
			if (!m.isAnnotationPresent(WSEndpoint.class))
				continue;

			targetMethods.put(normalizeURI(m.getAnnotation(WSEndpoint.class).value()), m);
		}
	}

	public void onPacketReceived(WebSocketSession webSocketSession, WebSocketMessage<?> message) throws Exception {
		if (!(message instanceof TextMessage)) {
			throw new IllegalArgumentException("Only supports TextMessages.");
		}

		if (wsExtPacketExecutor.getWsClient() != webSocketSession) {
			LOGGER.warning("WebSocketSession instance changed !");
		}
		wsExtPacketExecutor.setWebSocketSession(webSocketSession);

		final String body = ((TextMessage) message).getPayload();
		final JsonNode incomingJson = objectMapper.readTree(body);
		illegalArgument(!incomingJson.has("destination"), "Invalid packet format: missing 'destination' field.", body);
		final String requestPath = incomingJson.get("destination").asText();
		final String packetId = incomingJson.has("packetId") ? incomingJson.get("packetId").asText() : null;
		final JsonNode payload = incomingJson.get("payload");

		final Method method = resolveMethod(requestPath);
		illegalArgument(method == null, "No method found for destination: " + requestPath, body);
		final boolean returnsVoid = method.getReturnType().equals(Void.TYPE);

		Exception err = null;
		try {
			Object returnValue = null;

			if (method.getParameterCount() == 1) {
				final Class<?> parameterType = method.getParameterTypes()[1];
				illegalArgument(payload == null, "Payload expected for destination: " + requestPath, body);
				final Object param = objectMapper.readValue(payload.toString(), parameterType);

				returnValue = method.invoke(wsExtPacketExecutor, param);
			} else if (method.getParameterCount() == 0) {
				returnValue = method.invoke(wsExtPacketExecutor);
			} else {
				LOGGER.warning("Method " + method.getName() + " has an invalid number of parameters: " + method.getParameterCount());
				return;
			}

			if (!returnsVoid) {
				final ObjectNode root = objectMapper.createObjectNode();
				root.set("payload", objectMapper.valueToTree(returnValue));
				root.put("destination", requestPath);
				if (packetId != null) {
					root.put("packetId", packetId);
				}
				final String jsonResponse = objectMapper.writeValueAsString(root);

				webSocketSession.sendMessage(new TextMessage(jsonResponse));
			}
		} catch (Exception e) {
			err = e;

			final ObjectNode root = objectMapper.createObjectNode();
			root.put("status", 500);
			root.set("packet", incomingJson);

			if (e instanceof ResponseStatusException rse) {
				root.put("status", rse.getStatusCode().value());
				root.put("message", rse.getReason());
			}

			webSocketSession.sendMessage(new TextMessage(root.toString()));
		}

		if (err != null) {
			throw err;
		}
	}

	private Method resolveMethod(String requestPath) {
		requestPath = normalizeURI(requestPath);
		final List<String> matchingPatterns = new ArrayList<>();
		for (String pattern : targetMethods.keySet()) {
			if (pathMatcher.match(pattern, requestPath)) {
				matchingPatterns.add(pattern);
			}
		}
		matchingPatterns.sort(pathMatcher.getPatternComparator(requestPath));
		String bestPattern = matchingPatterns.get(0);
		Method bestMatch = targetMethods.get(bestPattern);

		return bestMatch;
	}

	private void illegalArgument(boolean b, String string, String payload) {
		if (b) {
			throw new IllegalArgumentException(string, new Throwable(payload));
		}
	}

	public static String normalizeURI(String path) {
		if (path == null || path.isEmpty())
			return "/";
		String trimmed = path.replaceAll("^/+", "").replaceAll("/+$", "");
		return "/" + trimmed;
	}

}
