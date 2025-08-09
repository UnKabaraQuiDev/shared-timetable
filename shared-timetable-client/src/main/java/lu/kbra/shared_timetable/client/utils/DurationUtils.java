package lu.kbra.shared_timetable.client.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class DurationUtils {

	public static String formatDuration(LocalDateTime time) {
		final Duration remainingTime = Duration.between(LocalDateTime.now(), time);
		final long remainingTotalSeconds = remainingTime.getSeconds();
		final long remainingMinutes = Math.abs(remainingTotalSeconds) / 60;
		final long remainingSeconds = Math.abs(remainingTotalSeconds) % 60;

		final String prefix = remainingTotalSeconds >= 0 ? "T-" : "T+";
		final String formatted = String.format("%s %02d:%02d", prefix, remainingMinutes, remainingSeconds);

		return formatted;
	}
	
}
