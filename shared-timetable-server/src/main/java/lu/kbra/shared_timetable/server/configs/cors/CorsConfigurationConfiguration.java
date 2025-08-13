package lu.kbra.shared_timetable.server.configs.cors;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class CorsConfigurationConfiguration {

	private static final Logger LOGGER = Logger.getLogger(CorsConfigurationConfiguration.class.getName());

	@Bean
	public CorsConfiguration corsConfiguration() {
		LOGGER.info("Creating CORS configuration");

		final CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("*"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		return config;
	}

}
