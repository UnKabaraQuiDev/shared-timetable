package lu.kbra.shared_timetable.server.discord.commands.slash;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.server.discord.modals.CreateEventModal;
import lu.rescue_rush.spring.jda.command.slash.SlashCommandExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component("create")
public class CreateCommand implements SlashCommandExecutor {

	@Autowired
	private CreateEventModal createEventModal;

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.replyModal(createEventModal.build()).queue();
	}

	@Override
	public String description() {
		return "Fetches upcoming events from the database.";
	}

}
