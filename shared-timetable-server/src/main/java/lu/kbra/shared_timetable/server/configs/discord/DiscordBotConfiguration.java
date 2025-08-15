package lu.kbra.shared_timetable.server.configs.discord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lu.kbra.shared_timetable.server.STServerMain;
import lu.kbra.shared_timetable.server.utils.SpringUtils;
import lu.pcy113.pclib.config.ConfigLoader;

@Configuration
public class DiscordBotConfiguration {

	@Bean
	public DiscordBotConfig discordBotConfig() throws FileNotFoundException, IOException {
		final Logger logger = Logger.getLogger(DiscordBotConfiguration.class.getName());

		if (SpringUtils.extractFile("discord_bot.json", STServerMain.CONFIG_DIR, "discord_bot.json")) {
			logger.info("Extracted default discord_bot.json to " + STServerMain.CONFIG_DIR);
		} else {
			logger.info("Config file discord_bot.json already existed found.");
		}

		final DiscordBotConfig discordBotConfig = ConfigLoader
				.loadFromJSONFile(new DiscordBotConfig(), new File(STServerMain.CONFIG_DIR, "discord_bot.json"));

		return discordBotConfig;
	}

}
