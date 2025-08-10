package lu.kbra.shared_timetable.server.db.datas;

import java.time.LocalDateTime;
import java.util.List;

import lu.pcy113.pclib.db.autobuild.column.AutoIncrement;
import lu.pcy113.pclib.db.autobuild.column.Column;
import lu.pcy113.pclib.db.autobuild.column.PrimaryKey;
import lu.pcy113.pclib.db.impl.DataBaseEntry;

public class TimetableEventData implements DataBaseEntry {

	public enum Category {
		STUDENTS, TEACHERS, STAFF
	}

	@Column
	@PrimaryKey
	@AutoIncrement
	private long id;

	@Column
	private String name;

	@Column
	private String location;

	@Column
	private LocalDateTime startTime;

	@Column
	private LocalDateTime endTime;

	@Column
	private List<Category> categories;

	public TimetableEventData() {
	}

	public TimetableEventData(String name, String location, LocalDateTime startTime, LocalDateTime endTime, List<Category> categories) {
		this.name = name;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
		this.categories = categories;
	}

}
