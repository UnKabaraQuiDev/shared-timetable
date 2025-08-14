package lu.kbra.shared_timetable.server.db.tables;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.server.db.datas.TimetableEventData;
import lu.kbra.shared_timetable.server.db.datas.TimetableEventData.Category;
import lu.kbra.shared_timetable.server.utils.QueryBuilder;
import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.utils.DataBaseEntryUtils;

@Component
public class TimetableEventTable extends STTable<TimetableEventData> {

	public TimetableEventTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	public List<TimetableEventData> upcoming() {
		return super.query(QueryBuilder
				.select(this)
				.where("end_time > NOW()") /* future */
				.and("start_time <= NOW() + INTERVAL 5 DAY") /* within 5 days */
				.orderByAsc("start_time")
				.list()).run();
	}
	
	public TimetableEventData updateTimetableEvent(TimetableEventData data) {
		return super.update(data).run();
	}

	public TimetableEventData create(String name, String location, LocalDateTime start, LocalDateTime end, List<Category> categories) {
		return super.insertAndReload(new TimetableEventData(name, location, start, end, categories)).run();
	}

}
