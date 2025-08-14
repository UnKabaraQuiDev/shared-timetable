package lu.kbra.shared_timetable.server.configs.discord;

import lu.pcy113.pclib.config.ConfigLoader.ConfigContainer;
import lu.pcy113.pclib.config.ConfigLoader.ConfigProp;

public class DiscordBotConfig implements ConfigContainer {

	@ConfigProp("token")
	public String token;

	public DiscordBotConfig() {
	}

	public DiscordBotConfig(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "DiscordBotConfig [token=" + token + "]";
	}

}
