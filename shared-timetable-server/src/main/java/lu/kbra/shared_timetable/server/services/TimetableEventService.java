package lu.kbra.shared_timetable.server.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import lu.kbra.shared_timetable.common.TimetableEventData;
import lu.kbra.shared_timetable.common.TimetableEventData.TimetableEventCategory;
import lu.kbra.shared_timetable.server.Permission;
import lu.kbra.shared_timetable.server.db.datas.UserData;
import lu.kbra.shared_timetable.server.db.tables.TimetableEventTable;
import lu.kbra.shared_timetable.server.utils.SpringUtils;

@Service
public class TimetableEventService {

	@Autowired
	private TimetableEventTable timetableEventTable;

	@Autowired
	@Lazy
	private UserNotifierService userNotifierService;

	public List<TimetableEventData> fetch(UserData user) {
		SpringUtils.forbidden(!user.getPermissions().contains(Permission.FETCH), "Missing permission: " + Permission.FETCH);
		return fetch();
	}

	public List<TimetableEventData> fetch() {
		return timetableEventTable.upcoming();
	}

	public TimetableEventData createEvent(String name, String location, LocalDateTime start, LocalDateTime end) {
		return createEvent(name, location, start, end, List.of());
	}

	public TimetableEventData createEvent(
			String name,
			String location,
			LocalDateTime start,
			LocalDateTime end,
			List<TimetableEventCategory> categories) {
		final TimetableEventData event = timetableEventTable.create(name, location, start, end, categories);

		userNotifierService.notifyEventCreated(event);

		return event;
	}

}
