package lu.kbra.shared_timetable.client.network;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.client.WebSocketConnectionManager;

import jakarta.annotation.PostConstruct;
import lu.pcy113.pclib.builder.ThreadBuilder;

@Component
public class WebSocketReconnectManager {

	private static final Logger LOGGER = Logger.getLogger(WebSocketReconnectManager.class.getName());

	@Autowired
	private WebSocketConnectionManager wsManager;

	private final int initialDelayMs = 1000; // initial reconnect delay
	private final int maxDelayMs = 30000; // max delay
	private volatile boolean tryingReconnect = false;
	private volatile boolean stopped = false;
	private volatile boolean error = false;

	@PostConstruct
	private void init() {
		this.wsManager.setAutoStartup(false); // control manually
	}
	
	@EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        start();
    }

	public void start() {
		stopped = false;
		tryConnect();
	}

	public void stop() {
		stopped = true;
		tryingReconnect = false;
		error = false;
		wsManager.stop();
	}

	public boolean isTryingReconnect() {
		return tryingReconnect;
	}

	public boolean isConnected() {
		return wsManager.isConnected();
	}

	public boolean isStopped() {
		return stopped;
	}

	public boolean isError() {
		return error;
	}

	private void tryConnect() {
		ThreadBuilder.create(() -> {
			int delay = initialDelayMs;

			while (!stopped) {
				tryingReconnect = true;
				try {
					wsManager.start();
					error = false;
					LOGGER.info("WebSocket connected.");
					return; // exit loop on success
				} catch (Exception e) {
					error = true;
					LOGGER.log(Level.WARNING, "Failed to connect WebSocket, retrying in " + delay + "ms", e);

					try {
						Thread.sleep(delay);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						return;
					}

					delay = Math.min(delay * 2, maxDelayMs); // exponential backoff
				}
			}

			tryingReconnect = false;
		}).name("WebSocket-Reconnect-Thread").start();
	}

	public void handleClosed(CloseStatus status) {
		int code = status.getCode();
		if (code != 1000 && !stopped) { // reconnect only if not normal closure
			error = true;
			LOGGER.warning("WebSocket closed with code " + code + ". Reconnecting...");
			tryConnect();
		} else {
			error = false;
			LOGGER.info("WebSocket closed normally.");
		}
	}

}
