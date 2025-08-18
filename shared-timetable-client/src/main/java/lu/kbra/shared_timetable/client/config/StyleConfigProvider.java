package lu.kbra.shared_timetable.client.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.shared_timetable.client.STClientMain;
import lu.kbra.shared_timetable.client.frame.AbstractTimetableFrame;

@Configuration
public class StyleConfigProvider {

	private static final Logger LOGGER = Logger.getLogger(StyleConfigProvider.class.getName());

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AbstractTimetableFrame abstractTimetableFrame;

	@Autowired
	private List<StyleConfig> styleConfigs;

	@Autowired
	@Qualifier("style")
	private String style;

	@Primary
	@Bean
	public StyleConfig styleConfig() throws FileNotFoundException, IOException {
		Class<?> clazz = null;

		for (final Field f : abstractTimetableFrame.getClass().getDeclaredFields()) {
			if (!(f.getType() instanceof Class<?>))
				continue;
			final Class<?> type = f.getType();
			if (!StyleConfig.class.isAssignableFrom(type))
				continue;

			if (STClientMain.DEBUG) {
				LOGGER.info("Found candidate config: " + f);
			}

			if (clazz != null) {
				throw new IllegalStateException("Multiple candidate configs found in: " + abstractTimetableFrame.getClass().getName());
			}

			clazz = type;
		}

		if (clazz == null) { // no need to inject anything
			return null;
		}

		final Class<? extends StyleConfig> targetClazz = (Class<? extends StyleConfig>) clazz;

		final List<StyleConfig> candidateConfigs = styleConfigs.stream().filter(s -> targetClazz.isInstance(s)).toList();

		if (candidateConfigs.size() > 1) {
			throw new IllegalStateException("Too many candidate configs found, please narrow your scope. ("
					+ candidateConfigs.stream().map(Object::getClass).map(Class::getName).toList() + ")");
		}
		if (candidateConfigs.size() == 0) {
			throw new IllegalStateException("No candidate configs found matching: " + targetClazz.getName());
		}

		final StyleConfig styleConfig = candidateConfigs.get(0);
		final File file = new File(STClientMain.CONFIG_DIR, style + ".json");

		if (!file.exists()) {
			save(style, styleConfig);
			return styleConfig;
		}

		objectMapper.readerForUpdating(styleConfig).readValue(file);

		return styleConfig;
	}

	public void save(String name, StyleConfig config) throws IOException {
		final File file = new File(STClientMain.CONFIG_DIR, name + ".json");
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, config);
	}

	@EventListener(ContextRefreshedEvent.class)
	public void onRefresh() {
		abstractTimetableFrame.setActive();
	}

}
