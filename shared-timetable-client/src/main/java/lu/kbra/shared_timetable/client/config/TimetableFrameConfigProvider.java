package lu.kbra.shared_timetable.client.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.shared_timetable.client.STClientMain;
import lu.kbra.shared_timetable.client.frame.classic.ClassicTimetableFrame;

@Configuration
public class TimetableFrameConfigProvider {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Environment environment;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	@Lazy
	private TimetableFrameConfig timetableFrameConfig;

	private final File file = new File(STClientMain.CONFIG_DIR, "frame.json");

	@Bean("style")
	public String style() {
		return environment
				.getProperty("frame.style",
						timetableFrameConfig.getStyle() == null ? applicationContext.getBeanNamesForType(ClassicTimetableFrame.class)[0]
								: timetableFrameConfig.getStyle());
	}

	@Bean
	public TimetableFrameConfig timetableFrameConfig() throws FileNotFoundException, IOException {
		final Logger logger = Logger.getLogger(RemoteConfiguration.class.getName());

		if (!file.exists()) {
			file.getParentFile().mkdirs();
			save(new TimetableFrameConfig());
		}

		return objectMapper.readValue(file, TimetableFrameConfig.class);
	}

	public void save(TimetableFrameConfig config) throws IOException {
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, config);
	}

}
