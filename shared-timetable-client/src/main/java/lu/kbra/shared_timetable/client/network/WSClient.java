package lu.kbra.shared_timetable.client.network;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WSClient extends WebSocketClient {

	private TimetableList timetableList = new TimetableList();
	
	public WSClient(URI serverUri) {
		super(serverUri);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
	}

	@Override
	public void onMessage(String message) {
		
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("Connection closed: " + reason + " (" + code + ")");
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
	}

}
