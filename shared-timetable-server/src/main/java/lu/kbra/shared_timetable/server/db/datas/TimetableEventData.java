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

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public String asMarkdown() {
		return String
				.format("__**%s**__ *(%d)*\nLocation: %s\nStart: %s\nEnd: %s\nCategories: %s\n\n", name, id, location, startTime, endTime, categories);
	}

	@Override
	public String toString() {
		return "TimetableEventData [id=" + id + ", name=" + name + ", location=" + location + ", startTime=" + startTime + ", endTime="
				+ endTime + ", categories=" + categories + "]";
	}

}
