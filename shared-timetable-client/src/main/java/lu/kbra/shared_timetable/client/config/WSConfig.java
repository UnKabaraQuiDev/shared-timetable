package lu.kbra.shared_timetable.client.config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClientException;
import org.springframework.web.socket.client.WebSocketConnectionManager;

import lu.kbra.shared_timetable.client.STClientMain;
import lu.kbra.shared_timetable.client.network.RemoteConfig;
import lu.kbra.shared_timetable.client.network.WSClient;

@Configuration
public class WSConfig {

	private static final Logger LOGGER = Logger.getLogger(WSConfig.class.getName());

	@Autowired
	private RemoteConfig remoteConfig;

	@Autowired
	private WSClient wsClient;

	@Bean
	public WebSocketConnectionManager wsConnectionManager()
			throws URISyntaxException, RestClientException, IOException, InterruptedException, ExecutionException {
		if (STClientMain.DEBUG) {
			LOGGER.info(remoteConfig.buildHttpHeaders().toString());
		}

		final WebSocketConnectionManager manager = new WebSocketConnectionManager(wsClient, wsClient,
				remoteConfig.getWSURI(RemoteConfig.USER_PERSISTENT)) {
			@Override
			public void startInternal() {
				try {
					wsClient.doAuth();
				} catch (RestClientException | URISyntaxException | IOException | InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}

				this.setHeaders(remoteConfig.buildHttpHeaders());

				super.startInternal();
			};
		};
		manager.setHeaders(remoteConfig.buildHttpHeaders());
		manager.setAutoStartup(true);

		return manager;
	}

}
