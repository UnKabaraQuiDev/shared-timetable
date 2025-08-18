package lu.kbra.shared_timetable.client.config;

import java.awt.Color;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lu.kbra.shared_timetable.client.utils.ColorDeserializer;
import lu.kbra.shared_timetable.client.utils.ColorSerializer;

@Configuration
public class JacksonConfig {

	@Bean
	public Module colorDeserializerModule() {
		final SimpleModule module = new SimpleModule();
		module.addDeserializer(Color.class, new ColorDeserializer());
		module.addSerializer(Color.class, new ColorSerializer());
		return module;
	}

}
