package lu.kbra.shared_timetable.client.network;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketHttpHeaders;

import lu.kbra.shared_timetable.client.VisualTimetableEvent;
import lu.kbra.shared_timetable.common.Endpoints;
import lu.rescue_rush.spring.ws_ext.client.WSExtClientHandler;
import lu.rescue_rush.spring.ws_ext.client.WebSocketExtClientHandler.WebSocketSessionData;
import lu.rescue_rush.spring.ws_ext.client.annotations.WSPersistentConnection;
import lu.rescue_rush.spring.ws_ext.common.annotations.WSMapping;

@WSPersistentConnection
@WSMapping(path = "/user/persistent")
public class ClientWS extends WSExtClientHandler {

	private static final Logger LOGGER = Logger.getLogger(ClientWS.class.getName());

	@Autowired
	private RemoteConfig remoteConfig;

	@Autowired
	private TimetableList timetableList;

	@Override
	public void onConnect(WebSocketSessionData sessionData) {
		LOGGER.info("Successfully connected to server.");

		super.send(Endpoints.WS_FETCH, null);
	}

	@WSMapping(path = Endpoints.WS_FETCH)
	public void fetch(WebSocketSessionData sessionData, List<VisualTimetableEvent> events) {
		timetableList.clear();
		timetableList.addAll(events);
		System.out.println(events + " " + events.getClass().getName() + " and " + events.get(0).getClass().getName());
	}

	@WSMapping(path = Endpoints.WS_NEW_EVENT)
	public void newEvent(WebSocketSessionData sessionData, VisualTimetableEvent event) {
		timetableList.add(event);
		System.out.println(event);
	}
	
	@WSMapping(path = Endpoints.WS_EDIT_EVENT)
	public void editEvent(WebSocketSessionData sessionData, VisualTimetableEvent event) {
		timetableList.removeIf(e -> e.getId() == event.getId());
		timetableList.add(event);
		System.out.println(event);
	}

	@Override
	public WebSocketHttpHeaders buildHttpHeaders() {
		return new WebSocketHttpHeaders(remoteConfig.buildHttpHeaders());
	}

	@Override
	public URI buildRemoteURI() {
		try {
			return remoteConfig.getWSURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
