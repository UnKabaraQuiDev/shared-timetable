package lu.kbra.shared_timetable.client.network;

import java.util.ArrayList;
import java.util.List;

import lu.kbra.shared_timetable.client.data.TimetableEvent;

public class TimetableList {

	private List<TimetableEvent> events = new ArrayList<>();

	public List<TimetableEvent> getEvents() {
		return events;
	}

	public void setEvents(List<TimetableEvent> events) {
		this.events = events;
	}

}
