package lu.kbra.shared_timetable.client.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.common.TimetableEventData;

@Component
public class TimetableList implements Iterable<TimetableEventData> {

	private List<TimetableEventData> events = new ArrayList<>();

	public TimetableList() {
	}

	public TimetableList(List<TimetableEventData> events) {
		this.events = events;
	}

	public List<TimetableEventData> getEvents() {
		return events;
	}

	public void setEvents(List<TimetableEventData> events) {
		this.events = events;
	}

	public void forEach(Consumer<? super TimetableEventData> action) {
		events.forEach(action);
	}

	public boolean isEmpty() {
		return events.isEmpty();
	}

	@Override
	public Iterator<TimetableEventData> iterator() {
		return events.iterator();
	}

	public boolean add(TimetableEventData e) {
		return events.add(e);
	}

	public boolean remove(Object o) {
		return events.remove(o);
	}

	public boolean addAll(Collection<? extends TimetableEventData> c) {
		return events.addAll(c);
	}

	public void clear() {
		events.clear();
	}

	public TimetableEventData get(int index) {
		return events.get(index);
	}

	public TimetableEventData remove(int index) {
		return events.remove(index);
	}

	public Stream<TimetableEventData> stream() {
		return events.stream();
	}

	public boolean removeIf(Predicate<TimetableEventData> filter) {
		return events.removeIf(filter);
	}

	public void sort(Comparator<? super TimetableEventData> c) {
		events.sort(c);
	}

}
