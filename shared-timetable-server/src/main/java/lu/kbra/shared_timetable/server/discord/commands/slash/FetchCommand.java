package lu.kbra.shared_timetable.server.discord.commands.slash;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.server.db.datas.TimetableEventData;
import lu.kbra.shared_timetable.server.services.TimetableEventService;
import lu.rescue_rush.spring.jda.command.slash.SlashCommandExecutor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component("fetch")
public class FetchCommand implements SlashCommandExecutor {

	@Autowired
	private TimetableEventService timetableEventService;

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply().queue();
		final List<TimetableEventData> events = timetableEventService.fetch();

		if (events.isEmpty()) {
			event.getHook().sendMessage("No upcoming events found.").setEphemeral(true).queue();
			return;
		}

		final StringBuilder msg = new StringBuilder();
		final Iterator<TimetableEventData> iterator = events.iterator();
		String block = null;
		while (iterator.hasNext() /* && msg.length() < Message.MAX_CONTENT_LENGTH */
				&& msg.length() + (block = iterator.next().asMarkdown()).length() < Message.MAX_CONTENT_LENGTH) {
			msg.append(block);
		}

		event.getHook().sendMessage(msg.toString()).setEphemeral(true).queue();
	}

	@Override
	public String description() {
		return "Fetches upcoming events from the database.";
	}

}
