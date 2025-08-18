package lu.kbra.shared_timetable.server.ws;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import lu.kbra.shared_timetable.common.TimetableEventData;
import lu.kbra.shared_timetable.server.db.datas.UserData;
import lu.kbra.shared_timetable.server.services.TimetableEventService;
import lu.rescue_rush.spring.ws_ext.common.annotations.WSMapping;
import lu.rescue_rush.spring.ws_ext.server.WSExtServerHandler;
import lu.rescue_rush.spring.ws_ext.server.WebSocketExtServerHandler.WebSocketSessionData;
import lu.rescue_rush.spring.ws_ext.server.annotations.WSTimeout;

@WSTimeout(false)
@WSMapping(path = "/user/persistent")
public class UserNotifierWS extends WSExtServerHandler {

	@Autowired
	private TimetableEventService timetableDataService;

	@WSMapping(path = "/fetch")
	public List<TimetableEventData> fetch(WebSocketSessionData sessionData) {
		return timetableDataService.fetch((UserData) sessionData.getUser());
	}

}
