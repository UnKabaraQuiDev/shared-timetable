package lu.kbra.shared_timetable.common;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import lu.pcy113.pclib.db.autobuild.column.AutoIncrement;
import lu.pcy113.pclib.db.autobuild.column.Column;
import lu.pcy113.pclib.db.autobuild.column.PrimaryKey;
import lu.pcy113.pclib.db.impl.DataBaseEntry;

public class TimetableEventData implements DataBaseEntry, Comparable<TimetableEventData> {

	public static final int UPCOMING_MINUTES = 30, LONG_UPCOMING_MINUTES = 120;

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

	public boolean isPast() {
		return LocalDateTime.now().isAfter(this.getEndTime());
	}

	public boolean isOngoing() {
		LocalDateTime now = LocalDateTime.now();
		return !now.isBefore(this.getStartTime()) && !now.isAfter(this.getEndTime());
	}

	/**
	 * if the event starts in < 30 minutes
	 */
	public boolean isUpcoming() {
		return LocalDateTime.now().isAfter(getUpcomingTime()) && !this.isOngoing();
	}

	/**
	 * if the event starts in < 2 hour
	 */
	public boolean isLongUpcoming() {
		return LocalDateTime.now().isAfter(getLongUpcomingTime()) && !this.isOngoing();
	}

	public int getTotalDuration() {
		return (int) Duration.between(this.getStartTime(), this.getEndTime()).toMinutes();
	}

	public int getElapsedDuration() {
		return (int) Duration.between(this.getStartTime(), LocalDateTime.now()).toMinutes();
	}

	public int getElapsedPercentage() {
		return (int) (getElapsedDuration() * 100 / getTotalDuration());
	}

	public int getRemainingDuration() {
		return (int) Duration.between(LocalDateTime.now(), this.getEndTime()).toMinutes();
	}

	public int getUpcomingDuration() {
		return (int) Duration.between(LocalDateTime.now(), this.getUpcomingTime()).toMinutes();
	}

	public int getStartDuration() {
		return (int) Duration.between(LocalDateTime.now(), this.getStartTime()).toMinutes();
	}

	/**
	 * should flash if the event transitions to upcoming (10s after) or from upcoming to ongoing (20s
	 * before ongoing)
	 */
	public boolean shouldFlash() {
		final LocalDateTime now = LocalDateTime.now();
		return (isUpcoming() && Math.abs(Duration.between(now, getUpcomingTime()).toSeconds()) <= 10)
				|| (Math.abs(Duration.between(now, getStartTime()).toSeconds()) <= 20);
	}

	public boolean isSameDay() {
		return getEndTime().toLocalDate().equals(getStartTime().toLocalDate());
	}

	public boolean isStartToday() {
		return getStartTime().toLocalDate().equals(LocalDateTime.now().toLocalDate());
	}

	public boolean isEndToday() {
		return getEndTime().toLocalDate().equals(LocalDateTime.now().toLocalDate());
	}

	public LocalDateTime getUpcomingTime() {
		return this.getStartTime().minusMinutes(UPCOMING_MINUTES);
	}

	public LocalDateTime getLongUpcomingTime() {
		return this.getStartTime().minusMinutes(LONG_UPCOMING_MINUTES);
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

	@Override
	public int compareTo(TimetableEventData o) {
		return startTime.compareTo(o.startTime);
	}

}
