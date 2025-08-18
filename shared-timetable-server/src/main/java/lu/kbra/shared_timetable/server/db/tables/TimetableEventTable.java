package lu.kbra.shared_timetable.server.db.tables;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.common.TimetableEventData;
import lu.kbra.shared_timetable.common.TimetableEventData.TimetableEventCategory;
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

	public TimetableEventData create(
			String name,
			String location,
			LocalDateTime start,
			LocalDateTime end,
			List<TimetableEventCategory> categories) {
		return super.insertAndReload(new TimetableEventData(name, location, start, end, categories)).run();
	}

	public Optional<TimetableEventData> byId(int id) {
		return super.load(new TimetableEventData(id)).toOptional().run();
	}

	public Collection<String> getCandidateNames(String start, int limit) {
		return super.query(QueryBuilder
				.select(this)
				.where("name", "LIKE", start + "%")
				.transform(l -> l.stream().map(TimetableEventData::getName).collect(Collectors.toList()))).run();
	}

	public Collection<String> getCandidateLocations(String start, int limit) {
		return super.query(QueryBuilder
				.select(this)
				.where("location", "LIKE", start + "%")
				.transform(l -> l.stream().map(TimetableEventData::getName).collect(Collectors.toList()))).run();
	}

}
