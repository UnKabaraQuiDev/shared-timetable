package lu.kbra.shared_timetable.server.services;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lu.kbra.shared_timetable.server.db.datas.TimetableEventData;
import lu.kbra.shared_timetable.server.ws.UserNotifierWS;

@Service
public class UserNotifierService {

	private static final Logger LOGGER = Logger.getLogger(UserNotifierService.class.getName());

	@Autowired
	private UserNotifierWS userNotifierWS;

	public void notifyEventCreated(TimetableEventData event) {
		final Object payload = event;

		final int count = userNotifierWS.getWebSocketHandler().broadcast("/new-event", payload);

		LOGGER.info("Notified " + count + " about new event: " + event.toString());
	}

}
