package lu.kbra.shared_timetable.server.discord.commands.slash;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.common.DurationUtils;
import lu.kbra.shared_timetable.common.Formats;
import lu.kbra.shared_timetable.common.TimetableEventData;
import lu.kbra.shared_timetable.common.TimetableEventData.TimetableEventCategory;
import lu.kbra.shared_timetable.server.db.tables.TimetableEventTable;
import lu.kbra.shared_timetable.server.services.UserNotifierService;
import lu.pcy113.pclib.PCUtils;
import lu.rescue_rush.spring.jda.command.slash.SlashCommandAutocomplete;
import lu.rescue_rush.spring.jda.command.slash.SlashCommandExecutor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component("edit")
public class EditCommand implements SlashCommandExecutor, SlashCommandAutocomplete {

	@Autowired
	private TimetableEventTable timetableEventTable;

	@Autowired
	private UserNotifierService userNotifierService;

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		final String field = event.getOption("field").getAsString();
		final String value = event
				.getOption("value", () -> null, o -> o.getAsString().equals("/") || o.getAsString().isBlank() ? null : o.getAsString());
		final int id = event.getOption("id").getAsInt();

		// validate input
		switch (field) {
		case "name":
		case "location":
		case "start_time":
		case "end_time":
		case "categories_add":
		case "categories_remove":
			if (value == null) {
				event.reply("Value cannot be empty for: `" + field + "`").setEphemeral(true).queue();
				return;
			}
			break;
		}

		event.deferReply().queue();

		final Optional<TimetableEventData> dataOp = timetableEventTable.byId(id);

		if (dataOp.isEmpty()) {
			event.getHook().sendMessage("No event with id: `" + id + "`").setEphemeral(true).queue();
			return;
		}

		final TimetableEventData data = dataOp.get();

		final String oldValue = switch (field) {
		case "name" -> data.getName();
		case "location" -> data.getLocation();
		case "start_time", "start_time_offset" -> data.getStartTime().format(Formats.DATE_TIME_FMT);
		case "end_time", "end_time_offset" -> data.getEndTime().format(Formats.DATE_TIME_FMT);
		case "categories_add", "categories_remove", "categories_set" -> data.getCategories().toString();
		case "time_offset" -> data.getStartTime().format(Formats.DATE_TIME_FMT) + " - " + data.getEndTime().format(Formats.DATE_TIME_FMT);
		default -> "Invalid action ?";
		};

		final Consumer<String> ephemeral = s -> event.getHook().sendMessage(s).setEphemeral(true).queue();
		final List<TimetableEventCategory> categories = field.startsWith("categories")
				? Arrays
						.stream(value.split(";"))
						.map(e -> PCUtils.enumValuetoEnum(TimetableEventCategory.class, e))
						.filter(Predicate.not(Objects::isNull))
						.collect(Collectors.toList())
				: null;

		switch (field) {
		case "name":
			data.setName(value);
			break;
		case "location":
			data.setLocation(value);
			break;

		case "start_time":
			data.setStartTime(Formats.parseDateTime(value, ephemeral));
			break;
		case "end_time":
			data.setEndTime(Formats.parseDateTime(value, ephemeral));
			break;

		case "categories_add":
			data.getCategories().addAll(categories);
			break;
		case "categories_remove":
			data.getCategories().removeAll(categories);
			break;
		case "categories_set":
			data.setCategories(categories);
			break;

		case "start_time_offset":
			if (value == null) {
				data.setStartTime(LocalDateTime.now());
				break;
			}
			data.setStartTime(DurationUtils.applyOffset(data.getStartTime(), value));
			break;
		case "end_time_offset":
			if (value == null) {
				data.setEndTime(LocalDateTime.now());
				break;
			}
			data.setEndTime(DurationUtils.applyOffset(data.getEndTime(), value));
			break;

		case "time_offset":
			if (value == null) {
				final Duration offset = Duration.between(LocalDateTime.now(), data.getStartTime());
				data.setStartTime(data.getStartTime().plus(offset));
				data.setEndTime(data.getEndTime().plus(offset));
				break;
			}
			data.setStartTime(DurationUtils.applyOffset(data.getStartTime(), value));
			data.setEndTime(DurationUtils.applyOffset(data.getEndTime(), value));
			break;
		}

		final String newValue = switch (field) {
		case "name" -> data.getName();
		case "location" -> data.getLocation();
		case "start_time", "start_time_offset" -> data.getStartTime().format(Formats.DATE_TIME_FMT);
		case "end_time", "end_time_offset" -> data.getEndTime().format(Formats.DATE_TIME_FMT);
		case "categories_add", "categories_remove", "categories_set" -> data.getCategories().toString();
		case "time_offset" -> data.getStartTime().format(Formats.DATE_TIME_FMT) + " - " + data.getEndTime().format(Formats.DATE_TIME_FMT);
		default -> "Invalid action ?";
		};

		final String changedMsg = "Changed field `" + field + "` from `" + oldValue + "` to `" + newValue + "`. ";

		event
				.getHook()
				.sendMessage(changedMsg + "Saving...")
				.queue(e -> timetableEventTable
						.update(data)
						.catch_(ex -> e
								.editMessage(changedMsg + "Failed: " + ex.getMessage() + " (" + e.getClass().getSimpleName() + ")")
								.queue())
						.thenParallel(s -> e.editMessage(changedMsg + "Saved!").queue())
						.thenParallel(s -> userNotifierService.notifyEventUpdated(s))
						.thenParallel(s -> e.editMessage(changedMsg + "Saved & pushed!").queue())
						.run());
	}

	@Override
	public void complete(CommandAutoCompleteInteractionEvent event) {
		if (!"value".equals(event.getFocusedOption().getName())) {
			return;
		}

		final String field = event.getOption("field", () -> null, OptionMapping::getAsString);
		final String currentValue = event
				.getOption("value", () -> null, o -> o.getAsString().equals("/") || o.getAsString().isBlank() ? null : o.getAsString());
		final int id = event.getOption("id", () -> null, OptionMapping::getAsInt);
		final Optional<TimetableEventData> dataOp = timetableEventTable.byId(id);

		if (dataOp.isEmpty()) {
			event.replyChoice("No event with id: `" + id + "`", "/");
			return;
		}

		final TimetableEventData data = dataOp.get();

		if (currentValue == null && field.endsWith("_time_offset")) {
			event
					.replyChoices(PCUtils
							.addAll(PCUtils.asArrayList(new Choice("NOW", "/")),
									PCUtils
											.asArrayList("1d", "2d", "5d", "1h", "2h", "4h", "6h", "10m", "15m", "20m", "30m")
											.stream()
											.map(s -> new Choice(s, s))
											.collect(Collectors.toList()))
							.stream()
							.limit(OptionData.MAX_CHOICES)
							.collect(Collectors.toList()))
					.queue();
			return;
		}

		final List<TimetableEventCategory> categories = field.startsWith("categories")
				? Arrays
						.stream(currentValue.split(";"))
						.map(e -> PCUtils.enumValuetoEnum(TimetableEventCategory.class, e))
						.filter(Predicate.not(Objects::isNull))
						.collect(Collectors.toList())
				: null;

		switch (field) {
		case "name":
			event.replyChoiceStrings(timetableEventTable.getCandidateNames(currentValue, OptionData.MAX_CHOICES)).queue();
			break;
		case "location":
			event.replyChoiceStrings(timetableEventTable.getCandidateLocations(currentValue, OptionData.MAX_CHOICES)).queue();
			break;

		case "start_time":
			event
					.replyChoiceStrings(Formats.complete(currentValue, data.getStartTime().toLocalDate()).format(Formats.DATE_TIME_FMT))
					.queue();
			break;
		case "end_time":
			event.replyChoiceStrings(Formats.complete(currentValue, data.getEndTime().toLocalDate()).format(Formats.DATE_TIME_FMT)).queue();
			break;

		case "categories_add":
		case "categories_remove":
		case "categories_set":
			event.replyChoiceStrings(TimetableEventCategory.completeLast(currentValue)).queue();
			break;

		case "start_time_offset":
		case "end_time_offset":
		case "time_offset":
			event.replyChoiceStrings(DurationUtils.autocomplete(currentValue)).queue();
			break;
		}
	}

	@Override
	public OptionData[] options() {
		return new OptionData[] {
				new OptionData(OptionType.STRING, "field", "Action identifier")
						.addChoice("set name", "name")
						.addChoice("set location", "location")
						.addChoice("set start time", "start_time")
						.addChoice("set end time", "end_time")
						.addChoice("add category", "categories_add")
						.addChoice("remove category", "categories_remove")
						.addChoice("set category", "categories_set")
						.addChoice("offset start time", "start_time_offset")
						.addChoice("offset end time", "end_time_offset")
						.addChoice("offset time", "time_offset")
						.setRequired(true),
				new OptionData(OptionType.INTEGER, "id", "id").setRequired(true),
				new OptionData(OptionType.STRING, "value", "value").setAutoComplete(true) };
	}

	@Override
	public String description() {
		return "Edit an event's field";
	}

}
