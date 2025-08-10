package lu.kbra.shared_timetable.server.configs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
public class SecurityContextRepositoryConfiguration {

	@Bean
	public SecurityContextRepository securityContextRepository() {
		return new HttpSessionSecurityContextRepository();
	}

}
