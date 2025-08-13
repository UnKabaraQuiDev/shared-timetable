package lu.kbra.shared_timetable.server.configs.cors;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfigurationSourceConfiguration {

	private static final Logger LOGGER = Logger.getLogger(CorsConfigurationSourceConfiguration.class.getName());

	@Autowired
	private CorsConfiguration corsConfiguration;

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		LOGGER.info("Creating CORS configuration source");

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);

		return source;
	}

}
