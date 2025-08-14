package lu.kbra.shared_timetable.server.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lu.kbra.shared_timetable.server.Permission;
import lu.kbra.shared_timetable.server.db.datas.TimetableEventData;
import lu.kbra.shared_timetable.server.db.datas.UserData;
import lu.kbra.shared_timetable.server.db.tables.TimetableEventTable;
import lu.kbra.shared_timetable.server.utils.SpringUtils;

@Service
public class TimetableEventService {

	@Autowired
	private TimetableEventTable timetableEventTable;

	public List<TimetableEventData> fetch(UserData user) {
		SpringUtils.forbidden(!user.getPermissions().contains(Permission.FETCH), "Missing permission: " + Permission.FETCH);
		return timetableEventTable.upcoming();
	}

}
