package lu.kbra.shared_timetable.server.discord.modals;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.common.Formats;
import lu.kbra.shared_timetable.common.TimetableEventData;
import lu.kbra.shared_timetable.common.TimetableEventData.TimetableEventCategory;
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

		final Consumer<String> ephemeral = s -> event.getHook().sendMessage(s).setEphemeral(true).queue();
		
		final LocalDateTime start = Formats.parseDateTime(startTime, ephemeral);
		final LocalDateTime end = Formats.parseDateTime(endTime, ephemeral);

		if (start == null || end == null) {
			return;
		}

		if (start.isAfter(end)) {
			event.reply("Start time must be before end time.").setEphemeral(true).queue();
			return;
		}

		final List<TimetableEventCategory> categories = Arrays
				.stream(categoriesStr.split(";"))
				.map(e -> PCUtils.enumValuetoEnum(TimetableEventCategory.class, e))
				.filter(e -> e != null)
				.collect(Collectors.toList());

		event.deferReply().queue();

		final TimetableEventData timetableEventData = timetableEventService.createEvent(name, location, start, end, categories);

		event.getHook().sendMessage(timetableEventData.asMarkdown()).setEphemeral(true).queue();
	}

	@Override
	public ItemComponent[] components() {
		return new ItemComponent[] {
				TextInput.create("name", "Name", TextInputStyle.SHORT).build(),
				TextInput.create("location", "Location", TextInputStyle.SHORT).build(),
				TextInput
						.create("start_time", "Start time", TextInputStyle.SHORT)
						.setValue(LocalDateTime.now().format(Formats.DATE_TIME_FMT))
						.build(),
				TextInput
						.create("end_time", "End time", TextInputStyle.SHORT)
						.setValue(LocalDateTime.now().plusHours(2).format(Formats.DATE_TIME_FMT))
						.build(),
				TextInput
						.create("categories", "Categories", TextInputStyle.SHORT)
						.setPlaceholder("Values separated by semicolons: " + Arrays.toString(TimetableEventCategory.values()))
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
