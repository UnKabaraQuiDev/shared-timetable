package lu.kbra.shared_timetable.common;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationUtils {

	public static String formatDuration(LocalDateTime time) {
		Duration remainingTime = Duration.between(LocalDateTime.now(), time);
		long totalSeconds = remainingTime.getSeconds();
		long absSeconds = Math.abs(totalSeconds);

		long hours = absSeconds / 3600;
		long minutes = (absSeconds % 3600) / 60;
		long seconds = absSeconds % 60;

		String prefix = totalSeconds >= 0 ? "T-" : "T+";
		if (hours > 0)
			return String.format("%s %02d:%02d:%02d", prefix, hours, minutes, seconds);
		else
			return String.format("%s %02d:%02d", prefix, minutes, seconds);
	}

	private static final Pattern TOKEN = Pattern.compile("(-?\\d+)([smhd])");

	public static LocalDateTime applyOffset(LocalDateTime base, String input) {
		Matcher matcher = TOKEN.matcher(input);
		LocalDateTime result = base;

		while (matcher.find()) {
			int value = Integer.parseInt(matcher.group(1));
			String unit = matcher.group(2);

			switch (unit) {
			case "s":
				result = result.plus(value, ChronoUnit.SECONDS);
				break;
			case "m":
				result = result.plus(value, ChronoUnit.MINUTES);
				break;
			case "h":
				result = result.plus(value, ChronoUnit.HOURS);
				break;
			case "d":
				result = result.plus(value, ChronoUnit.DAYS);
				break;
			default:
				throw new IllegalArgumentException("Unsupported unit: " + unit);
			}
		}
		return result;
	}

	private static final String[] ORDER = { "d", "h", "m", "s" };

	public static String autocomplete(String input) {
		Matcher matcher = TOKEN.matcher(input);
		StringBuilder sb = new StringBuilder();

		String lastUnit = null;

		while (matcher.find()) {
			String number = matcher.group(1);
			String unit = matcher.group(2);

			sb.append(number);

			if (unit.isEmpty()) {
				if (lastUnit == null) {
					unit = "h";
				} else {
					unit = nextSmallerUnit(lastUnit);
				}
			}

			sb.append(unit).append(" ");
			lastUnit = unit;
		}

		return sb.toString().trim();
	}

	private static String nextSmallerUnit(String unit) {
		switch (unit) {
		case "d":
			return "h";
		case "h":
			return "m";
		case "m":
			return "s";
		default:
			return "s";
		}
	}

}
