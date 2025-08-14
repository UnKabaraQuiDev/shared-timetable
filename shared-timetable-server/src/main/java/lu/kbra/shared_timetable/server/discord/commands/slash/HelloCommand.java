package lu.kbra.shared_timetable.server.discord.commands.slash;

import org.springframework.stereotype.Component;

import lu.rescue_rush.spring.jda.command.slash.SlashCommandExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component("hello")
public class HelloCommand implements SlashCommandExecutor {

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		if (event.getOption("target") == null) {
			event.reply("Hello!").queue();
		} else {
			event.reply("Hello " + event.getOption("target").getAsUser().getAsMention() + "!").queue();
		}
	}

	@Override
	public OptionData[] options() {
		return new OptionData[] { new OptionData(OptionType.MENTIONABLE, "target", "Target") };
	}

	@Override
	public String description() {
		return "Says hello...";
	}

}
