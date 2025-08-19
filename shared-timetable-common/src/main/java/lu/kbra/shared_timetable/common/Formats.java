package lu.kbra.shared_timetable.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Consumer;

public final class Formats {

	public static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
	public static final DateTimeFormatter SHORT_DATE_TIME_FMT = DateTimeFormatter.ofPattern("HH:mm dd/MM");
	public static final DateTimeFormatter SHORT_DATE_TIME_REVERSED_FMT = DateTimeFormatter.ofPattern("dd/MM HH:mm");

	public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	public static final DateTimeFormatter SHORT_DATE_FMT = DateTimeFormatter.ofPattern("dd/MM");
	
	public static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
	public static final DateTimeFormatter SHORT_TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

	public static LocalDateTime parseDateTime(String time, Consumer<String> event) {
		try {
			return LocalDateTime.parse(time, Formats.DATE_TIME_FMT);
		} catch (DateTimeParseException e) {
			event.accept("Valid format: `" + Formats.DATE_TIME_FMT.toString() + "` for: `" + time + "`");
			return null;
		}
	}

	public static LocalDateTime complete(String start, LocalDate date) {
		final String[] parts = start.split(":");

		final int hour = parts.length > 0 && !parts[0].isEmpty() ? Integer.parseInt(parts[0]) : 0;
		final int minute = parts.length > 1 && !parts[1].isEmpty() ? Integer.parseInt(parts[1]) : 0;
		final int second = parts.length > 2 && !parts[2].isEmpty() ? Integer.parseInt(parts[2]) : 0;

		final LocalTime time = LocalTime.of(hour, minute, second);
		final LocalDateTime dateTime = LocalDateTime.of(date, time);

		return dateTime;
	}

}
