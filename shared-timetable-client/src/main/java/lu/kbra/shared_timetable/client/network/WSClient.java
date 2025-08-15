package lu.kbra.shared_timetable.client.network;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import lu.kbra.shared_timetable.client.config.RemoteConfiguration;
import lu.kbra.shared_timetable.client.utils.SpringUtils;

@Component
public class WSClient extends StandardWebSocketClient implements WebSocketHandler {

	private static final Logger LOGGER = Logger.getLogger(WSClient.class.getName());

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RemoteConfiguration remoteConfiguration;

	@Autowired
	private RemoteConfig remoteConfig;
	
	@Autowired
	@Lazy
	private PacketExecutorRegistry packetExecutorRegistry;

	@Autowired
	@Lazy
	private WebSocketConnectionManager webSocketConnectionManager;

	@Autowired
	@Lazy
	private WebSocketReconnectManager webSocketReconnectManager;

	public void doAuth() throws URISyntaxException, RestClientException, IOException, InterruptedException, ExecutionException {
		if (!remoteConfig.isRegenToken() && !SpringUtils.validString(remoteConfig.getToken())) {
			throw new IllegalStateException("Username/password auth (user.regenToken) is turned off, but no token was provided.");
		}

		if (!checkForAuth()) {// we need to auth with pass & user
			if (!remoteConfig.isRegenToken()) {
				throw new IllegalStateException(
						"Username/password auth (user.regenToken) is turned off and the authentication token is outdated/invalid.");
			}

			LOGGER.info("Token invalid, regenerating.");

			regenToken();

			LOGGER.info("Token regenerated.");
		} else {
			LOGGER.info("Token still valid.");
		}
	}

	private void regenToken() throws RestClientException, URISyntaxException, IOException {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		final HttpEntity<String> entity = new HttpEntity<>(
				"{\"name\": \"" + remoteConfig.getUsername() + "\", \"password\": \"" + remoteConfig.getPassword() + "\"}", headers);

		final ResponseEntity<Void> response = restTemplate
				.exchange(remoteConfig.getHTTPURI(RemoteConfig.USER_LOGIN), HttpMethod.POST, entity, Void.class);

		if (!response.getStatusCode().is2xxSuccessful()) {
			throw new IllegalStateException("Username/password auth not successful: " + response.getStatusCode().value());
		}

		final HttpHeaders responseHeaders = response.getHeaders();
		if (responseHeaders.containsKey(HttpHeaders.SET_COOKIE)) {
			for (String cookie : responseHeaders.get(HttpHeaders.SET_COOKIE)) {
				if (cookie.startsWith("token=")) {
					remoteConfig.setToken(cookie.substring("token=".length(), cookie.indexOf(';')));
					remoteConfiguration.save(remoteConfig);
					return;
				}
			}
		}

		throw new IllegalStateException("Username/pass auth successful, but got no token cookie.");
	}

	private boolean checkForAuth() throws RestClientException, URISyntaxException {
		final HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.COOKIE, "token=" + remoteConfig.getToken());

		final HttpEntity<Void> entity = new HttpEntity<>(headers);
		final ResponseEntity<Void> response = restTemplate
				.exchange(remoteConfig.getHTTPURI(RemoteConfig.USER_CHECK), HttpMethod.GET, entity, Void.class);

		return response.getStatusCode().is2xxSuccessful();
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		webSocketReconnectManager.handleClosed(status);
		
		if (status.equalsCode(CloseStatus.NORMAL)) {
			LOGGER.info("WebSocket closed normally: " + status);
		} else if (status.equalsCode(CloseStatus.NO_CLOSE_FRAME)) {
			LOGGER.severe("Abnormal closure (connection lost): " + status);
		} else {
			LOGGER.warning("WebSocket closed with status: " + status);
		}
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

}
