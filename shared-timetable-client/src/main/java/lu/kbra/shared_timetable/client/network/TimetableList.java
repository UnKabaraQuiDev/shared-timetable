package lu.kbra.shared_timetable.client.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.client.data.VisualTimetableEvent;

@Component
public class TimetableList {

	private List<VisualTimetableEvent> events = new ArrayList<>();

	public TimetableList() {
	}

	public TimetableList(List<VisualTimetableEvent> events) {
		this.events = events;
	}

	public List<VisualTimetableEvent> getEvents() {
		return events;
	}

	public void setEvents(List<VisualTimetableEvent> events) {
		this.events = events;
	}

	public void forEach(Consumer<? super VisualTimetableEvent> action) {
		events.forEach(action);
	}

	public boolean isEmpty() {
		return events.isEmpty();
	}

	public Iterator<VisualTimetableEvent> iterator() {
		return events.iterator();
	}

	public boolean add(VisualTimetableEvent e) {
		return events.add(e);
	}

	public boolean remove(Object o) {
		return events.remove(o);
	}

	public boolean addAll(Collection<? extends VisualTimetableEvent> c) {
		return events.addAll(c);
	}

	public void clear() {
		events.clear();
	}

	public VisualTimetableEvent get(int index) {
		return events.get(index);
	}

	public VisualTimetableEvent remove(int index) {
		return events.remove(index);
	}

	public Stream<VisualTimetableEvent> stream() {
		return events.stream();
	}

	public boolean removeIf(Predicate<VisualTimetableEvent> filter) {
		return events.removeIf(filter);
	}

}
