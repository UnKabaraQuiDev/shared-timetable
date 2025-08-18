package lu.kbra.shared_timetable.client;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;
import lu.kbra.shared_timetable.client.frame.AbstractTimetableFrame;

@SpringBootApplication
public class STClientMain {

	private static final Logger LOGGER = Logger.getLogger(STClientMain.class.getName());

	public static final File CONFIG_DIR = new File(System.getProperty("user.home"), ".config/shared-timetable-client/");

	public static STClientMain INSTANCE;

	public static String[] PROFILES;
	public static String NAME, VERSION, BUILD, SHARED_VERSION;
	public static boolean DEBUG = false, TEST = false;

	private static Environment environment;

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private AbstractTimetableFrame abstractTimetableFrame;

	public STClientMain() {
		INSTANCE = this;
	}

	private static void extractEnvironmentConsts() {
		if (environment == null) {
			LOGGER.warning("Environment is null before start.");
			return;
		}

		PROFILES = environment.getActiveProfiles();
		DEBUG = Arrays.stream(PROFILES).anyMatch("debug"::equalsIgnoreCase);
		NAME = environment.getProperty("spring.application.name");
		VERSION = environment.getProperty("spring.application.version");
		BUILD = environment.getProperty("spring.application.build");
		SHARED_VERSION = environment.getProperty("spring.application.shared-version");
		TEST = false; // don't move this
		for (StackTraceElement s : Thread.currentThread().getStackTrace()) {
			if (s.getClassName().contains("org.junit.jupiter")) {
				TEST = true;
				break;
			}
		}
		LOGGER.info("Started profile: " + Arrays.toString(PROFILES) + " (" + DEBUG + ", " + TEST + ")");
		LOGGER.info("Application version: " + NAME + " v." + VERSION + "-build." + BUILD + "-shared." + SHARED_VERSION);
	}

	@PostConstruct
	public void setup() {
		if (environment == null) {
			environment = context.getEnvironment();
			extractEnvironmentConsts();
		}
	}

	public static void main(String[] args) {
		if (!CONFIG_DIR.exists()) {
			CONFIG_DIR.mkdirs();
			LOGGER.info("Created config directory: " + CONFIG_DIR.getAbsolutePath());
		} else {
			LOGGER.info("Config directory already exists: " + CONFIG_DIR.getAbsolutePath());
		}

		final SpringApplication app = new SpringApplication(STClientMain.class);
		app.addListeners(new ApplicationPidFileWriter(new File(CONFIG_DIR, "st-client.pid")));

		app.addListeners(new ApplicationListener<ApplicationEnvironmentPreparedEvent>() {
			@Override
			public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
				STClientMain.environment = event.getEnvironment();
				STClientMain.extractEnvironmentConsts();
			}
		});

		app.setHeadless(false);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.setKeepAlive(true);

		app.run(args);
	}

}
