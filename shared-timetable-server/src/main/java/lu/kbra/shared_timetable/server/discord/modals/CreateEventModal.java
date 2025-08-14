package lu.kbra.shared_timetable.server.discord.modals;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.server.db.datas.TimetableEventData;
import lu.kbra.shared_timetable.server.db.datas.TimetableEventData.Category;
import lu.kbra.shared_timetable.server.services.TimetableEventService;
import lu.kbra.shared_timetable.server.utils.SpringUtils;
import lu.pcy113.pclib.PCUtils;
import lu.rescue_rush.spring.jda.modal.DefaultModalInteractionExecutor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

@Component("create-event")
public class CreateEventModal extends DefaultModalInteractionExecutor {

	@Autowired
	private TimetableEventService timetableEventService;

	@Override
	public void execute(ModalInteractionEvent event) {
		final String name = event.getValue("name").getAsString();
		final String location = event.getValue("location").getAsString();
		final String startTime = event.getValue("start_time").getAsString();
		final String endTime = event.getValue("end_time").getAsString();
		final String categoriesStr = event.getValue("categories") == null ? "" : event.getValue("categories").getAsString();

		if (!SpringUtils.validString(name)) {
			event.reply("Name cannot be empty.").setEphemeral(true).queue();
			return;
		}
		if (!SpringUtils.validString(location)) {
			event.reply("Name cannot be empty.").setEphemeral(true).queue();
			return;
		}

		final LocalDateTime start = parseDateTime(startTime, event);
		final LocalDateTime end = parseDateTime(endTime, event);

		if (start == null || end == null) {
			return;
		}

		if (start.isAfter(end)) {
			event.reply("Start time must be before end time.").setEphemeral(true).queue();
			return;
		}

		final List<Category> categories = Arrays
				.stream(categoriesStr.split(";"))
				.map(e -> PCUtils.enumValuetoEnum(Category.class, e))
				.filter(e -> e != null)
				.collect(Collectors.toList());

		event.deferReply().queue();

		final TimetableEventData timetableEventData = timetableEventService.createEvent(name, location, start, end, categories);

		event.getHook().sendMessage(timetableEventData.asMarkdown()).setEphemeral(true).queue();
	}

	private LocalDateTime parseDateTime(String time, ModalInteractionEvent event) {
		try {
			return LocalDateTime.parse(time, SpringUtils.DATE_TIME_FMT);
		} catch (DateTimeParseException e) {
			event.reply("Valid format: `" + SpringUtils.DATE_TIME_FMT.toString() + "` for: `" + time + "`").setEphemeral(true).queue();
			return null;
		}
	}

	@Override
	public ItemComponent[] components() {
		return new ItemComponent[] {
				TextInput.create("name", "Name", TextInputStyle.SHORT).build(),
				TextInput.create("location", "Location", TextInputStyle.SHORT).build(),
				TextInput
						.create("start_time", "Start time", TextInputStyle.SHORT)
						.setValue(LocalDateTime.now().format(SpringUtils.DATE_TIME_FMT))
						.build(),
				TextInput
						.create("end_time", "End time", TextInputStyle.SHORT)
						.setValue(LocalDateTime.now().plusHours(2).format(SpringUtils.DATE_TIME_FMT))
						.build(),
				TextInput
						.create("categories", "Categories", TextInputStyle.SHORT)
						.setPlaceholder("Values separated by semicolons: " + Arrays.toString(Category.values()))
						.setRequired(false)
						.build(),
				/*
				 * ((Supplier<StringSelectMenu>) () -> { final StringSelectMenu.Builder builder =
				 * StringSelectMenu.create("categories"); Arrays.stream(Category.values()).forEach(c ->
				 * builder.addOption(c.name().toLowerCase(), PCUtils.enumToString(c))); return builder.build();
				 * }).get()
				 */ };
	}

	@Override
	public String title() {
		return "Create an event";
	}

	@Override
	@Deprecated
	public ItemComponent component() {
		return null;
	}

}
