package lu.kbra.shared_timetable.client.config;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lu.kbra.shared_timetable.client.frame.AbstractTimetableFrame;
import lu.kbra.shared_timetable.client.frame.ClassicTimetableFrame;

@Configuration
public class AbstractTimetableFrameProvider {

	@Autowired
	private TimetableFrameConfiguration timetableFrameConfiguration;

	@Autowired
	private TimetableFrameConfig timetableFrameConfig;

	@Autowired
	private ApplicationContext context;

	@Bean
	@Primary
	public AbstractTimetableFrame activeTimetableFrame(Map<String, AbstractTimetableFrame> allStyles) throws IOException {
		final String style = timetableFrameConfig.getStyle() == null ? context.getBeanNamesForType(ClassicTimetableFrame.class)[0]
				: timetableFrameConfig.getStyle();

		final AbstractTimetableFrame frame = allStyles
				.entrySet()
				.stream()
				.filter(s -> s.getKey().equalsIgnoreCase(style))
				.map(Entry::getValue)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unknown style: " + style));

		frame.setActive();
		frame.validateConfig(timetableFrameConfig);
		timetableFrameConfiguration.save(timetableFrameConfig);

		return frame;
	}
}
