package lu.kbra.shared_timetable.server.configs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lu.kbra.shared_timetable.server.STMain;
import lu.kbra.shared_timetable.server.utils.SpringUtils;
import lu.pcy113.pclib.config.ConfigLoader;
import lu.pcy113.pclib.db.DataBaseConnector;
	
@Configuration
public class DatabaseConnectorProvider {

	@Bean
	public DataBaseConnector dataBaseConnector() throws FileNotFoundException, IOException {
		final Logger logger = Logger.getLogger(DatabaseConnectorProvider.class.getName());

		// -- Load config
		if (SpringUtils.extractFile("db_connector.json", STMain.CONFIG_DIR, "db_connector.json")) {
			logger.info("Extracted default db_connector.json to " + STMain.CONFIG_DIR);
		} else {
			logger.info("Config file db_connector.json already existed found. Using default values.");
		}

		DataBaseConnector dbConfig = ConfigLoader.loadFromJSONFile(new DataBaseConnector(), new File(STMain.CONFIG_DIR, "db_connector.json"));

		return dbConfig;
	}

}