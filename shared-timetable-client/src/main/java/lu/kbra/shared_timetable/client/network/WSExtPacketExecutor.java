package lu.kbra.shared_timetable.client.network;

import org.springframework.web.socket.WebSocketSession;

public class WSExtPacketExecutor {

	private WSClient wsClient;

	private WebSocketSession webSocketSession;

	public void onConnected() {
	}

	public void onDisconnected() {
	}

	public final WSClient getWsClient() {
		return wsClient;
	}

	public final WebSocketSession getWebSocketSession() {
		return webSocketSession;
	}

	public final void setWsClient(WSClient wsClient) {
		this.wsClient = wsClient;
	}

	public final void setWebSocketSession(WebSocketSession webSocketSession) {
		this.webSocketSession = webSocketSession;
	}

}
