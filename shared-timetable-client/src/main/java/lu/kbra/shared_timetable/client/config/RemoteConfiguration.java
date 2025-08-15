package lu.kbra.shared_timetable.client.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.shared_timetable.client.STClientMain;
import lu.kbra.shared_timetable.client.network.RemoteConfig;
import lu.kbra.shared_timetable.client.utils.SpringUtils;

@Configuration
public class RemoteConfiguration {

	@Autowired
	private ObjectMapper objectMapper;

	private final File file = new File(STClientMain.CONFIG_DIR, "remote.json");

	@Bean
	public RemoteConfig remoteConfig() throws FileNotFoundException, IOException {
		final Logger logger = Logger.getLogger(RemoteConfiguration.class.getName());

		/*if (SpringUtils.extractFile("remote.json", file.getParentFile(), file.getName())) {
			logger.info("Extracted default remote.json to " + STClientMain.CONFIG_DIR);
		} else {
			logger.info("Config file remote.json already existed found.");
		}*/
		
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			save(new RemoteConfig());
		}

		return objectMapper.readValue(file, RemoteConfig.class);
	}

	public void save(RemoteConfig config) throws IOException {
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, config);
	}

}
