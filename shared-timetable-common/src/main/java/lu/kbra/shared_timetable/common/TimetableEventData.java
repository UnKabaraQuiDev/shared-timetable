package lu.kbra.shared_timetable.common;

import java.time.LocalDateTime;
import java.util.List;

import lu.pcy113.pclib.db.autobuild.column.AutoIncrement;
import lu.pcy113.pclib.db.autobuild.column.Column;
import lu.pcy113.pclib.db.autobuild.column.PrimaryKey;
import lu.pcy113.pclib.db.impl.DataBaseEntry;

public class TimetableEventData implements DataBaseEntry {

	public static enum TimetableEventCategory {
		STUDENTS, TEACHERS, STAFF;

		public static String completeLast(String input) {
			String[] parts = input.split(";");
			String last = parts[parts.length - 1];

			for (TimetableEventCategory role : TimetableEventCategory.values()) {
				if (role.name().startsWith(last)) {
					parts[parts.length - 1] = role.name();
					break;
				}
			}

			return String.join(";", parts);
		}
	}

	@Column
	@PrimaryKey
	@AutoIncrement
	private long id;

	@Column
	protected String name;

	@Column
	protected String location;

	@Column
	protected LocalDateTime startTime;

	@Column
	protected LocalDateTime endTime;

	@Column
	protected List<TimetableEventCategory> categories;

	public TimetableEventData() {
	}

	public TimetableEventData(long id) {
		this.id = id;
	}

	public TimetableEventData(String name, String location, LocalDateTime startTime, LocalDateTime endTime,
			List<TimetableEventCategory> categories) {
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

	public List<TimetableEventCategory> getCategories() {
		return categories;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public void setCategories(List<TimetableEventCategory> categories) {
		this.categories = categories;
	}

	public String asMarkdown() {
		return String
				.format("__**%s**__ *(%d)*\nLocation: %s\nStart: %s\nEnd: %s\nCategories: %s\n\n",
						name,
						id,
						location,
						startTime,
						endTime,
						categories);
	}

	@Override
	public String toString() {
		return "TimetableEventData [id=" + id + ", name=" + name + ", location=" + location + ", startTime=" + startTime + ", endTime="
				+ endTime + ", categories=" + categories + "]";
	}

}
