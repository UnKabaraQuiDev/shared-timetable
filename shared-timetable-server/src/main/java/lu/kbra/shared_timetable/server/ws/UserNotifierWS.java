package lu.kbra.shared_timetable.server.ws;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import lu.kbra.shared_timetable.server.db.datas.TimetableEventData;
import lu.kbra.shared_timetable.server.db.datas.UserData;
import lu.kbra.shared_timetable.server.services.TimetableEventService;
import lu.rescue_rush.spring.ws_ext.WSExtHandler;
import lu.rescue_rush.spring.ws_ext.WebSocketHandlerExt.WebSocketSessionData;
import lu.rescue_rush.spring.ws_ext.annotations.WSMapping;

@WSMapping(path = "/user-notifier")
public class UserNotifierWS extends WSExtHandler {

	@Autowired
	private TimetableEventService timetableDataService;
	
	@WSMapping(path = "/fetch")
	public List<TimetableEventData> fetch(WebSocketSessionData sessionData) {
		return timetableDataService.fetch((UserData) sessionData.getUser());
	}
	
}
