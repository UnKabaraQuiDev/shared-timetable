package lu.kbra.shared_timetable.client.config;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lu.kbra.shared_timetable.client.frame.AbstractTimetableFrame;

@Configuration
public class AbstractTimetableFrameProvider {

	private static final Logger LOGGER = Logger.getLogger(AbstractTimetableFrameProvider.class.getName());

	@Autowired
	private TimetableFrameConfigProvider timetableFrameConfiguration;

	@Autowired
	@Qualifier("style")
	private String style;

	@Bean
	@Primary
	public AbstractTimetableFrame activeTimetableFrame(Map<String, AbstractTimetableFrame> allFrames) throws IOException {
		final AbstractTimetableFrame frame = allFrames
				.entrySet()
				.stream()
				.filter(s -> s.getKey().equalsIgnoreCase(style))
				.map(Entry::getValue)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unknown style: " + style));

		LOGGER.info("Loaded style: " + style + " (" + frame.getClass().getName() + ")");
		
		frame.setName(style);

		return frame;
	}
}
